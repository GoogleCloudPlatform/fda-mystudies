# My Studies
My Studies is an open-source project to take part in health surveys. ‘My Studies’ is developed using [ResearchKit](https://github.com/ResearchKit/ResearchKit#charts) which is an open-source framework provided by Apple.
My Studies project is FISMA & HIPPA compliant.
# Requirements
My Studies requires Xcode 11 or newer and can be run on iOS 11 and above.
# Backend Server Integration
My Studies fetches all the Studies, Activities, Consent and Resources from the backend, and responses provided by users is stored on the backend.

#### Creating Build Configuration Files
1. To create a build configuration file, open HPHC.xcworkspace, select the “File > New File…” menu item (⌘n), scroll down to the section labeled “Other”, and select the Configuration Settings File template. Next, save it somewhere in your project directory, making sure to add it to your desired targets.
2. Once you’ve created an xcconfig file, you can assign it to one or more build configurations for its associated targets.
3. Go to project info settings.
4. Under build configuration, select the configuration settings file for project(not for any target) you created in Step 1.

#### Registration Server
Registration Server stores user information & user’s status for each study and activity.
Once you have successfully setup Registration Server, add registration 
server URL in your configuration setting file which you created above.

```
REGISTRATION_URL = xx
```

#### WCP Server
WCP Server provides the platform to create study, activities, consent, and Resources.
Once you have successfully setup WCP Server, add wcp server URL in same configuration setting file.

```
WCP_URL = xx

```
Also, add your Username(represented as bundle id) and Password(represented as appToken) in the same configuration file.
This is used inside WCPConfiguration.swift in getDefaultHeaders() method.

```
AUTH_USERNAME = xx

AUTH_PASSWORD = xx

```

#### Response Server
Response Server stores all user’s response to each study activity.
Once you have successfully setup WCP Server, add response server URL in same configuration setting file.

```
RESPONSE_URL = xx

```


# Author
Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.

# License
License Agreement for FDA My Studies
Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is
hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the &quot;Software&quot;), to deal in the Software without restriction, including without
limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
Software, and to permit persons to whom the Software is furnished to do so, subject to the following
conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.
Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
THE SOFTWARE IS PROVIDED &quot;AS IS&quot;, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
# Libraries We Used
[IQKeyboardManagerSwift](https://github.com/hackiftekhar/IQKeyboardManager)
[SlideMenuControllerSwift](https://github.com/dekatotoro/SlideMenuControllerSwift)
[Crashlytics](https://cocoapods.org/pods/Crashlytics)
[SDWebImage](https://github.com/rs/SDWebImage)
[RealmSwift](https://github.com/realm/realm-cocoa)
[CryptoSwift](https://github.com/krzyzanowskim/CryptoSwift)
[ActionSheetPicker-3.0](https://github.com/skywinder/ActionSheetPicker-3.0)
