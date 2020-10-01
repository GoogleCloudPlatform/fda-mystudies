//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Embassy
import EnvoyAmbassador

class DefaultRouter: Router {

  static let registerPath = "/myStudiesUserMgmtWS/register"

  override init() {
    super.init()
    self[DefaultRouter.registerPath] = DelayResponse(JSONResponse(handler: ({ environ -> Any in
      return [
        "userId": "1"
      ]
    })))
  }
}
