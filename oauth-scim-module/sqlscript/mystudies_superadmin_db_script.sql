/*
-- Query: SELECT * FROM oauth_scim_db.users
LIMIT 0, 1000

-- Date: 2020-10-05 15:18

	"email":"superadmin@gmail.com",
	"password":"Ch@ngeM3",
	"appId":"PARTICIPANT MANAGER",
	"status":0
*/
INSERT INTO `users` (`id`,`app_id`,`created`,`email`,`status`,`temp_reg_id`,`user_id`,`user_info`) VALUES ('8ad16a8c74f823a10174f82c9a300001','PARTICIPANT MANAGER','2020-10-05 15:21:46','superadmin@gmail.com',0,'bd676334dd745c6afaa6547f9736a4c4df411a3ca2c4f514070daae31008cd9d','96494ebc2ae5ac344437ec19bfc0b09267a876015b277e1f6e9bfc871f578508','{\"password\": {\"hash\": \"9bb85ab372ccd4b69b78477a89ddd8437d26e0fe10d2618e1edf48cddf56f1d2fcf9de71a39cdae01493c69e2bbc1b3ff890eda31ee2f4c00967e17f8fe03556\", \"salt\": \"1e73c28e50e41f2d2175ba3ba3349395ebe80c42f837ffaaa06a7adf170bd3238fbda39cb6357b8410aeafcd8647619abfd2657900bd26011c7775504760c968\", \"expire_timestamp\": 1609667506735}, \"password_history\": [{\"hash\": \"9bb85ab372ccd4b69b78477a89ddd8437d26e0fe10d2618e1edf48cddf56f1d2fcf9de71a39cdae01493c69e2bbc1b3ff890eda31ee2f4c00967e17f8fe03556\", \"salt\": \"1e73c28e50e41f2d2175ba3ba3349395ebe80c42f837ffaaa06a7adf170bd3238fbda39cb6357b8410aeafcd8647619abfd2657900bd26011c7775504760c968\", \"expire_timestamp\": 1609667506735}]}');