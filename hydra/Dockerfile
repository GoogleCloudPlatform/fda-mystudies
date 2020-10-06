# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM golang:1.14.1-buster

WORKDIR /

ARG HYDRA_VERSION=v1.7.4

# Install Hydra
RUN git clone https://github.com/ory/hydra.git && \
    cd hydra && \
    git checkout ${HYDRA_VERSION} && \
    go build

COPY hydra/entrypoint.bash /entrypoint.sh

EXPOSE 4444 4445

ENTRYPOINT [ "/entrypoint.sh" ]
