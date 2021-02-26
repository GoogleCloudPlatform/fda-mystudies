UPDATE fda_hphc.roles SET role_name = 'Superadmin' 
WHERE role_name = 'Project Lead';

UPDATE fda_hphc.roles SET role_name = 'Study admin' 
WHERE role_name = 'Coordinator';

DELETE FROM fda_hphc.user_permission_mapping WHERE permission_id IN ('5','7') AND user_id not IN
 (SELECT user_id FROM fda_hphc.users WHERE access_level ='SUPERADMIN');
 
UPDATE fda_hphc.users SET role_id='2', access_level='STUDY ADMIN'   WHERE access_level !='SUPERADMIN';
