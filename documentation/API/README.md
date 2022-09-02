<!--
 Copyright 2021 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

## API Documentation

### Introduction
This guide provides instructions for generating API docs using the Springfox implementation of the Open API (Swagger) specification and using the Springfox Swagger UI or [Swagger Editor](https://editor.swagger.io/) to visually render documentation for an API defined with Open API (Swagger) specification. 

### Springfox Configuration
As mentioned above, we'll use Springfox implementation of the Open API (Swagger) specification. We've added below maven artifacts to the `pom.xml` in `common-service` module.

*  `springfox-swagger2`
*  `springfox-swagger-ui`
*  `springfox-bean-validators`
*  `spring-swagger-simplified`

The `SwaggerConfig` class is added in `common-service` module to override the default response messages, provide custom information about the API and to add static resources to the ResourceHandlerRegistry object. Please refer to the [Configuration explained](https://springfox.github.io/springfox/docs/current/#configuration-explained) for more details.

### Springfox Annotations

Springfox provides following annotations to use on Bean and Controller classes: `@ApiModelProperty`, `@ApiParam`, `@ApiImplicitParam`, `@ApiOperation`, `@RequestParam`, `@RequestHeader`. Please refer to the [Overriding descriptions via properties](https://springfox.github.io/springfox/docs/current/#overriding-descriptions-via-properties) for more details on Springfox annotations and attributes.

### API Docs

To verify that Springfox is working, we can visit following URL's in our browser:

*  http[s]://[DOMAIN]/auth-server/api/v2/api-docs
*  http[s]://[DOMAIN]/participant-manager-datastore/api/v2/api-docs
*  http[s]://[DOMAIN]/participant-consent-datastore/api/v2/api-docs
*  http[s]://[DOMAIN]/participant-enroll-datastore/api/v2/api-docs
*  http[s]://[DOMAIN]/participant-user-datastore/api/v2/api-docs
*  http[s]://[DOMAIN]/response-datastore/api/v2/api-docs

The result is a JSON response which is not very human readable. Swagger provides Swagger UI to visually render documentation for an API defined with Open API (Swagger) specification. 

The `SwaggerGeneratorTest` class is added in auth-server, response-datastore, participant-manager-datastore, participant-consent-datastore, participant-enroll-datastore and participant-user-datastore modules to generate the `openapi.docs` under `/fda-mystudies/documentation/API/[service-name]/`

### Springfox Swagger UI

The `springfox-swagger-ui` web jar ships with Swagger UI. To include it in a standard Spring Boot application we've added this dependency to `pom.xml` in `common-service` module. The Swagger UI page should then be available at

*  http[s]://[DOMAIN]/auth-server/swagger-ui.html 
*  http[s]://[DOMAIN]/participant-manager-datastore/swagger-ui.html 
*  http[s]://[DOMAIN]/participant-consent-datastore/swagger-ui.html 
*  http[s]://[DOMAIN]/participant-enroll-datastore/swagger-ui.html
*  http[s]://[DOMAIN]/participant-user-datastore/swagger-ui.html
*  http[s]://[DOMAIN]/response-datastore/swagger-ui.html

### Swagger Editor

The API docs are stored under `/fda-mystudies/documentation/API/[service-name]/` for offline reference. Please refer to the [Importing OpenAPI documents](https://github.com/swagger-api/swagger-editor/blob/master/docs/import.md#importing-openapi-documents) to import the `openapi.json` file into the [Swagger Editor](https://editor.swagger.io/).

