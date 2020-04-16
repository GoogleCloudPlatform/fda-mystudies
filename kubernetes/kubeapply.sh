#!/usr/bin/env bash

# Short helper script to run repetitive commands for Kubernetes deployments.
# Args:
# kubeapply.sh <project> <region> <cluster>
# 
# It does the following:
#  * Activate sthe cluster for kubectl via `gcloud container clusters get-credentials`.
#  * Applies the pod security policies.
#  * Applies the heroes-hat cert configuration.
#  * Applies all services from children of the parent folder.
#  * Applies the ingress configuration
#
# The services and deployments should be applied separately.
#
# Requires existence of files ./pod_security_policy{,-istio}.yaml.
# Currently hardcoded to use projects "heroes-hat-dev-{apps,data}".
#
# Run like:
# $ ./kubernetes/kubeapply.sh heroes-hat-cluster

if [ "$#" -ne 1 ]; then
  echo 'Please provide exactly 1 argument in the order of <cluster>'
  exit 1
fi

cluster="${1}"
shift 1

set -e

serviceaccount="$(gcloud container clusters describe "${cluster}" --region="us-east1" --project="heroes-hat-dev-apps" --format='value(nodeConfig.serviceAccount)')"

echo "=== Switching kubectl to cluster ${cluster} ==="
read -p "Press enter to continue"
gcloud container clusters get-credentials "${cluster}" --region="us-east1" --project="heroes-hat-dev-apps"

for policy in $(find . -name "pod_security_policy*.yaml"); do
  echo "=== Applying ${policy} ==="
  read -p "Press enter to continue"
  kubectl apply -f ${policy}
done

echo '=== Applying heroes-hat-cert.yaml ==='
read -p "Press enter to continue"
kubectl apply -f ./heroes-hat-cert.yaml

for service in $(find .. -name "service.yaml"); do
  echo "=== Applying service ${service} ==="
  read -p "Press enter to continue"
  kubectl apply -f ${service}
done

echo '=== Applying ingress.yaml ==='
read -p "Press enter to continue"
kubectl apply -f ./ingress.yaml
