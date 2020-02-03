//
//  HPHCSettings.swift
//  HPHC
//
//  Created by Surender on 20/06/19.
//  Copyright © 2019 BTC. All rights reserved.
//

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
      handler: { (alert: UIAlertAction!) -> Void in
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
      handler: { (alert: UIAlertAction!) -> Void in
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
      handler: { (alert: UIAlertAction!) -> Void in
        ud.setValue("http://192.168.0.44:8080/StudyMetaData/", forKey: "WCPBaseURL")
        ud.setValue("http://192.168.0.125:8081/labkey/fdahpUserRegWS/", forKey: "URBaseURL")
        ud.setValue(
          "https://hpresp-stage.lkcompliant.net/mobileappstudy-", forKey: "RSBaseURL")
        ud.synchronize()
      })

    let cancelAction = UIAlertAction(
      title: "Cancel(Staging)", style: .cancel,
      handler: { (alert: UIAlertAction!) -> Void in
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
