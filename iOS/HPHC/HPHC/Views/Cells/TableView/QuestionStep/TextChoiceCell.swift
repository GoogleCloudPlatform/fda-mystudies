//
//  TextChoiceCell.swift
//  Survey-Demo
//
//  Created by Tushar on 3/25/19.
//  Copyright © 2019 Tushar. All rights reserved.
//

import UIKit

/// Customer `UITableViewCell` similar to `ORKTextChoiceCell`
class TextChoiceCell: UITableViewCell {

  // MARK:- Outlets
  @IBOutlet weak var titleLbl: UILabel!

  @IBOutlet weak var checkmarkView: UIImageView!
  @IBOutlet weak var detailedTextLbl: UILabel!

  var didSelected: Bool = false {
    didSet {
      if didSelected {
        self.titleLbl.textColor = #colorLiteral(red: 0.2431372549, green: 0.5411764706,
        blue: 0.9921568627, alpha: 1)
        self.checkmarkView.isHidden = false
      } else {
        self.titleLbl.textColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1)
        self.checkmarkView.isHidden = true
      }
    }
  }

  override func setSelected(_ selected: Bool, animated: Bool) {
    super.setSelected(selected, animated: animated)
    self.didSelected = selected
  }

}
