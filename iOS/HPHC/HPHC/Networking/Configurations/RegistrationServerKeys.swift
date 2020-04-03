//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

enum RegistrationServerAPIKey {
  static var apiKey: String {
    return (try? Configuration.value(for: "REGISTRATIONSERVER_API_KEY")) ?? ""
  }
}

enum RegistrationServerSecretKey {
  static var secretKey: String {
    return (try? Configuration.value(for: "REGISTRATIONSERVER_API_SECERET")) ?? ""
  }
}
