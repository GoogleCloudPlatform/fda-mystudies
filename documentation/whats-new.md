<!--
 Copyright 2020-2021 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

> Subscribe to [mystudies-announce@googlegroups.com](https://groups.google.com/g/mystudies-announce/) to receive release notifications and announcements
# Release 2.0.9
* This release fixes the security vulnerability detected with Log4j recently. More information on the vulnerability is here (https://logging.apache.org/log4j/2.x/security.html#CVE-2021-45046). 
* Note: The platform was using a log4j version and logging framework which is not impacted by this vulnerability. However, as a safety measure, the platform is updated with release v2.0.9, to use the latest Log4j version 2.16.0 that was provided by Apache to address this issue.

# Release 2.0.8
* Note: This release requires users to update to new versions of the mobile apps from the app stores.
* Added functionality to manage apps using the Study Builder interface. Read more about setting up apps [here](/deployment/README.md#manage-apps-in-the-study-builder)
* Upgrading to 2.0.8 requires following [these steps](/deployment/README.md#managing-apps-208-upgrade)
* Added the ability to notify app users when an app update is required
* Changed iOS push notifications to use Firebase Cloud Messaging (FCM). See [iOS configuration instructions](/iOS/README.md#configuration-instructions) for details
* Additional code improvements and minor enhancements
* The full list of bugs and features addressed in this release can be viewed [here ](https://github.com/GoogleCloudPlatform/fda-mystudies/milestone/11?closed=1)

# Release 2.0.7
* Minor feature enhancements
* Bug fixes and UI refinements
* Additional code improvements
* The full list of bugs and features addressed in this release can be viewed [`here`](https://github.com/GoogleCloudPlatform/fda-mystudies/milestone/10?closed=1)

# Release 2.0.6
* Added functionality to support study import and export. For upgrading existing deployments, follow [these steps](/deployment#study-import--export-206-upgrade).
* Bug fixes and UI refinements
* Additional code improvements
* The full list of bugs and features addressed in this release can be viewed [`here`](https://github.com/GoogleCloudPlatform/fda-mystudies/milestone/9?closed=1)

# Release 2.0.5
* Changes to Cloud Storage permissions for study resources in new deployments. For upgrading existing deployments, follow [these steps](/deployment/README.md#study-resources-in-cloud-storage-205-upgrade).
* Added support for additional email services
* Bug fixes and UI refinements
* Additional code improvements and minor enhancements
* The full list of bugs and features addressed in this release can be viewed [`here`](https://github.com/GoogleCloudPlatform/fda-mystudies/milestone/8?closed=1)

# Release 2.0.4
* Upgrade to newer - HTTP/2-based Apple Push Notification service (APNs) [#3417](https://github.com/GoogleCloudPlatform/fda-mystudies/issues/3364)
* Fix app container build issue [#3409](https://github.com/GoogleCloudPlatform/fda-mystudies/issues/3409)
* Update deployment instructions for Shared VPC Admin

# Release 2.0.3
* Added [database migration tools](/db-migration/README.md) using Flyway to support upgrades
* Added [API documentation](/documentation/API/README.md) using Swagger libraries
* Bug fixes and UI refinements
* Additional code improvements
* The full list of bugs and features addressed in this release can be viewed [`here`](https://github.com/GoogleCloudPlatform/fda-mystudies/milestone/6?closed=1)

# Release 2.0.2
* Minor enhancements to Study Builder, Participant Manager, Auth Server and mobile apps
* Refined automated deployment process including GKE cluster upgrade
* Bug fixes and UI refinements
* Additional code improvements
* The full list of bugs and features addressed in this release can be viewed [`here`](https://github.com/GoogleCloudPlatform/fda-mystudies/milestone/5?closed=1)

# Release 2.0.1
* Simplified semi-automated deployment
* Published an updated [deployment guide](/deployment/README.md)
* Fixed an issue that was causing flaky build tests in some environments
* Fixed an issue that was preventing a database setup script from running during deployment 

# Release 2.0.0
## What’s changed?
The overall goals, compliance principles and functionality of this FDA MyStudies release are similar to previous releases. Notable changes from version [`2019.10`](https://github.com/PopMedNet-Team/FDA-My-Studies-Mobile-Application-System/tree/2019.10) of FDA MyStudies include:

## Functionality
*   Removed dependencies on the LabKey framework  
*   Added the [`Response datastore`](/response-datastore/) as a platform-agnostic service to handle study response storage and access
*   Added the [`Participant manager`](/participant-manager/) graphical user interface and [`Participant datastore`](/participant-datastore/) backend to manage participant enrollment
*   Added support for [OAuth 2.0](https://oauth.net/2/) and [OIDC](https://openid.net/connect/)
*   Added [templates](/deployment/) for semi-automated deployment
*   Added support for infrastructure-as-code and CICD
*   Upgraded the [`Android`](/Android/) application for compatibility with Android 10
*   Improved exception handling
*   Improved request and data validation
*   Introduced unit tests and test frameworks to the codebase

## Architecture
*   Migrated to a modular [container-based](/deployment/kubernetes/) architecture
*   Refactored and extended `mobileAppStudy-ResponseServer` to [`Response datastore`](/response-datastore/)
*   Refactored `UserReg-WS` to [`Participant datastore`](/participant-datastore/)
*   Refactored `WCP-WS` and `Resources-WCP` to [`Study datastore`](/study-datastore/)
*   Reduced code duplication by extracting [`Common modules`](/common-modules) that are used by all new services
*   Migrated restrictive open-source dependencies to alternatives with permissive licenses 
*   Removed dependencies that require commercial licenses
*   Simplified mobile application calls and moved some functionality server-side to reduce dependencies between services
*   Adjusted data storage based on usage and security requirements (for example, study and participant status storage and calls were moved to[ `Response datastore`](/response-datastore/); enrollment generation logic was moved to [`Participant datastore`](/participant-datastore/))
    
## Security
* Replaced hard-coded credentials with scripts that inject initial users into each component
* Fixed potential cross-site scripting vulnerabilities
* Added query binding to all existing queries to prevent SQL injection
* Enhanced auth throughout codebase:
   * Unified distributed auth implementation into a single [`Auth server`](/auth-server/) (`Study builder` retains built-in auth)
   * Integrated with [Hydra](https://ory.sh/hydra), an OAuth 2.0 and OpenID Connect provider for OAuth 2.0 Access & Refresh token generation and authentication
   * Improved remaining authentication (for example, removed `client_secret` from being transmitted in all calls)
* Created [deployment templates](/deployment/) that support security best practices, such as:
  * Automation of secret generation and handling:
    * Configured secrets to be generated and stored with a [Secret Manager](https://cloud.google.com/secret-manager/docs/overview) instance deployed in an isolated cloud project
    * Configured secret values to be transmitted automatically within the private Kubernetes cluster
  * Implementation of [centralized network control](https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#centralize_network_control):
    * Configured deployment to use a [VPC host project](https://cloud.google.com/vpc/docs/shared-vpc) to manage networks and subnets in a centralized way (enabling network administration to be separated from project administration)
    * Enabled resources in different projects to communicate securely with internal IPs
  * Separation of projects with the security principle of least privilege:
    * Configured dedicated projects for different purposes (secrets, networks, applications, audit) for management by teams with isolated permissions - for example, a centralized network team can administer the network without having access the secrets project)
  * Implementation of [external access limitations](https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#limit-access):
    * Configured databases and VMs to be isolated from the internet with only internal IP addresses ([Private Google Access](https://cloud.google.com/vpc/docs/configure-private-google-access))
    * Established [bastion host](https://cloud.google.com/solutions/connecting-securely#external) for secure on-demand connections to private instances
  * Implementation of DevOps best practices:
    * Configured [Continuous Integration and Continuous Deployment](https://cloud.google.com/solutions/managing-infrastructure-as-code) (CICD) pipelines to automate Cloud resource deployment and minimize direct human access
  * [Delegation of responsibility](https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#groups-and-service-accounts) through groups and service accounts:
    * Configured deployment to assign IAM roles to groups and service accounts so that individuals obtain permissions through groups rather than direct IAM roles

## Usability
*   Made interactions more intuitive for participants using the [`Android`](/Android/) and [`iOS`](/iOS/) mobile applications
*   Updated text for clarity in user interfaces and messages throughout the platform
*   Moved hard-coded values to centralized configuration files to streamline platform customization 
*   Improved code readability to simplify usability and extensibility for developers
*   Added support for unit testing, linter and CICD
*   Added detailed documentation and deployment instructions
    
## Bug fixes
*   Fixed stability and usability bugs throughout the applications and platform

***
<p align="center">Copyright 2020-2021 Google LLC</p>
