curl -v -X POST \
-H "appId: GCPMS001" \
-H "orgId: OrgName" \
-H "clientId: test_client_id_ma" \
-H "secretKey: caff08160ade4cd738f9531dbd2ed33c12715abb402296f3479170f626bc7f56" \
-H "Content-Type:application/json" \
-d '{"emailId": "yuchikuo+testcurl8@google.com", "password": "Password@1234"}' \
https://dev.heroes-hat.rocketturtle.net/myStudiesUserMgmtWSTest/register
#-F "emailId=yuchikuo+testcurl@google.com" \
#-F "password=Password@1234" \
