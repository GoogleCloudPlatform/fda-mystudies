#Stage 1
#Installing all the dependencies and creating Dist file or building angular application  
FROM node:latest AS compile-image
RUN npm install
WORKDIR /app
COPY package.json ./

COPY . ./
RUN npm install -g @angular/cli
RUN npm install typescript@3.8.3
RUN ng build --aot --prod

#Stage2
#copying and running the application in the nginx with the dist file created in stage 1(Angular Build)
FROM nginx
COPY --from=compile-image /app/dist/userRegistrationWeb /usr/share/nginx/html
