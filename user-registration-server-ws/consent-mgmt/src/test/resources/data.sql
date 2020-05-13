
INSERT INTO `org_info` (`id`, `name`, `org_id`, `created_on`, `created_by`, `modified_by`, `modified_date`) VALUES
	(1, 'organizations name', 1, '2020-03-03 15:35:27', 0, 0, '2020-03-12 15:25:14');

INSERT INTO `app_info` (`app_info_id`, `custom_app_id`, `org_info_id`, `created_on`, `android_bundle_id`, `app_name`, `created_by`, `modified_by`, `android_server_key`, `app_description`, `ios_certificate`, `ios_bundle_id`, `ios_certificate_password`, `from_email_id`, `from_email_password`, `forgot_email_body`, `forgot_email_sub`, `reg_email_body`, `reg_email_sub`, `method_handler`, `modified_date`) VALUES
	(1, 'app-id-cust', 1, '2020-01-16 15:22:22', NULL, 'app-name-1', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2020-03-12 15:17:56');
	

	
INSERT INTO `study_info` (`id`, `custom_id`, `app_info_id`, `name`, `description`, `type`,`created_by`, `category`, `created_on`, `enrolling`, `modified_by`, `modified_date`, `sponsor`, `status`, `tagline`, `version`) VALUES
	(1, 'StudyofHealth', 1, 'name', 'description', NULL, 0, NULL, '2020-03-12 15:23:41', NULL, NULL, '2020-03-12 15:24:42', NULL, NULL, NULL, NULL),
	(2, 'custom-id-2', 1, 'name-2', 'description', 'CLOSED', 0, NULL, '2020-03-12 15:23:44', NULL, NULL, '2020-03-12 15:24:45', NULL, NULL, NULL, NULL);

INSERT INTO `locations` (`id`, `created`, `created_by`, `custom_id`, `description`, `is_default`, `name`, `status`) VALUES
	(2, '2020-03-17 18:59:15', 1, '-customId130.53', 'location-descp-updated', 'N', 'name -1-updated000', '1');
	
	
INSERT INTO `sites` (`id`, `study_id`, `location_id`, `status`, `target_enrollment`, `name`, `created`, `created_by`) VALUES
	(1, 1, 2, 1, 10, 'test-site', '2020-03-17 20:19:42', 0),
	(2, 2, 2, 0, 15, 'test-site', '2020-03-12 15:19:38', 0),
	(3, 2, 2, 1, 45, 'test site', '2020-03-13 15:26:56', 0);


--here
INSERT INTO `user_details` (`user_details_id`, `user_id`, `app_info_id`, `email`, `status`, `first_name`, `last_name`, `local_notification_flag`, `locale`, `remote_notification_flag`, `security_token`, `touch_id`, `use_pass_code`, `verification_date`, `_ts`, `code_expire_date`, `email_code`, `reminder_lead_time`) VALUES
	(44, 'kR2g5m2pJPP0P31-WNFYK8Al7jBP0mJ-cTSFJJHJ4DewuCg', 1, 'cdash93@gmail.com', 1, NULL, NULL, 0, NULL, 0, NULL, 0, 0, '2020-01-30 20:21:28', '2020-02-05 19:11:05', NULL, NULL, NULL);
	
		
INSERT INTO `participant_registry_site` (`id`, `site_id`, `study_info_id`, `email`, `invitation_date`, `onboarding_status`, `enrollment_token`, `enrollment_token_expiry`, `created`, `created_by`, `name`, `invitation_count`, `disabled_date`) VALUES
	(33, 1, 1, 'abc@gmail.com', '2020-02-07 20:37:25', 'I', 'dsgdsfgag', '2020-02-09 18:42:32', '2020-02-09 18:42:32', 2, NULL, 0, NULL),
	(34, 1, 1, 'xyz@gf.com', '2020-02-07 20:38:36', 'N', 'dfdsg', '2020-02-09 18:42:34', '2020-02-09 18:42:32', 3, NULL, 0, NULL),
	(35, 1, 1, 'pqr@gf.com', '2020-02-07 20:38:36', 'D', 'dfdsg', '2020-02-09 18:42:34', '2020-02-09 18:42:32', 3, NULL, 0, NULL);

-- here
INSERT INTO `participant_study_info` (`participant_study_info_id`, `participant_id`, `study_info_id`, `participant_registry_site_id`, `site_id`, `user_details_id`, `consent_status`, `status`, `bookmark`, `eligibility`, `enrolled_date`, `sharing`, `completion`, `adherence`, `withdrawal_date`) VALUES
	(101, '1', 1, 33, 1, 44, NULL, 'Withdrawn', 1, NULL, '2020-02-06 14:07:29', NULL, 45, 20, '2020-02-10 14:03:14'),
	(102, '2', 1, 34, 1, 44, NULL, 'Enrolled', 0, NULL, '2020-02-06 14:07:31', NULL, 50, 36, '2020-02-06 14:07:31');