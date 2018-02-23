/*
 *    Copyright 2018 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebase.crf;

import org.sagebionetworks.bridge.android.di.ApplicationModule;
import org.sagebionetworks.bridge.android.di.BridgeServiceModule;
import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by liujoshua on 2/22/2018.
 */
@Singleton
@Component(modules = {ApplicationModule.class, BridgeServiceModule.class, CrfS3Module.class})
public interface CrfApplicationComponent extends BridgeManagerProvider {
}
