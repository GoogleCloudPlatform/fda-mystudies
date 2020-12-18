# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.

#!/usr/bin/env bash

# Short helper script to run repetitive commands for Kubernetes deployments.
# Args:
# kubeapply.sh <apps-project> <region> <cluster>
#
# It does the following:
#  * Activates the cluster for kubectl via `gcloud container clusters get-credentials`.
#  * Applies the pod security policies.
#  * Applies the cert configuration.
#  * Applies all deployments from children of the parent folder.
#  * Applies all services from children of the parent folder.
#  * Applies the ingress configuration
#
# The services and deployments should be applied separately.
#
#
# Run like:
# $ ./deployment/scripts/kubeapply.sh <apps-project> <cluster> <region>

if [ "$#" -ne 3 ]; then
  echo 'Please provide exactly 1 argument in the order of <apps-project> <cluster> <region>'
  exit 1
fi

PROJECT=${1}
CLUSTER="${2}"
REGION="${3}"
shift 3

set -e

echo "=== Switching kubectl to cluster ${CLUSTER} ==="
read -p "Press enter to continue"
gcloud container clusters get-credentials "${CLUSTER}" --region="${REGION}" --project="${PROJECT}"

for policy in $(find . -name "pod_security_policy*.yaml"); do
  echo "=== Applying ${policy} ==="
  read -p "Press enter to continue"
  kubectl apply -f ${policy}
done

for cert in $(find . -name "cert*.yaml"); do
  echo "=== Applying ${cert} ==="
  read -p "Press enter to continue"
  kubectl apply -f ${cert}
done

for deployment in $(find . -name "tf-deployment.yaml"); do
  echo "=== Applying deployment ${deployment} ==="
  read -p "Press enter to continue"
  kubectl apply -f ${deployment}
done

for service in $(find . -name "tf-service.yaml"); do
  echo "=== Applying service ${service} ==="
  read -p "Press enter to continue"
  kubectl apply -f ${service}
done

for ingress in $(find . -name "ingress*.yaml"); do
  echo "=== Applying ${ingress} ==="
  read -p "Press enter to continue"
  kubectl apply -f ${ingress}
done
