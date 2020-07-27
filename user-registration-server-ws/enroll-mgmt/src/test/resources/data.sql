INSERT INTO `org_info` (`id`, `name`, `org_id`, `created_on`, `created_by`, `modified_by`, `modified_date`) VALUES
	(1, 'organizations name', 1, '2020-03-03 15:35:27', 0, 0, '2020-03-12 15:25:14');

INSERT INTO `app_info` (`app_info_id`, `custom_app_id`, `org_info_id`, `created_on`, `app_name`, `created_by`, `modified_date`) VALUES
	(1, 'app-id-cust', 1, '2020-01-16 15:22:22', 'app-name-1', 0, '2020-03-12 15:17:56');
	
INSERT INTO `study_info` (`id`, `custom_id`, `app_info_id`, `name`, `description`, `type`,`created_by`, `created_on`, `modified_date`) VALUES
	(1, 'StudyofHealth', 1, 'name', 'description', 'OPEN', 0, '2020-03-12 15:23:41',  '2020-03-12 15:24:42'),
	(2, 'custom-id-2', 1, 'name-2', 'description', 'CLOSE', 0, '2020-03-12 15:23:44', '2020-03-12 15:24:45'),
	(3, 'StudyofHealthClose', 1, 'name', 'description', 'CLOSE', 0, '2020-03-12 15:23:41',  '2020-03-12 15:24:42'),
	(4, 'StudyofHealth1', 1, 'name', 'description', 'CLOSE', 0, '2020-03-12 15:23:41',  '2020-03-12 15:24:42'),
	(5, 'OpenStudy02', 1, 'name', 'description', 'CLOSE', 0, '2020-03-12 15:23:41',  '2020-03-12 15:24:42');

INSERT INTO `locations` (`id`, `created`, `created_by`, `custom_id`, `description`, `is_default`, `name`, `status`) VALUES
	(2, '2020-03-17 18:59:15', 1, '-customId130.53', 'location-descp-updated', 'N', 'name -1-updated000', '1');

INSERT INTO `sites` (`id`, `study_id`, `location_id`, `status`, `target_enrollment`, `name`, `created`, `created_by`) VALUES
	(1, 1, 2, 1, 10, 'test-site', '2020-03-17 20:19:42', 0),
	(2, 2, 2, 0, 15, 'test-site', '2020-03-12 15:19:38', 0),
	(3, 2, 2, 1, 45, 'test site', '2020-03-13 15:26:56', 0);

INSERT INTO `user_details` (`user_details_id`, `user_id`, `app_info_id`, `email`, `status`, `first_name`, `last_name`, `local_notification_flag`, `remote_notification_flag`,`touch_id`, `use_pass_code`, `verification_date`, `_ts`) VALUES
	(44, 'kR2g5m2pJPP0P31-WNFYK8Al7jBP0mJ-cTSFJJHJ4DewuCg', 1, 'cdash93@gmail.com', 1, 'test', 'user', 0, 0,  0, 0, '2020-01-30 20:21:28', '2020-02-05 19:11:05'),
	(45, 'leqmF97faSdVEB8-aUpS0dbXtSMDwIC-Iu9DgRWEcGxZezM', 1, 'cdash93@gmail.com', 1, 'test', 'user', 0, 0,  0, 0, '2020-01-30 20:21:28', '2020-02-05 19:11:05');
	
INSERT INTO `participant_registry_site` (`id`, `site_id`, `study_info_id`, `email`, `invitation_date`, `onboarding_status`, `enrollment_token`, `enrollment_token_expiry`, `created`, `created_by`, `invitation_count`) VALUES
	(30, 1, 1, 'abc@gmail.com', '2020-02-07 20:37:25', 'I', 'BSEEMNH6', '2020-02-09 18:42:32', '2020-02-09 18:42:32', 2, 0),
	(33, 1, 1, 'abc@gmail.com', '2020-02-07 20:37:25', 'I', 'BSEEMNH6', '2020-02-09 18:42:32', '2020-02-09 18:42:32', 2, 0),
	(34, 1, 4, 'xyz@gf.com', '2020-02-07 20:38:36', 'N', 'YB0CZICQ', '2020-02-09 18:42:34', '2020-02-09 18:42:32', 3, 0),
	(35, 1, 3, 'pqr@gf.com', '2020-02-07 20:38:36', 'D', 'YB0CZICQ', '2020-02-09 18:42:34', '2020-02-09 18:42:32', 3, 0),
	(36, 1, 1, 'pqr@gf.com', '2020-02-07 20:38:36', 'D', '6DL0POQF', '2020-02-09 18:42:34', '2020-02-09 18:42:32', 3, 0),
	(37, 1, 3, 'pqr@gf.com', '2020-02-07 20:38:36', 'D', '6DL0POQF', '2020-02-09 18:42:34', '2020-02-09 18:42:32', 3, 0),
	(38, 1, 3, 'abcd@gf.com', '2020-02-07 20:38:36', 'D', 'MCE0M600', '2020-02-09 18:42:34', '2020-02-09 18:42:32', 3, 0),
	(39, 1, 5, 'abcd@gf.com', '2020-02-07 20:38:36', 'D', '9E3IFFL0', '2020-02-09 18:42:34', '2020-02-09 18:42:32', 3, 0),
	(40, 1, 5, 'abcd@gf.com', '2020-02-07 20:38:36', 'D', '9E3IFFL0', '2020-02-09 18:42:34', '2020-02-09 18:42:32', 3, 0);
	

INSERT INTO `participant_study_info` (`participant_study_info_id`, `participant_id`, `study_info_id`, `participant_registry_site_id`, `site_id`, `user_details_id`, `status`, `bookmark`, `enrolled_date`, `completion`, `adherence`, `withdrawal_date`) VALUES
	(101, '1', 3, 35, 1, 44, 'Withdrawn', 1, '2020-02-06 14:07:29', 45, 20, '2020-02-10 14:03:14'),
	(102, '2', 4, 34, 1, 44, 'Enrolled', 0, '2020-02-06 14:07:31', 50, 36, '2020-02-06 14:07:31'),
	(103, '3', 3, 38, 1, 44, 'Enrolled', 0, '2020-02-06 14:07:31', 50, 36, '2020-02-06 14:07:31'),
	(104, '4', 5, 39, 1, 44, 'Enrolled', 0, '2020-02-06 14:07:31', 50, 36, '2020-02-06 14:07:31');
	