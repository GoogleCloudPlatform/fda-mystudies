var express = require("express");
var app = express();
let cors = require("cors");
var httpProxy = require("http-proxy");
var apiProxy = httpProxy.createProxyServer();
var particpantManageServerUrl = "http://192.168.0.44:8003/",
  authServerUrl = "http://35.193.185.224:8087";
app.use(cors());
app.all("/participant-manager-service/*", function (req, res) {
  apiProxy.web(req, res, { target: particpantManageServerUrl });
});
app.all("/oauth-scim-service/*", function (req, res) {
  apiProxy.web(req, res, { target: authServerUrl });
});
app.listen(3000);
