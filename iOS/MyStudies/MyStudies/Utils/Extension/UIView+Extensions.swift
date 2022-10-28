//
//  UIView+Extensions.swift
//  MyStudies
//
//  Created by AMRUTHAPRASAD KK on 04/07/22.
//  Copyright © 2022 Google. All rights reserved.
//

import Foundation
import UIKit

extension UIView {
    @discardableResult
    func alignAllMarginsToSuperview(padding: CGFloat) -> [NSLayoutConstraint] {
        return alignAllMargins(.equal, to: self.superview, padding: padding)
    }
    @discardableResult
    func alignAllMargins(_ relation: NSLayoutConstraint.Relation, to view: UIView!, padding: CGFloat) -> [NSLayoutConstraint] {
        let attributes: [NSLayoutConstraint.Attribute] = [.leadingMargin, .topMargin, .trailingMargin, .bottomMargin]
        return align(attributes, relation, to: view, attributes, padding: padding)
    }
    @discardableResult
    func alignCenterVertical(padding: CGFloat) -> [NSLayoutConstraint] {
        
        guard let superview = self.superview else {
            assertionFailure("Trying to set constraints without first setting superview")
            return []
        }

        let constraint = NSLayoutConstraint(item: self,
                                                   attribute: .centerY,
                                                   relatedBy: .equal,
                                                   toItem: superview,
                                                   attribute: .centerY,
                                                   multiplier: 1.0,
                                                   constant: padding)
        superview.addConstraint(constraint)
        return [constraint]
    }
    @discardableResult
    func alignCenterHorizontal(padding: CGFloat) -> [NSLayoutConstraint] {
        
        guard let superview = self.superview else {
            assertionFailure("Trying to set constraints without first setting superview")
            return []
        }

        let constraint = NSLayoutConstraint(item: self,
                                                   attribute: .centerX,
                                                   relatedBy: .equal,
                                                   toItem: superview,
                                                   attribute: .centerX,
                                                   multiplier: 1.0,
                                                   constant: padding)
        superview.addConstraint(constraint)
        return [constraint]
    }
    @discardableResult
    func align(_ attributes: [NSLayoutConstraint.Attribute]!, _ relation: NSLayoutConstraint.Relation,
               to view:UIView!, _ toAttributes: [NSLayoutConstraint.Attribute]!, padding: CGFloat,
               priority: UILayoutPriority = UILayoutPriority(1000.0)) -> [NSLayoutConstraint] {
        
        guard let superview = self.superview else {
            assertionFailure("Trying to set constraints without first setting superview")
            return []
        }
        
        guard attributes.count > 0 else {
            assertionFailure("'attributes' must contain at least one 'NSLayoutAttribute'")
            return []
        }

        guard attributes.count == toAttributes.count else {
            assertionFailure("The number of 'attributes' must match the number of 'toAttributes'")
            return []
        }
        
        var constraints: [NSLayoutConstraint] = []
        attributes.forEach({
            
            let toAttribute = toAttributes[attributes.firstIndex(of: $0)!]
            let _padding = $0 == .trailing || $0 == .bottom ? -1 * padding : padding
            let constraint = NSLayoutConstraint(item: self,
                               attribute: $0,
                               relatedBy: relation,
                               toItem: view,
                               attribute: toAttribute,
                               multiplier: 1.0,
                               constant: _padding)
            constraint.priority = priority
            constraints.append(constraint)
            superview.addConstraint(constraint)
        })
        
        return constraints
    }
    @discardableResult
    func makeWidth(_ relation: NSLayoutConstraint.Relation, _ width : CGFloat,
                   priority: UILayoutPriority = UILayoutPriority(1000.0)) -> [NSLayoutConstraint] {
        let constraint = NSLayoutConstraint(item: self,
                                              attribute: .width,
                                              relatedBy: relation,
                                              toItem: nil,
                                              attribute: .notAnAttribute,
                                              multiplier: 1.0,
                                              constant: width)
        self.addConstraint(constraint)
        return [constraint]
    }
    
    @discardableResult
    func makeHeight(_ relation: NSLayoutConstraint.Relation, _ height : CGFloat,
                    priority: UILayoutPriority = UILayoutPriority(1000.0)) -> [NSLayoutConstraint] {
        let constraint = NSLayoutConstraint(item: self,
                                              attribute: .height,
                                              relatedBy: relation,
                                              toItem: nil,
                                              attribute: .notAnAttribute,
                                              multiplier: 1.0,
                                              constant: height)
        self.addConstraint(constraint)
        return [constraint]
    }
    @discardableResult
    func alignAbove(view: UIView, padding: CGFloat, priority: UILayoutPriority = UILayoutPriority(1000.0)) -> [NSLayoutConstraint] {
        return align([.bottom], .equal, to: view, [.top], padding: padding, priority: priority)
    }
    @discardableResult
    func alignTop(view: UIView, padding: CGFloat, priority: UILayoutPriority = UILayoutPriority(1000.0)) -> [NSLayoutConstraint] {
        return align([.top], .equal, to: view, [.top], padding: padding, priority: priority)
    }
}
