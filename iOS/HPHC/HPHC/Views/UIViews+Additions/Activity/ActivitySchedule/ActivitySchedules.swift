//
//  ActivitySchedules.swift
//  HPHC
//
//  Created by Tushar Katyal on 28/11/19.
//  Copyright © 2019 BTC. All rights reserved.
//

import UIKit

// MARK:- ActivitySchedules Class
class ActivitySchedules: UIView, UITableViewDelegate, UITableViewDataSource {

  // MARK:- Outlets
  @IBOutlet var tableview: UITableView?

  @IBOutlet var buttonCancel: UIButton!
  @IBOutlet var heightLayoutConstraint: NSLayoutConstraint!

  var activity: Activity!

  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
  }

  class func instanceFromNib(frame: CGRect, activity: Activity) -> ActivitySchedules {
    let view = (
      UINib(nibName: "ActivitySchedules", bundle: nil).instantiate(
        withOwner: nil, options: nil)[0]
        as? ActivitySchedules
    )!
    view.frame = frame
    view.activity = activity
    view.tableview?.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
    view.tableview?.delegate = view
    view.tableview?.dataSource = view
    let height = (activity.activityRuns.count*44) + 104
    let maxViewHeight = Int(UIScreen.main.bounds.size.height - 67)
    view.heightLayoutConstraint.constant = CGFloat(
      (height > maxViewHeight) ? maxViewHeight : height)
    view.layoutIfNeeded()

    return view
  }

  // MARK:- Button Action
  @IBAction func buttonCancelClicked(_: UIButton) {
    self.removeFromSuperview()
  }

  // MARK: Tableview Delegates
  func numberOfSections(in tableView: UITableView) -> Int {
    return 1
  }

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return self.activity.activityRuns.count
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
    cell.textLabel?.font = UIFont(name: "HelveticaNeue-Light", size: 13)
    let activityRun = self.activity.activityRuns[indexPath.row]
    cell.textLabel?.text = ActivitySchedules.formatter.string(from: activityRun.startDate)
      + " - "
      + ActivitySchedules.formatter.string(from: activityRun.endDate)

    if activityRun.runId == self.activity.currentRunId {
      cell.textLabel?.textColor = kBlueColor

    } else if activityRun.runId < self.activity.currentRunId {
      cell.textLabel?.textColor = UIColor.gray
    }
    cell.textLabel?.textAlignment = .center
    return cell
  }

  private static let formatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "hh:mma, MMM dd YYYY"
    return formatter
  }()
}
