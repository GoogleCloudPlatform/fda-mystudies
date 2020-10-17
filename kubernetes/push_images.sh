#!/usr/bin/env bash

# This is a helper script to copy all container images between repositories.
# Only copies the "latest" tags.

if [ "$#" -ne 2 ]; then
  cat >&2 <<EOF
Invalid number of arguments

Usage:
./push_images.sh <source_registry> <destination_registry>

Example:
./push_images.sh gcr.io/fda-mystudies-dev gcr.io/fda-mystudies-dev-apps

EOF
  exit 1
fi

src="${1}"
dst="${2}"


for image in $(gcloud container images list --repository=gcr.io/fda-mystudies-dev | tail -n +2); do
  new="$(echo "${image}" | sed "s|${src}|${dst}|")"
  echo "Migrating image ${image} -> ${new}"
  docker pull "${image}"
  docker tag "${image}" "${new}"
  docker push "${new}"
done
