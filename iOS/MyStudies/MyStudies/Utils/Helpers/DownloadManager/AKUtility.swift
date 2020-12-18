// Copyright (c) 2016, Arpad Goretity https://github.com/H2CO3/HCDownload.git
// Copyright (c) 2016, Muhammad Zeeshan https://github.com/mzeeshanid/MZDownloadManager.git
// All rights reserved.
// License Agreement for FDA MyStudies
// Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
// Copyright 2020 Google LLC
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
// documentation files (the &quot;Software&quot;), to deal in the Software without restriction, including without
// limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
// Software, and to permit persons to whom the Software is furnished to do so, subject to the following
// conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial
// portions of the Software.
// Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
// Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
// THE SOFTWARE IS PROVIDED &quot;AS IS&quot;, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
// PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
// OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.

import UIKit

open class AKUtility: NSObject {

  public static let DownloadCompletedNotif: String = {
    return "com.MZDownloadManager.DownloadCompletedNotif"
  }()

  public static let baseFilePath: String = {
    return (NSHomeDirectory() as NSString).appendingPathComponent("Documents") as String
  }()

  open class func getUniqueFileNameWithPath(_ filePath: NSString) -> NSString {
    let fullFileName: NSString = filePath.lastPathComponent as NSString
    let fileName: NSString = fullFileName.deletingPathExtension as NSString
    let fileExtension: NSString = fullFileName.pathExtension as NSString
    var suggestedFileName: NSString = fileName

    var isUnique: Bool = false
    var fileNumber: Int = 0

    let fileManger: FileManager = FileManager.default

    repeat {
      var fileDocDirectoryPath: NSString?

      if fileExtension.length > 0 {
        fileDocDirectoryPath =
          "\(filePath.deletingLastPathComponent)/\(suggestedFileName).\(fileExtension)"
          as NSString?
      } else {
        fileDocDirectoryPath =
          "\(filePath.deletingLastPathComponent)/\(suggestedFileName)"
          as NSString?
      }

      let isFileAlreadyExists: Bool = fileManger.fileExists(
        atPath: fileDocDirectoryPath! as String
      )

      if isFileAlreadyExists {
        fileNumber += 1
        suggestedFileName = "\(fileName)(\(fileNumber))" as NSString
      } else {
        isUnique = true
        if fileExtension.length > 0 {
          suggestedFileName = "\(suggestedFileName).\(fileExtension)" as NSString
        }
      }

    } while isUnique == false

    return suggestedFileName
  }

  open class func calculateFileSizeInUnit(_ contentLength: Int64) -> Float {
    let dataLength: Float64 = Float64(contentLength)
    if dataLength >= (1024.0 * 1024.0 * 1024.0) {
      return Float(dataLength / (1024.0 * 1024.0 * 1024.0))
    } else if dataLength >= 1024.0 * 1024.0 {
      return Float(dataLength / (1024.0 * 1024.0))
    } else if dataLength >= 1024.0 {
      return Float(dataLength / 1024.0)
    } else {
      return Float(dataLength)
    }
  }

  open class func calculateUnit(_ contentLength: Int64) -> NSString {
    if contentLength >= (1024 * 1024 * 1024) {
      return "GB"
    } else if contentLength >= (1024 * 1024) {
      return "MB"
    } else if contentLength >= 1024 {
      return "KB"
    } else {
      return "Bytes"
    }
  }

  open class func addSkipBackupAttributeToItemAtURL(_ docDirectoryPath: NSString) -> Bool {
    let url: URL = URL(fileURLWithPath: docDirectoryPath as String)
    let fileManager = FileManager.default
    if fileManager.fileExists(atPath: url.path) {

      do {
        try (url as NSURL).setResourceValue(
          NSNumber(value: true as Bool),
          forKey: URLResourceKey.isExcludedFromBackupKey
        )
        return true
      } catch let error as NSError {
        Logger.sharedInstance.error("Error excluding \(url.lastPathComponent) from backup \(error)")
        return false
      }

    } else {
      return false
    }
  }

  open class func getFreeDiskspace() -> Int64? {
    let documentDirectoryPath = NSSearchPathForDirectoriesInDomains(
      .documentDirectory,
      .userDomainMask,
      true
    )
    let systemAttributes: AnyObject?
    do {
      systemAttributes =
        try FileManager.default.attributesOfFileSystem(
          forPath: documentDirectoryPath.last!
        ) as AnyObject?
      let freeSize = systemAttributes?[FileAttributeKey.systemFreeSize] as? NSNumber
      return freeSize?.int64Value
    } catch let error as NSError {
      Logger.sharedInstance.error(
        "Error Obtaining System Memory Info: Domain = \(error.domain), Code = \(error.code)"
      )
      return nil
    }
  }

  @discardableResult
  static func moveFileToDocuments(
    fromUrl url: URL,
    toDirectory directory: String?,
    withName name: String
  ) -> (Bool, Error?, URL?) {
    var newUrl: URL
    if let directory = directory {
      let directoryCreationResult = self.createDocumentsDirectoryIfNotExists(withName: directory)
      guard directoryCreationResult.0 else {
        return (false, directoryCreationResult.1, nil)
      }
      newUrl = self.documentsDirectoryPath.appendingPathComponent(directory).appendingPathComponent(name)
    } else {
      newUrl = self.documentsDirectoryPath.appendingPathComponent(name)
    }
    do {
      try FileManager.default.moveItem(at: url, to: newUrl)
      return (true, nil, newUrl)
    } catch {
      return (false, error, nil)
    }
  }

  static var cacheDirectoryPath: URL {
    let cachePath = NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true)[0]
    return URL(fileURLWithPath: cachePath)
  }

  static var documentsDirectoryPath: URL {
    let documentPath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
    return URL(fileURLWithPath: documentPath)
  }

  /// Creates a new directory.
  /// - Parameter name: Name for the directory.
  /// - Returns: A `Boolean` indicating the status of directory and an Optional `Error`.
  static func createDocumentsDirectoryIfNotExists(withName name: String) -> (Bool, Error?) {
    let directoryUrl = self.documentsDirectoryPath.appendingPathComponent(name)
    if FileManager.default.fileExists(atPath: directoryUrl.path) {
      return (true, nil)
    }
    do {
      try FileManager.default.createDirectory(at: directoryUrl, withIntermediateDirectories: true, attributes: nil)
      return (true, nil)
    } catch {
      return (false, error)
    }
  }

  /// Verifies the file available in the directory.
  /// - Parameters:
  ///   - directory: Directory link.
  ///   - filename: Name of the file.
  /// - Returns: A `Boolean` indicating the availablity of the file and an Optional file `URL`.
  static func checkFileExistAt(directory: String, filename: String) -> (exist: Bool, filepath: URL?) {
    let directoryUrl = self.documentsDirectoryPath.appendingPathComponent(directory).appendingPathComponent(filename)
    if FileManager.default.fileExists(atPath: directoryUrl.path) {
      return (true, directoryUrl)
    } else {
      return (false, nil)
    }
  }

  /// Deletes the directory under Documents.
  /// - Parameters:
  ///   - directory: Directory name.
  static func deleteDirectoryFromDocuments(name: String) {
    let directoryUrl = self.documentsDirectoryPath.appendingPathComponent(name)
    try? FileManager.default.removeItem(at: directoryUrl)
  }

  static func deleteFile(from path: URL) {
    try? FileManager.default.removeItem(at: path)
  }
}
