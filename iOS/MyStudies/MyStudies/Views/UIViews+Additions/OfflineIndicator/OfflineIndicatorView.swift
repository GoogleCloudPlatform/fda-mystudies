//
//  OfflineIndicatorView.swift
//  MyStudies
//
//  Created by AMRUTHAPRASAD KK on 26/06/22.
//  Copyright © 2022 Google. All rights reserved.
//


import UIKit

class OfflineIndicatorView: UIView {

    @IBOutlet var view: UIView!
    @IBOutlet var indicatorView: UIView!
    @IBOutlet weak var closeButton: UIButton!
    @IBOutlet weak var detailedMessage: UILabel!
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    private func commonInit() {
        Bundle.main.loadNibNamed("OfflineIndicatorView", owner: self, options: nil)
        addSubview(view)
        view.frame = self.bounds
        view.autoresizingMask = [.flexibleHeight, .flexibleWidth]
        view.translatesAutoresizingMaskIntoConstraints = false
        view.alignAllMarginsToSuperview(padding: 0)
        indicatorView.alignAllMarginsToSuperview(padding: 0)
    }
    func isOffline(status: Bool) {
        if status == true {
            detailedMessage.isHidden = false
        } else {
            detailedMessage.isHidden = true
        }
    }
    @IBAction func closeButtonPressed(_ sender: Any) {
        self.isHidden = true
        self.removeFromSuperview()
    }
}
