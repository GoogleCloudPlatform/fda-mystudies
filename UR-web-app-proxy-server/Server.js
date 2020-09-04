var express = require("express");
var app = express();
let cors = require("cors");
app.get("/app1", function (req, res) {});
app.use(cors());
app.listen(3001);
