# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.
# Stage 1
# Installing all the dependencies and creating Dist file or building angular application
FROM node:6.9.5-alpine
RUN mkdir -p /code
WORKDIR /code
ADD . /code
RUN npm install -g -s --no-progress yarn && \
    yarn && \
    yarn run build && \
    yarn run prune && \
    yarn cache clean
CMD [ "yarn", "run", "prod" ]

#Stage2
#copying and running the application in the nginx with the dist file created in stage 1(Angular Build)
FROM nginx
COPY --from=compile-image /app/certificates/default.conf /etc/nginx/conf.d/
COPY --from=compile-image /app/certificates/server.crt /etc/ssl/
COPY --from=compile-image /app/certificates/server.key /etc/ssl/
COPY --from=compile-image /app/dist/userRegistrationWeb /usr/share/nginx/html
