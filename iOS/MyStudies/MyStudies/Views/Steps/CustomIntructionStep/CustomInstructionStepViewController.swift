//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import ResearchKit
import SafariServices

/// Subclass of `ORKInstructionStep`.
class CustomInstructionStep: ORKInstructionStep {

  required init(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
    fatalError("init(coder:) has not been implemented")
  }

  override init(identifier: String) {
    super.init(identifier: identifier)
  }

}

class CustomInstructionStepViewController: ORKStepViewController {

  // MARK: - Views

  lazy fileprivate var footerView: ORKNavigationContainerView = {
    let footerView = ORKNavigationContainerView()
    footerView.cancelButtonItem = self.cancelButtonItem
    footerView.continueEnabled = true
    footerView.translatesAutoresizingMaskIntoConstraints = false
    return footerView
  }()

  lazy fileprivate var scrollView: UIScrollView = {
    let view = UIScrollView(frame: .zero)
    view.backgroundColor = .clear
    view.frame = self.view.bounds
    view.translatesAutoresizingMaskIntoConstraints = false
    view.autoresizingMask = .flexibleHeight
    view.showsHorizontalScrollIndicator = false
    view.showsVerticalScrollIndicator = false
    view.bounces = true
    return view
  }()

  lazy fileprivate var containerView: UIStackView = {
    let view = UIStackView()
    view.backgroundColor = .clear
    view.translatesAutoresizingMaskIntoConstraints = false
    view.axis = .vertical
    view.alignment = .leading
    view.spacing = 8
    return view
  }()

  /// Label to set  DetailText for `ORKInstructionStep`
  lazy fileprivate var detailTextView: UITextView = {
    let textView = UITextView()
    textView.isEditable = false
    textView.dataDetectorTypes = [.link, .phoneNumber]
    textView.isScrollEnabled = false
    textView.translatesAutoresizingMaskIntoConstraints = false
    textView.backgroundColor = .clear
    textView.textContainerInset = .zero
    textView.textContainer.lineFragmentPadding = 0
    textView.font = UIFont.systemFont(ofSize: 17)
    let step = self.step as? CustomInstructionStep
    let detailText = step?.detailText ?? ""
    let regex = "<[^>]+>"
    if detailText.stringByDecodingHTMLEntities.range(of: regex, options: .regularExpression) == nil {
      textView.text = detailText
    } else {
      textView.attributedText =
        detailText.stringByDecodingHTMLEntities.htmlToAttributedString
    }
    textView.delegate = self
    return textView
  }()

  /// Label to set Text for `ORKInstructionStep`
  lazy fileprivate var textLabel: UILabel = {
    let label = UILabel()
    label.numberOfLines = 0
    let step = self.step as? CustomInstructionStep
    label.text = step?.text
    label.font = UIFont.systemFont(ofSize: 17)
    label.translatesAutoresizingMaskIntoConstraints = false
    return label
  }()

  // MARK: - Initializers
  override init(step: ORKStep?) {
    super.init(step: step)
  }

  override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
    super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
  }

  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
  }

  // MARK: - View Controller Lifecycle

  override func viewDidLoad() {
    super.viewDidLoad()
    DispatchQueue.main.async {
      self.addFooterView()
      self.addTextLabel()
    }
  }

  // MARK: - UI Utils

  /// Adds a footer view.
  private func addFooterView() {

    let continueTitle = hasNextStep() ? LocalizableString.next.localizedString : LocalizableString.done.localizedString
    let continueBtn = UIBarButtonItem(
      title: continueTitle,
      style: .done,
      target: self,
      action: #selector(self.goForward)
    )

    footerView.continueButtonItem = continueBtn
    self.view.addSubview(footerView)

    NSLayoutConstraint.activate([
      footerView.bottomAnchor.constraint(equalTo: self.view.bottomAnchor),
      footerView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
      footerView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor),
    ])
  }

  private func addTextLabel() {

    self.view.addSubview(scrollView)
    self.scrollView.addSubview(containerView)
    self.containerView.addArrangedSubview(textLabel)
    self.containerView.addArrangedSubview(detailTextView)
    NSLayoutConstraint.activate([
      scrollView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
      scrollView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor),
      scrollView.topAnchor.constraint(equalTo: self.view.topAnchor),
      scrollView.widthAnchor.constraint(equalTo: self.view.widthAnchor),
      scrollView.bottomAnchor.constraint(greaterThanOrEqualTo: footerView.topAnchor, constant: -8),
      containerView.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor, constant: 12),
      containerView.widthAnchor.constraint(equalTo: scrollView.widthAnchor, constant: -15),
    ])
    detailTextView.sizeToFit()
    containerView.sizeToFit()
    containerView.layoutIfNeeded()
    scrollView.contentSize = containerView.frame.size
  }

}

// MARK: - UITextViewDelegate Delegate
extension CustomInstructionStepViewController: UITextViewDelegate {
  func textView(
    _ textView: UITextView,
    shouldInteractWith URL: URL,
    in characterRange: NSRange,
    interaction: UITextItemInteraction
  ) -> Bool {
    if ["http", "https"].contains(URL.scheme?.lowercased()) {
      let safariViewController = SFSafariViewController(url: URL)
      present(safariViewController, animated: true, completion: nil)
      return false
    } else if ["mailto"].contains(URL.scheme?.lowercased()) {
      return true
    } else {
      return false
    }
  }

  func textView(
    _ textView: UITextView,
    shouldInteractWith textAttachment: NSTextAttachment,
    in characterRange: NSRange,
    interaction: UITextItemInteraction
  ) -> Bool {
    return true
  }
}
