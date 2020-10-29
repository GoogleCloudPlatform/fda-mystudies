# Stage 1
# Installing all the dependencies and creating Dist file or building angular application
FROM node:latest AS compile-image
WORKDIR /app
COPY . ./
RUN npm install
#RUN  npm install -g @angular/cli
#RUN npm install -g npm@7.0.6
RUN  npm install -g @angular/cli
RUN ng build --aot --prod
#Stage2
#copying and running the application in the nginx with the dist file created in stage 1(Angular Build)
FROM nginx
COPY --from=compile-image /app/certificates/default.conf /etc/nginx/conf.d/
COPY --from=compile-image /app/certificates/server.crt /etc/ssl/
COPY --from=compile-image /app/certificates/server.key /etc/ssl/
COPY --from=compile-image /app/dist/userRegistrationWeb /usr/share/nginx/html
