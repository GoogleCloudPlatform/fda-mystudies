#!/usr/bin/env bash

for image in $(gcloud container images list --repository=gcr.io/heroes-hat-dev | tail -n +2); do
  new="$(echo "${image}" | sed 's/dev/dev-apps/')"
  docker pull "${image}"
  docker tag "${image}" "${new}"
  docker push "${new}"
done
