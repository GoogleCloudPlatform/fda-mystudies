////////////////////////////////////////////////////////////////////////////
//
// Copyright 2014 Realm Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

#import "RLMObjectStore.h"

#import "RLMAccessor.hpp"
#import "RLMArray_Private.hpp"
#import "RLMListBase.h"
#import "RLMObservation.hpp"
#import "RLMObject_Private.hpp"
#import "RLMObjectSchema_Private.hpp"
#import "RLMOptionalBase.h"
#import "RLMProperty_Private.h"
#import "RLMQueryUtil.hpp"
#import "RLMRealm_Private.hpp"
#import "RLMSchema_Private.h"
#import "RLMSwiftSupport.h"
#import "RLMUtil.hpp"

#import "object_store.hpp"
#import "results.hpp"
#import "shared_realm.hpp"

#import <realm/group.hpp>

#import <objc/message.h>

using namespace realm;

static_assert(RLMUpdatePolicyError == static_cast<int>(CreatePolicy::ForceCreate), "");
static_assert(RLMUpdatePolicyUpdateAll == static_cast<int>(CreatePolicy::UpdateAll), "");
static_assert(RLMUpdatePolicyUpdateChanged == static_cast<int>(CreatePolicy::UpdateModified), "");

void RLMRealmCreateAccessors(RLMSchema *schema) {
    const size_t bufferSize = sizeof("RLM:Managed  ") // includes null terminator
                            + std::numeric_limits<unsigned long long>::digits10
                            + realm::Group::max_table_name_length;

    char className[bufferSize] = "RLM:Managed ";
    char *const start = className + strlen(className);

    for (RLMObjectSchema *objectSchema in schema.objectSchema) {
        if (objectSchema.accessorClass != objectSchema.objectClass) {
            continue;
        }

        static unsigned long long count = 0;
        sprintf(start, "%llu %s", count++, objectSchema.className.UTF8String);
        objectSchema.accessorClass = RLMManagedAccessorClassForObjectClass(objectSchema.objectClass, objectSchema, className);
    }
}

static inline void RLMVerifyRealmRead(__unsafe_unretained RLMRealm *const realm) {
    if (!realm) {
//        @throw RLMException(@"Realm must not be nil");
    }
    [realm verifyThread];
    if (realm->_realm->is_closed()) {
        // This message may seem overly specific, but frozen Realms are currently
        // the only ones which we outright close.
//        @throw RLMException(@"Cannot read from a frozen Realm which has been invalidated.");
    }
}

static inline void RLMVerifyInWriteTransaction(__unsafe_unretained RLMRealm *const realm) {
    RLMVerifyRealmRead(realm);
    // if realm is not writable throw
    if (!realm.inWriteTransaction) {
//        @throw RLMException(@"Can only add, remove, or create objects in a Realm in a write transaction - call beginWriteTransaction on an RLMRealm instance first.");
    }
}

void RLMInitializeSwiftAccessorGenerics(__unsafe_unretained RLMObjectBase *const object) {
    if (!object || !object->_row || !object->_objectSchema->_isSwiftClass) {
        return;
    }
    if (![object isKindOfClass:object->_objectSchema.objectClass]) {
        // It can be a different class if it's a dynamic object, and those don't
        // require any init here (and would crash since they don't have the ivars)
        return;
    }

    for (RLMProperty *prop in object->_objectSchema.swiftGenericProperties) {
        if (prop.type == RLMPropertyTypeLinkingObjects) {
            [prop.swiftAccessor initializeObject:(char *)(__bridge void *)object + ivar_getOffset(prop.swiftIvar)
                                          parent:object property:prop];
        }
        else if (prop.array) {
            id ivar = object_getIvar(object, prop.swiftIvar);
            RLMArray *array = [[RLMManagedArray alloc] initWithParent:object property:prop];
            [ivar set_rlmArray:array];
        }
        else {
            id ivar = object_getIvar(object, prop.swiftIvar);
            RLMInitializeManagedOptional(ivar, object, prop);
        }
    }
}

void RLMVerifyHasPrimaryKey(Class cls) {
    RLMObjectSchema *schema = [cls sharedSchema];
    if (!schema.primaryKeyProperty) {
        NSString *reason = [NSString stringWithFormat:@"'%@' does not have a primary key and can not be updated", schema.className];
        @throw [NSException exceptionWithName:@"RLMException" reason:reason userInfo:nil];
    }
}

void RLMAddObjectToRealm(__unsafe_unretained RLMObjectBase *const object,
                         __unsafe_unretained RLMRealm *const realm,
                         RLMUpdatePolicy updatePolicy) {
    RLMVerifyInWriteTransaction(realm);

    // verify that object is unmanaged
    if (object.invalidated) {
        @throw RLMException(@"Adding a deleted or invalidated object to a Realm is not permitted");
    }
    if (object->_realm) {
        if (object->_realm == realm) {
            // Adding an object to the Realm it's already manged by is a no-op
            return;
        }
        // for differing realms users must explicitly create the object in the second realm
        @throw RLMException(@"Object is already managed by another Realm. Use create instead to copy it into this Realm.");
    }
    if (object->_observationInfo && object->_observationInfo->hasObservers()) {
        @throw RLMException(@"Cannot add an object with observers to a Realm");
    }

    auto& info = realm->_info[object->_objectSchema.className];
    RLMAccessorContext c{info, true};
    object->_info = &info;
    object->_realm = realm;
    object->_objectSchema = info.rlmObjectSchema;
    try {
        realm::Object::create(c, realm->_realm, *info.objectSchema, (id)object,
                              static_cast<CreatePolicy>(updatePolicy),
                              {}, &object->_row);
    }
    catch (std::exception const& e) {
        @throw RLMException(e);
    }
    object_setClass(object, info.rlmObjectSchema.accessorClass);
    RLMInitializeSwiftAccessorGenerics(object);
}

