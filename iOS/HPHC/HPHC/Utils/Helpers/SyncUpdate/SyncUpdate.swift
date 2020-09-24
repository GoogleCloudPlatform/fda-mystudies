// License Agreement for FDA MyStudies
// Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
// Copyright 2020 Google LLC
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
// documentation files (the &quot;Software&quot;), to deal in the Software without restriction, including without
// limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
// Software, and to permit persons to whom the Software is furnished to do so, subject to the following
// conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial
// portions of the Software.
// Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
// Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
// THE SOFTWARE IS PROVIDED &quot;AS IS&quot;, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
// PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
// OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.

import Foundation
import RealmSwift

class SyncUpdate {

  enum ServerType: String {
    case response, enrollment
  }

  static var sharedInstance = SyncUpdate()
  private init() {}

  private var lastSyncedObject: DBDataOfflineSync?

  @objc func updateData(isReachable: Bool) {
    if isReachable {
      // Start syncing Data.
      DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
        self.syncDataToServer()
      }
    }
  }

  /// SyncData to server, called to sync responses stored in offline mode to server.
  func syncDataToServer() {

    guard let realm = DBHandler.getRealmObject(),
      let toBeSyncedData = realm.objects(DBDataOfflineSync.self).sorted(
        byKeyPath: "date",
        ascending: true
      ).first
    else { return }
    lastSyncedObject = toBeSyncedData
    // Request params
    var params: [String: Any]?

    if let requestParams = toBeSyncedData.requestParams {
      params =
        try? JSONSerialization.jsonObject(
          with: requestParams,
          options: []
        ) as? JSONDictionary
    }

    // Header params
    var headers: [String: String]?

    if let requestHeaders = toBeSyncedData.headerParams {
      headers =
        try? JSONSerialization.jsonObject(
          with: requestHeaders,
          options: []
        ) as? [String: String]
    }

    let methodString = toBeSyncedData.method
    let server = toBeSyncedData.server

    if server == SyncUpdate.ServerType.enrollment.rawValue {
      let methodName = methodString?.components(separatedBy: ".").first ?? ""
      let enrollmentMethod = EnrollmentMethods(rawValue: methodName)
      if let method = enrollmentMethod?.method {
        EnrollServices().syncOfflineSavedData(
          method: method,
          params: params,
          headers: headers,
          delegate: self
        )
      }
    } else if server == SyncUpdate.ServerType.response.rawValue {
      let responseMethod: ResponseMethods?
      if let method = ResponseMethods(rawValue: methodString ?? "") {
        responseMethod = method
      } else {
        let methodName = methodString?.components(separatedBy: "/").last ?? ""
        responseMethod = ResponseMethods(rawValue: methodName)
      }
      if let method = responseMethod?.method {
        ResponseServices().syncOfflineSavedData(
          method: method,
          params: params,
          headers: headers,
          delegate: self
        )
      }
    }
  }

  private func deleteSyncedObject(completion: @escaping () -> Void) {
    guard let realm = DBHandler.getRealmObject(),
      let syncObj = lastSyncedObject
    else {
      completion()
      return
    }
    // Delete Synced object from DB
    try? realm.write {
      realm.delete(syncObj)
      completion()
    }
  }
}

// MARK: - Webservices Delegates
extension SyncUpdate: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {}

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    deleteSyncedObject {
      DispatchQueue.main.async {
        self.syncDataToServer()
      }
    }
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {}

}
