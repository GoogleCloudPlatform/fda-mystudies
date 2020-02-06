//
//  HPHCSettings.swift
//  HPHC
//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation
import UIKit

class HPHCSettings {

  func setupDefaultURLs() {
    let ud = UserDefaults.standard
    ud.setValue("https://hpwcp-stage.lkcompliant.net/StudyMetaData/", forKey: "WCPBaseURL")
    ud.setValue("https://hpreg-stage.lkcompliant.net/fdahpUserRegWS/", forKey: "URBaseURL")
    ud.setValue("https://hpresp-stage.lkcompliant.net/mobileappstudy-", forKey: "RSBaseURL")
    ud.synchronize()
  }

  func showSettings(_ presenter: UIViewController, handler: (() -> Void)) {

    let alertConrtolelr = UIAlertController.init(
      title: "Change server to", message: nil, preferredStyle: .actionSheet)

    let ud = UserDefaults.standard

    let production = UIAlertAction(
      title: "Production", style: .default,
      handler: { (_: UIAlertAction!) -> Void in
        ud.setValue(
          "https://hpwcp-stage.lkcompliant.net/StudyMetaData/", forKey: "WCPBaseURL")
        ud.setValue(
          "https://hpreg-stage.lkcompliant.net/fdahpUserRegWS/", forKey: "URBaseURL")
        ud.setValue(
          "https://hpresp-stage.lkcompliant.net/mobileappstudy-", forKey: "RSBaseURL")
        ud.synchronize()
      })

    let stagging = UIAlertAction(
      title: "Stagging", style: .default,
      handler: { (_: UIAlertAction!) -> Void in
        ud.setValue(
          "https://hpwcp-stage.lkcompliant.net/StudyMetaData/", forKey: "WCPBaseURL")
        ud.setValue(
          "https://hpreg-stage.lkcompliant.net/fdahpUserRegWS/", forKey: "URBaseURL")
        ud.setValue(
          "https://hpresp-stage.lkcompliant.net/mobileappstudy-", forKey: "RSBaseURL")
        ud.synchronize()
      })

    let local = UIAlertAction(
      title: "Local", style: .default,
      handler: { (_: UIAlertAction!) -> Void in
        ud.setValue("http://192.168.0.44:8080/StudyMetaData/", forKey: "WCPBaseURL")
        ud.setValue("http://192.168.0.125:8081/labkey/fdahpUserRegWS/", forKey: "URBaseURL")
        ud.setValue(
          "https://hpresp-stage.lkcompliant.net/mobileappstudy-", forKey: "RSBaseURL")
        ud.synchronize()
      })

    let cancelAction = UIAlertAction(
      title: "Cancel(Staging)", style: .cancel,
      handler: { (_: UIAlertAction!) -> Void in
        ud.setValue(
          "https://hpwcp-stage.lkcompliant.net/StudyMetaData/", forKey: "WCPBaseURL")
        ud.setValue(
          "https://hpreg-stage.lkcompliant.net/fdahpUserRegWS/", forKey: "URBaseURL")
        ud.setValue(
          "https://hpresp-stage.lkcompliant.net/mobileappstudy-", forKey: "RSBaseURL")
        ud.synchronize()
      })

    alertConrtolelr.addAction(production)
    alertConrtolelr.addAction(stagging)
    alertConrtolelr.addAction(local)
    alertConrtolelr.addAction(cancelAction)
    presenter.present(
      alertConrtolelr, animated: true,
      completion: {
        ud.synchronize()
      })

  }

}
