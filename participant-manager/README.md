<!--
 Copyright 2020-2021 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

# Overview

The **FDA MyStudies** [`Participant manager`](/participant-manager/) is a web application that provides a no-code user interface for researchers, clinicians and other study coordinators to track and manage the progress of participant enrollment across sites and studies. Study coordinators use the `Participant manager` to add participants to studies and view participant enrollment and consent status. Study coordinators also use the `Participant manager` to create study sites, and assign sites to studies. The `Participant manager` is an Angular web application that uses the [Participant manager datastore](../participant-manager-datastore) as the application backend. The `Participant manager` uses basic authentication `client_id` and `client_secret` managed by [`Hydra`](/hydra/) to interact with the `Participant manager datastore`.

The `Participant manager` provides the following functionality:

1. Create and manage study coordinator accounts
1. Create and manage study sites
1. Assign studies to sites
1. Add participants to studies
1. View participant enrollment status
1. View participant consent forms
1. Visualize study and site enrollment

<!-- A detailed user-guide for how to configure your first study can be found [here](TODO) --->

<!--A demonstration of the `Participant manager` application can be found [here](TODO). --->

Example screens:
![Example screens](../documentation/images/participant-manager-screens.png "Example screens")

# Deployment

> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory.

---

<p align="center">Copyright 2020-2021 Google LLC</p>
