//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import ImageIO
import UIKit

// FIXME: comparison operators with optionals were removed from the Swift Standard Libary.
// Consider refactoring the code to use the non-optional operators.
private func < <T: Comparable>(lhs: T?, rhs: T?) -> Bool {
  switch (lhs, rhs) {
  case let (l?, r?):
    return l < r
  case (nil, _?):
    return true
  default:
    return false
  }
}

extension UIImage {

  public class func gifImageWithData(_ data: Data) -> UIImage? {
    guard let source = CGImageSourceCreateWithData(data as CFData, nil) else {
      return nil
    }

    return UIImage.animatedImageWithSource(source)
  }

  public class func gifImageWithURL(_ gifUrl: String) -> UIImage? {
    guard let bundleURL: URL = URL(string: gifUrl)
    else {
      return nil
    }
    guard let imageData = try? Data(contentsOf: bundleURL) else {
      return nil
    }

    return gifImageWithData(imageData)
  }

  public class func gifImageWithName(_ name: String) -> UIImage? {
    guard
      let bundleURL = Bundle.main
        .url(forResource: name, withExtension: "gif")
    else {
      return nil
    }
    guard let imageData = try? Data(contentsOf: bundleURL) else {
      return nil
    }

    return gifImageWithData(imageData)
  }

  class func delayForImageAtIndex(_ index: Int, source: CGImageSource!) -> Double {
    var delay = 0.0

    let cfProperties = CGImageSourceCopyPropertiesAtIndex(source, index, nil)
    let gifProperties: CFDictionary = unsafeBitCast(
      CFDictionaryGetValue(
        cfProperties,
        Unmanaged.passUnretained(kCGImagePropertyGIFDictionary).toOpaque()
      ),
      to: CFDictionary.self
    )

    var delayObject: AnyObject = unsafeBitCast(
      CFDictionaryGetValue(
        gifProperties,
        Unmanaged.passUnretained(kCGImagePropertyGIFUnclampedDelayTime).toOpaque()
      ),
      to: AnyObject.self
    )
    if delayObject.doubleValue == 0 {
      delayObject = unsafeBitCast(
        CFDictionaryGetValue(
          gifProperties,
          Unmanaged.passUnretained(kCGImagePropertyGIFDelayTime).toOpaque()
        ),
        to: AnyObject.self
      )
    }

    delay = delayObject as! Double

    if delay < 0.0 {
      delay = 0.0
    }

    return delay
  }

  class func gcdForPair(_ a: Int?, _ b: Int?) -> Int {
    var a = a
    var b = b
    if b == nil || a == nil {
      if b != nil {
        return b!
      } else if a != nil {
        return a!
      } else {
        return 0
      }
    }

    if a < b {
      let c = a
      a = b
      b = c
    }

    var rest: Int
    while true {
      rest = a! % b!

      if rest == 0 {
        return b!
      } else {
        a = b
        b = rest
      }
    }
  }

  class func gcdForArray(_ array: [Int]) -> Int {
    if array.isEmpty {
      return 1
    }

    var gcd = array[0]

    for val in array {
      gcd = UIImage.gcdForPair(val, gcd)
    }

    return gcd
  }

  class func animatedImageWithSource(_ source: CGImageSource) -> UIImage? {
    let count = CGImageSourceGetCount(source)
    var images = [CGImage]()
    var delays = [Int]()

    for i in 0..<count {
      if let image = CGImageSourceCreateImageAtIndex(source, i, nil) {
        images.append(image)
      }

      let delaySeconds = UIImage.delayForImageAtIndex(
        Int(i),
        source: source
      )
      delays.append(Int(delaySeconds * 1000.0))  // Seconds to ms
    }

    let duration: Int = {
      var sum = 0

      for val: Int in delays {
        sum += val
      }

      return sum
    }()

    let gcd = gcdForArray(delays)
    var frames = [UIImage]()

    var frame: UIImage
    var frameCount: Int
    for i in 0..<count {
      frame = UIImage(cgImage: images[Int(i)])
      frameCount = Int(delays[Int(i)] / gcd)

      for _ in 0..<frameCount {
        frames.append(frame)
      }
    }

    let animation = UIImage.animatedImage(
      with: frames,
      duration: Double(duration) / 1000.0
    )

    return animation
  }
}
