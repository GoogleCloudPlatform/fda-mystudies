# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.
# Stage 1
# Installing all the dependencies and creating Dist file or building angular application
FROM node:latest AS compile-image
WORKDIR /app
COPY . ./
RUN npm install
RUN npm install -g @angular/cli
RUN ng build --aot --dev

# Stage2
# copying and running the application in the nginx with the dist file created in stage 1(Angular Build)
FROM nginx
COPY --from=compile-image /app/dist/userRegistrationWeb /usr/share/nginx/html
