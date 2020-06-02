#Stage 1
FROM node:latest AS compile-image
WORKDIR /app
#COPY package.json ./
COPY . ./
RUN npm install
RUN npm install -g @angular/cli
RUN npm install typescript@3.8.3
RUN ng build --aot --prod

#Stage2
FROM nginx
COPY --from=compile-image /app/dist/userRegistrationWeb /usr/share/nginx/html