<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

This directory contains some Kubernetes resources common to all applications. All files below are relative to the root of the repo:
* kubernetes/
  * cert.yaml
    * A Kubernetes ManagedCertificate for using
            [Google-managed SSL certificates](https://cloud.google.com/kubernetes-engine/docs/how-to/managed-certs).
  * ingress.yaml
    * A Kubernetes Ingress for routing HTTP calls to services in the
            cluster
  * pod_security_policy.yaml
    * A restrictive Pod Security Policy that applies to the cluster apps
  * pod_security_policy-istio.yaml
    * A looser Pod Security Policy that only applies to Istio containers
            in the cluster
  * kubeapply.sh
    * A helper script that applies all resources to the cluster. Not
            required, the manual steps will be described below
* auth-server/
  * tf-deployment.yaml
    * A Kubernetes Deployment, deploying the app along with its secrets.
    * This is forked from deployment.yaml with modifications for the Terraform
        setup.
  * tf-service.yaml
    * A Kubernetes Service, exposing the app to communicate with other apps
        and the Ingress
    * This is forked from service.yaml with modifications for the Terraform
        setup
* response-datastore/
  * same as auth-server
* study-builder/
  * same as auth-server
* study-datastore/
  * same as auth-server
* participant-datastore/consent-mgmt-module
  * same as auth-server
* participant-datastore/enroll-mgmt-module
  * same as auth-server
* participant-datastore/user-mgmt-module
  * same as auth-server
* participant-manager/
  * same as auth-server

***
<p align="center">Copyright 2020 Google LLC</p>
