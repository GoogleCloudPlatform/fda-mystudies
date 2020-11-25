# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.

#/bin/bash

# This is a helper script to delete projects created with terraform engine.
# Run like:
# $ ./deployment/scripts/delete_project.sh fda-mystudies-dev-apps

set -e

for PROJECT in $@
do
   echo ${PROJECT}
   for lien in $(gcloud alpha resource-manager liens list --project=${PROJECT} --format="value(name)")
   do
   gcloud alpha resource-manager liens delete ${lien}
   done

   gcloud projects delete ${PROJECT}
done