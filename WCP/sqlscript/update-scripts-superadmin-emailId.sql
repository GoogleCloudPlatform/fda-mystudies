/*
Note:
1.superadmin@gmail.com must be updated with deployment-specific value as required to save in super admins email Id.
*/

SET @superAdminEmailId :='superadmin@gmail.com';

update users u set u.email=@superAdminEmailId where u.user_id=1;