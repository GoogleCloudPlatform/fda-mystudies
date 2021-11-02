# Logging and Monitoring

This page describes the process for configuring logging and monitoring of an FDA MyStudies deployment.

Cloud Logging can be used to configure a log viewer for viewing, querying, and analysis of logs. Log sinks can be set up to manage log retention policies. Cloud Logging can also be used to create log-based metrics and alerts.

Cloud Monitoring is used for setting up charts, dashboards, alerts, and notifications. It also can be used for Service Level Objective (SLO) monitoring and uptime checks.

- [1. Cloud Logging](#1-cloud-logging)
- [2. Cloud Monitoring](#2-cloud-monitoring)
- [3. Configuring Alerts and Notifications](#3-configuring-alerts-and-notifications)

## 1.  Cloud Logging

Cloud Logging API can be used for real-time log management analysis by configuring the following components:

*   Logs viewer           
*   Logs alerting
*   Logs Router, Retention & Storage 
*   Log buckets and views
*   Error Reporting
*   Audit Logging

### Logs viewer 

Logs Viewer and Logs Explorer can be used to filter and analyze logs during an incident.

### Logs alerting

Logs-based alerting is integrated with [Cloud Monitoring](https://cloud.google.com/monitoring) and can be used to set alerts on the logs events.

### Logs Router, Retention & Storage 

Cloud Logging uses two predefined log sinks for each GCP Project `_Required` and `_Default`. All logs that are generated in GCP projects are automatically processed through these two log sinks and then are stored in the correspondingly named log buckets. Cloud Logging doesn't charge to route logs, but destination charges may apply.

Bucket Configurations:

| Name | Description | Monthly | Retention | Type |
|------|-------------|---------|-----------|------|
| _Default | Default bucket | Billable | 30 days | global |
| _Required | Audit bucket | Not billed | 400 days | global |

### Log buckets and views

Logs Storage is used to create logs buckets and to monitor usage and retention.

### Error Reporting

Error Reporting API can be used to aggregate and display errors produced in the running cloud services. The centralized error management interface can be used to find errors during the deployments and infrastructure update process.

### Cloud Audit Logs

Cloud Audit Logs can be used to help meet security, auditing, and compliance needs by maintaining audit trails such as admin activity, data access, and system events. These are configured to be stored in coldline storage for longer-term storage.

| Name | Location  | Type | Life  Cycle |
|------|-------------|---------|-----------|
| `<PREFIX>-<ENV>-7yr-audit-logs` | Region | Coldline | 2555+ days since object was update |

## 2.  Cloud Monitoring

The Cloud Monitoring services will be used to monitor the project resources. Different monitoring and alerts are set up for the various projects involved in a deployment, including:

*   Apps - the project storing container images and running the compute (k8s) resources
*   Audit - the project storing audit logs from the platform and applications
*   Data - the project with the CloudSQL databases
*   Devops - the project for the CI/CD pipeline
*   Firebase - the project used for storing study response data
*   Networks - the project for the network policies and firewalls
*   Secrets - the project for managing the deployment secrets such as client IDs and credentials

### Monitoring Methods

The types of monitoring used include:

*   Dashboard Monitoring
*   Metrics based alerts with email notifications
*   Logs based alerts with email notifications
*   Incident monitoring dashboard

### Apps Project

Monitoring in the Apps project includes Cloud Storage, Kubernetes Engine (GKE), Compute Engine resources, Load Balancers, and Cloud Pub/Sub. There are metrics based alerts for GKE and SSL certificates. Logs based alerts include Cloud Build and GKE errors. This project also uses uptime and health checks for the GKE deployments. Instructions for creating the individual alerts can be found in the section [Configuring Alerts and Notifications](#3-configuring-alerts-and-notifications).

#### Monitoring Dashboards

|  Dashboard | Resources to be Monitored  |
|-------|-------|
| Cloud Storage | Requests, Network Traffic Sent, Network Traffic Received, Object Count, Object Size |
| GKE  | Alerts, Container restarts, Error logs, CPU utilization, Memory utilization, Disk utilization |
| VM Instances ( GKE Node VM’s) | CPU , Memory, Disk Utilization, Network Traffic |
| External HTTP(S) Load Balancers | 5xx Error Ratio, Backend Latency (95th Percentile), Total Latency (95th Percentile), Forwarding Rules, Health Status |
| Cloud Pub/Sub | Publish Message Operations, Average Message Size |

#### Metrics based alerts:

| Alert | Threshold | Description |
|-------|-------|-----------|
| SSL certificate expiring soon | `1 Month` | Expiration date of SSL certificates |
| Kubernetes Container - CPU utilization | `>= 70%` | CPU utilization percentage |
| Kubernetes Container - Memory usage | `>= 70%` | Memory usage percentage |
| Kubernetes Container - Restart count | `20` | Container restarting more than a specified number of times |
| k8s_pod - Volume utilization | `>= 70%` | Volume utilization of the kubernetes pod |
| k8s_pod - autoscaler_panic_mode | `1` or `0` | Value of `1` represents autoscaler failing |
| Kubernetes Node - Memory usage | `>= 70%` | Memory usage percentage of kubernetes node |
| Kubernetes Node - node_network_up | `1` or `0` | Value of `0` represents the node network being down |
| Kubernetes Node - CPU allocatable utilization | `>= 70%` | CPU utilization percentage of kubernetes node  |
| VM - CPU utilization | `>= 70%` | CPU utilization percentage of underlying VM |
| VM - disk utilization too high | `>= 70%` | Disk utilization percentage of underlying VM |
| VM - p95 networking latency too high | `< 1s` | Network latency of underlying VM |
| VM - memory utilization too high | `>= 70%` | Memory utilization percentage of underlying VM |

#### Logs based alert

| Alert | Metrics Query | Description |
|-------|-------|-------|
| Cloud Build failure | ```resource.type="build", logName=("projects/<projectname>/logs/cloudaudit.googleapis.com%2f Activity" OR "projects/<projectname>/logs/cloudaudit.googleapis.com%2Fdata_access" OR "projects/<projectname>/logs/cloudbuild"), severity>=ERROR, ``` | Trigger an alert when Cloud Build experiences a failure or error |
| GKE container failure | ```resource.type="k8s_container", resource.labels.cluster_name="<name of the cluster>", resource.labels.namespace_name="default", severity=(EMERGENCY OR ALERT OR CRITICAL OR ERROR), NOT textPayload:New connection, NOT textPayload:Client closed, ``` | Trigger an alert when a GKE container has a log message with severity of ERROR, CRITICAL, ALERT, or EMERGENCY |

#### Uptime and health checks

Uptime checks can be created for the following services which will test accessing the services from various global locations. Use the host name and path below Instructions when configuring the health checks following the instructions in the section [Configuring Alerts and Notifications](#3-configuring-alerts-and-notifications).

|  Application  | Host Name | Path |
|-------|-------|-------|
| Auth Server | `participants.<prefix>-<env>.<domain>` | `/auth-server/healthCheck` |
| Participant Consent Datastore | `participants.<prefix>-<env>.<domain>` | `/participant-consent-datastore/healthCheck` |
| Participant Enroll Datastore | `participants.<prefix>-<env>.<domain>` | `/participant-enroll-datastore/healthCheck` |
| Participant Manager | `participants.<prefix>-<env>.<domain>` | `/participant-manager/index.html` |
| Participant Manager Datastore | `participants.<prefix>-<env>.<domain>` | `/participant-manager-datastore/healthCheck` |
| Participant User Datastore | `participants.<prefix>-<env>.<domain>` | `/participant-user-datastore/healthCheck` |
| Response Datastore | `participants.<prefix>-<env>.<domain>` | `/response-datastore/healthCheck` |
| Study Datastore | `studies.<prefix>-<env>.<domain> ` | `/study-datastore/healthCheck` |
| Study Builder | `studies.<prefix>-<env>.<domain> ` | `/studybuilder/healthCheck.do` |

### Audit Project

Monitoring in the Audit project includes Cloud Storage, and BigQuery. There are metrics based alerts for Cloud Storage and BigQuery. Optional logs based alerts can include the amount of logs ingested. Instructions for creating the individual alerts can be found in the section [Configuring Alerts and Notifications](#3-configuring-alerts-and-notifications).

#### Monitoring Dashboards and resources:

|  Dashboard | Resources to be Monitored  |
|-------|-------|
| Cloud Storage | Requests, Network Traffic Sent, Network Traffic Received, Object Count, Object Size |
| BigQuery | Tables, Stored Bytes, Uploaded Rows |

#### Metrics based alerts:

| Alert | Threshold | Description |
|-------|-------|-------|
| Bigquery - dataset | `#` | Number of bytes stored |
| GCS Bucket - Sent bytes | `> 1024MB` | Cloud Storage bucket sent data in bytes |
| GCS Bucket - Received bytes | `> 1024MB` | Cloud Storage bucket received data in bytes |
| GCS Bucket - Total bytes | `>= 80%` | Cloud Storage cumulative data in bytes |
| GCS Bucket - Object count | `5000` | Cloud Storage bucket object counts  |

#### Logs based alerts:

Log based alerts are not necessary for this project but if needed can be configured to collect payload metrics.

| Alert | Type | Description |
|-------|-------|-------|
| Log bucket monthly bytes ingested | Log | Log bucket month-to-date bytes ingested. |
| Log bytes ingested | Log | Log bytes ingested to know the billing of ingested data |

### Data Project

Monitoring in the Data project includes Cloud Storage, Cloud Pub/Sub, and Cloud SQL. There are metrics based alerts for SSL certificates, Cloud SQL, Cloud Storage, Cloud Pub/Sub, and Cloud Functions. This project does not contain logs based alerts. Instructions for creating the individual alerts can be found in the section [Configuring Alerts and Notifications](#3-configuring-alerts-and-notifications).

#### Monitoring Dashboards and resources:

|  Dashboard | Resources to be Monitored  |
|-------|-------|
| Cloud Storage | Requests, Network Traffic Sent, Network Traffic Received, Object Count, Object Size |
| Cloud Pub/Sub | Publish Message Operations, Average Message Size |
| Cloud SQL | Queries, Network Connections, CPU Utilization, Memory Utilization, Disk Utilization |

#### Metrics based alerts:

| Alert | Threshold | Description |
|-------|-------|-------|
| SSL certificate expiring soon | `1 Month` | Expiration date of SSL certificates |
| Cloud SQL Available for failover | `> 0`  | A value `>0` indicates the failover operation is available on the instance. |
| Cloud SQL Connections | `< 500` | Number of SQL Connections |
| Cloud SQL Database - Memory utilization | `>= 70%` | Memory utilization percentage of the Cloud SQL instance |
| Cloud SQL Database - Disk utilization | `>= 70%` | Disk utilization percentage of the Cloud SQL instance |
| Cloud SQL Instances State | `1` or `0` | Cloud SQL instance state - a value `1` indicates the instance is up |
| Cloud SQL Database - CPU utilization | `>=70%` | CPU utilization percentage of the Cloud SQL instance |
| Cloud SQL InnoDB pages written | `>=70%` | InnoDB pages written percentage |
| Cloud SQL InnoDB pages read | `>= 70%` | InnoDB pages read percentage |
| Cloud SQL - Disk write IO | `>= 1000/s` | Disk IO write count of the Cloud SQL instance |
| Cloud SQL - Disk read IO | `>= 1000/s` | Disk IO read count of the Cloud SQL instance |
| Cloud SQL Database - Queries | `#` | Number of running SQL Queries |
| GCS Bucket - Sent bytes | `1024 MB` | Cloud Storage bucket sent data in bytes |
| GCS Bucket - Received bytes | `1024 MB` | Cloud Storage bucket received data in bytes |
| GCS Bucket - Total bytes | `>= 80%` | Cloud Storage cumulative data in bytes |
| GCS Bucket - Object count | `5000` | Cloud Storage bucket object count |
| Cloud Pub/Sub-Topic-Publish requests | `>= 10MB` | Cloud Pub/Sub Topics Publish message request  |
| Cloud Pub/Sub- subscription-Backlog size | `<= 1MB` | Size of Cloud Pub/Sub subscription backlog |
| Oldest unacked message age | `<= 1s` | Age (in seconds) of the oldest unacknowledged message |
| Cloud Function - Execution times | `>= 9 minutes` | Cloud Function Execution time |

### Devops Project

Monitoring in the Devops project is primarily based around Cloud storage usage with metrics based alerts triggering on Cloud Storage criteria. Logs based alerts are used to track failures in Cloud Build. Instructions for creating the individual alerts can be found in the section [Configuring Alerts and Notifications](#3-configuring-alerts-and-notifications).

Below are the recommended dashboard components and alerts for the Devops project

#### Monitoring Dashboards

|  Dashboard | Resources to be Monitored  |
| Cloud Storage | Requests, Network Traffic Sent, Network Traffic Received, Object Count, Object Size |

#### Metrics based alerts

| Alert | Threshold | Description |
|-------|-------|-------|
| GCS Bucket - Sent bytes | `>= 1024 MB` | Cloud Storage bucket sent data in bytes |
| GCS Bucket - Received bytes | `>= 1024 MB` | Cloud Storage bucket received data in bytes |
| GCS Bucket - Total bytes | `>= 80%` | Cloud Storage cumulative data in bytes |
| GCS Bucket - Object count | `>= 5000` | Cloud Storage bucket Object counts  |

   

#### Logs based alerts

| Alert |  Metric Query | Description |
|-------|-------|-------|
| Cloud Build failure | ```resource.type="build", logName=("projects/<projectname>/logs/cloudaudit.googleapis.com%2f Activity" OR "projects/<projectname>/logs/cloudaudit.googleapis.com%2Fdata_access" OR "projects/<projectname>/logs/cloudbuild"), severity>=ERROR, ``` | Trigger an alert when Cloud Build experiences a failure or error |

### Firebase Project

Monitoring in the Data project includes Cloud Storage, Cloud Pub/Sub, and firewall configurations. There are metrics based alerts for SSL certificates, Cloud Storage, Cloud Pub/Sub, Firestore, and Cloud Functions. Optional logs based alerts can include the amount of logs ingested. Instructions for creating the individual alerts can be found in the section [Configuring Alerts and Notifications](#3-configuring-alerts-and-notifications).

#### Monitoring Dashboards and resources:

|  Dashboard | Resources to be Monitored  |
|-------|-------|
| Cloud Storage | Requests, Network Traffic Sent, Network Traffic Received, Object Count, Object Size |
| Cloud Pub/Sub | Tables, Stored Bytes, Uploaded Rows |
| App Engine ( informational Dashboard) |  App Engine Dashboard will give the information about the firewall configuration. |

#### Metrics based alerts:

| Alert | Threshold | Description |
|-------|-------|-------|
| SSL certificate expiring soon | `1 Month` | Expiration date of SSL certificates |
| GCS Bucket - Object count | `>= 5000` | Total number of objects grouped by storage class. This value is measured once per day, and the value is repeated at each sampling interval throughout the day. For this metric, the sampling period is a reporting period, not a measurement period. |
| GCS Bucket - Sent bytes | `> 1024MB` | Cloud Storage bucket sent data in bytes |
| GCS Bucket - Received bytes | `> 1024MB` | Cloud Storage bucket received data in bytes |
| GCS Bucket - Total bytes | `>= 80%` | Cloud storage cumulative data in bytes |
| Oldest unacked message age | `< 1s` | Age (in seconds) of the oldest unacknowledged message |
| Pub/Sub - Publish message size | `10MB` | Distribution of publish message sizes (in bytes) |
| Firestore-instance - read | `#` | Number of successful document reads from queries or lookups |
| Firestore-instance - write | `#` | Number of successful document writes |
| Firestore-instance - delete | `#` | Number of document deletes |
| Cloud Function - Execution times | `>= 9 minutes` | Cloud Function execution time |

#### Logs based alerts:

Log based alerts are not necessary for this project but if needed can be configured to collect payload metrics.

| Alert | Type | Description |
| Log bucket monthly bytes ingested | Log | Log bucket month-to-date bytes ingested. |
| Log bytes ingested | Log | Log bytes ingested to know the billing of ingested data |

### Networks Project

Monitoring in the Networks project includes GCE VMs, network traffic and firewall configuration. There are metrics based alerts for the VPC usage and VM utilization. This project does not contain logs based alerts. Instructions for creating the individual alerts can be found in the section [Configuring Alerts and Notifications](#3-configuring-alerts-and-notifications).

#### Monitoring Dashboards and resources:

|  Dashboard | Resources to be Monitored  |
|-------|-------|
| VM instances | CPU , Memory, Disk Utilization, Network Traffic |
| Firewalls | Firewall configuration information |

#### Metrics based alerts:

| Alert | Type | Description |
|-------|-------|-------|
| Instances Per VPC Network quota limit | `#` | Quota limit for  `compute.googleapis.com/instances_per_vpc_network` |
| Instances Per VPC Network quota usage | `#` | Current usage on quota metric `compute.googleapis.com/instances_per_vpc_network` |
| VM - CPU utilization | `>= 80%` | CPU utilization percentage |
| VM - disk utilization  | `>= 80%` | Disk utilization percentage |
| VM - memory utilization  | `>= 80%` | Memory utilization percentage |

### Secrets Project

There are not recommended monitoring or alerts to be configured for the Secrets project.

## 3. Configuring Alerts and Notifications

#### Configuring Monitoring Dashboards

To configure monitoring dashboards go to the [Dashboards Overview](https://console.cloud.google.com/monitoring/dashboards) page in the Monitoring section of the console and use the project selector at the top to select the project. Use the `Create Dashboard` button to create individual dashboards and then configure the recommended resources for the project.

**Configure Notification Channels**

When creating an alerting policy, you can select a configured notification channel and add it to your policy. You can pre-configure your notification channels, or you can configure them as part of the process of creating an alerting policy.

Email notification channels can also be created during the creation of an alerting policy. For more information, see[ Creating a channel on demand.](https://cloud.google.com/monitoring/support/notification-options#on-demand) If you use a group email address as the notification channel for an alerting policy, make sure the group is configured to allow incoming mail from `[alerting-noreply@google.com](mailto:alerting-noreply@google.com)`.

To configure a notification channel, you must have one of the following Identity and Access Management roles on the project being monitored

*   `Monitoring Notification Channel Editor`
*   `Monitoring Editor`
*   `Monitoring Admin`
*   `Project Editor`
*   `Project Owner`

A notification channel must be created for each project. Use the following steps to configure a notification channel:

*   Go to the [Alerting](https://console.cloud.google.com/monitoring/alerting) page in the Monitoring section of the console and use the project selector at the top to select the project
*   Click the `Edit Notification Channels` button
*   If creating an email channel, locate the channel type `Email`, click `Add New` and follow the instructions
*   If creating another type of notification channel such as a mobile app, PagerDuty, SMS, Slack, Pub/Sub, or webhooks, see the instructions for [Creating channels](https://cloud.google.com/monitoring/support/notification-options#creating_channels). 

#### Configuring Uptime Checks

*   Go to the [Uptime checks](https://console.cloud.google.com/monitoring/uptime) page in the Monitoring section of the console and use the project selector at the top to select the project
*   Click `Create Uptime Check`
*   Enter a descriptive title such as: `Auth-Server-Health Check`
*   In the Target, select the protocol `HTTPS`
*   Choose the Resource Type `URL`
*   Enter the hostname and path as specified in the project instructions above
*   Select a frequency of how often you would like to run the checks
*   If you want to only run checks from certain geographic regions, you can edit this in `More Target Options`
*   Select a value for the response timeout to use and make sure the box `Log check failures` is checked to save errors to Cloud Logging
*   Give the alert a name and select one of your pre-configured notification channels
*   Verify the configuration using the `Test` button and then `Create` once the settings are confirmed

#### Configuring Metrics Based Alerts

*   Go to the [Alerting](https://console.cloud.google.com/monitoring/alerting) page in the Monitoring section of the console and use the project selector at the top to select the project
*   Click on the `Create Policy` button
*   Click `Add Condition`
*   Search for the target resource type, metric, and condition specified in the project recommendations above
*   After adding the condition, select one of your pre-configured notification channels and add auto-close duration before reviewing the configuration and creating the policy

#### Configuring Logs Based Alerts

*   Go to the [Logs Explorer](https://console.cloud.google.com/logs/query) page in the Logging section of the console and use the project selector at the top to select the project
*   In the Query field enter the metrics query specified in the project recommendations above and then run the query to validate the results
*   In the query results pane click on the `Actions` menu and then `Create log alert`
*   In the alert details pane, add a name and description
*   In `Choose logs to include in the alert` check the query and results by clicking `Preview logs`
*   Select a minimum time between notifications
*   Select one of your pre-configured notification channels and save the alert