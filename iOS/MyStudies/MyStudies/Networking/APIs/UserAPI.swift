//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

struct UserAPI {

  /// Creates a request to reset the `User` password
  /// - Parameters:
  ///   - email: Email Id of the`User `
  ///   - completion: Request status handler
  static func forgotPassword(
    email: String,
    completion: @escaping StatusHandler
  ) {

    let params: JSONDictionary = [
      "email": email,
      "contactEmail": UserManageApps.appDetails?.contactUsEmail ?? "",
      "supportEmail": UserManageApps.appDetails?.supportEmail ?? "",
      "fromEmail": UserManageApps.appDetails?.fromEmail ?? "",
      "appId": AppConfiguration.appID,
    ]
    
    let headers: [String: String] = [
      "contactEmail": UserManageApps.appDetails?.contactUsEmail ?? "",
      "supportEmail": UserManageApps.appDetails?.supportEmail ?? "",
      "fromEmail": UserManageApps.appDetails?.fromEmail ?? ""
    ]
    
    APIService.instance.requestForData(with: AuthRouter.forgotPassword(params: params, headers: headers)) { (_, status, error) in
      completion(status, error)
    }
  }

  /// Creates a request to logout the `User`
  /// - Parameters:
  ///   - completion: Request status handler
  static func logout(
    completion: @escaping StatusHandler
  ) {
    let userID = User.currentUser.userId ?? ""
    APIService.instance.requestForData(with: AuthRouter.logout(userID: userID)) { (_, status, error) in
      if status {
        DBHandler.logout()
      }
      completion(status, error)
    }
  }

  /// Creates a request to change the `User` password
  /// - Parameters:
  ///   - oldPassword: Old password of the `User `
  ///   - newPassword: New password of the `User `
  ///   - completion: Request status handler
  static func changePassword(
    oldPassword: String,
    newPassword: String,
    completion: @escaping StatusHandler
  ) {

    let params = [
      kUserOldPassword: oldPassword,
      kUserNewPassword: newPassword,
    ]
    let userID = User.currentUser.userId ?? ""

    APIService.instance
      .requestForData(with: AuthRouter.changePassword(params: params, userID: userID)) { (_, status, error) in
        if status {
          let user = User.currentUser
          UserDefaults.standard.set(user.userId, forKey: kUserId)
        }
        completion(status, error)
      }
  }

}