RLMObjectBase *RLMCreateObjectInRealmWithValue(RLMRealm *realm, NSString *className,
                                               id value, RLMUpdatePolicy updatePolicy) {
    RLMVerifyInWriteTransaction(realm);

    if (updatePolicy != RLMUpdatePolicyError && RLMIsObjectSubclass([value class])) {
        RLMObjectBase *obj = value;
        if (obj->_realm == realm && [obj->_objectSchema.className isEqualToString:className]) {
            // This is a no-op if value is an RLMObject of the same type already backed by the target realm.
            return value;
        }
    }

    if (!value || value == NSNull.null) {
        @throw RLMException(@"Must provide a non-nil value.");
    }

    auto& info = realm->_info[className];
    if ([value isKindOfClass:[NSArray class]] && [value count] > info.objectSchema->persisted_properties.size()) {
        @throw RLMException(@"Invalid array input: more values (%llu) than properties (%llu).",
                            (unsigned long long)[value count],
                            (unsigned long long)info.objectSchema->persisted_properties.size());
    }

    RLMAccessorContext c{info, false};
    RLMObjectBase *object = RLMCreateManagedAccessor(info.rlmObjectSchema.accessorClass, &info);
    try {
        object->_row = realm::Object::create(c, realm->_realm, *info.objectSchema, (id)value,
                                             static_cast<CreatePolicy>(updatePolicy)).obj();
    }
    catch (std::exception const& e) {
        @throw RLMException(e);
    }
    RLMInitializeSwiftAccessorGenerics(object);
    return object;
}

void RLMDeleteObjectFromRealm(__unsafe_unretained RLMObjectBase *const object,
                              __unsafe_unretained RLMRealm *const realm) {
    if (realm != object->_realm) {
//        @throw RLMException(@"Can only delete an object from the Realm it belongs to.");
    
    } else {

    RLMVerifyInWriteTransaction(object->_realm);

    // move last row to row we are deleting
    if (object->_row.is_valid()) {
        RLMTrackDeletions(realm, ^{
            object->_row.remove();
        });
    }

    // set realm to nil
    object->_realm = nil;
    }
}

void RLMDeleteAllObjectsFromRealm(RLMRealm *realm) {
    RLMVerifyInWriteTransaction(realm);

    // clear table for each object schema
    for (auto& info : realm->_info) {
        RLMClearTable(info.second);
    }
}

RLMResults *RLMGetObjects(__unsafe_unretained RLMRealm *const realm,
                          NSString *objectClassName,
                          NSPredicate *predicate) {
    RLMVerifyRealmRead(realm);

    // create view from table and predicate
    RLMClassInfo& info = realm->_info[objectClassName];
    if (!info.table()) {
        // read-only realms may be missing tables since we can't add any
        // missing ones on init
        return [RLMResults resultsWithObjectInfo:info results:{}];
    }

    if (predicate) {
        realm::Query query = RLMPredicateToQuery(predicate, info.rlmObjectSchema, realm.schema, realm.group);
        return [RLMResults resultsWithObjectInfo:info
                                         results:realm::Results(realm->_realm, std::move(query))];
    }

    return [RLMResults resultsWithObjectInfo:info
                                     results:realm::Results(realm->_realm, info.table())];
}

id RLMGetObject(RLMRealm *realm, NSString *objectClassName, id key) {
    RLMVerifyRealmRead(realm);

    auto& info = realm->_info[objectClassName];
    if (RLMProperty *prop = info.propertyForPrimaryKey()) {
        RLMValidateValueForProperty(key, info.rlmObjectSchema, prop);
    }
    try {
        RLMAccessorContext c{info};
        auto obj = realm::Object::get_for_primary_key(c, realm->_realm, *info.objectSchema,
                                                      key ?: NSNull.null);
        if (!obj.is_valid())
            return nil;
        return RLMCreateObjectAccessor(info, obj.obj());
    }
    catch (std::exception const& e) {
        @throw RLMException(e);
    }
}

RLMObjectBase *RLMCreateObjectAccessor(RLMClassInfo& info, int64_t key) {
    return RLMCreateObjectAccessor(info, info.table()->get_object(realm::ObjKey(key)));
}

// Create accessor and register with realm
RLMObjectBase *RLMCreateObjectAccessor(RLMClassInfo& info, realm::Obj&& obj) {
    RLMObjectBase *accessor = RLMCreateManagedAccessor(info.rlmObjectSchema.accessorClass, &info);
    accessor->_row = std::move(obj);
    RLMInitializeSwiftAccessorGenerics(accessor);
    return accessor;
}
