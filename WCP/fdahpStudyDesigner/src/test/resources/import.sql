INSERT INTO `users` (`user_id`, `first_name`, `last_name`, `email`, `password`, `role_id`, `created_by`, `modified_by`, `status`, `accountNonExpired`, `accountNonLocked`, `credentialsNonExpired`, `force_logout`) VALUES ('1', 'abc', 'xyx', 'ttt@gmail.com', '$2a$10$uSKnFqkar9ugqrdD1KElcOcPGEtdpuNMvwlHfRGwX4jovq.sH0e/q', '1', '1', '1', '1', '1', '1', '1', 'N');		

INSERT INTO questionnaires_steps (step_id, questionnaires_id, instruction_form_id, step_short_title, step_type, sequence_no, destination_step, repeatable, skiappable, active, status) VALUES (1, 11675, 85231, '1time', 'Question', 1, 60873, 'No', 'Yes', 1, 1), (2, 1, 1, '1time', 'Question', 1, 60873, 'No', 'Yes', 1, 1);

INSERT INTO questionnaires (id, study_id, frequency, title, study_lifetime_start, study_lifetime_end, short_title, repeat_questionnaire, created_by, created_date, modified_by, modified_date, branching, active, status, custom_study_id, is_live, version, is_Change, schedule_type) VALUES (1, 58, 'One time', 'quesions', '2020-09-02', '2020-09-23', 'onetime', 5, 0, '2020-09-02 13:03:11', 0, '2020-09-02 14:25:43', 1, 1, 1, 'OpenStudy003', 1, 1, 1, 'Regular'), (2, 58, 'One time', 'quesions', '2020-09-02', '2020-09-23', 'onetime', 5, 0, '2020-09-02 13:03:11', 0, '2020-09-02 14:25:43', 1, 1, 1, 'OpenStudy003', 1, 1, 1, 'Regular');

INSERT INTO study_version (version_id, activity_version, custom_study_id, study_version, consent_version) VALUES (1, 1, 'OpenStudy003', 1, 1);

INSERT INTO form_mapping (id, form_id, question_id, sequence_no) VALUES (1, 58, 85199, 1);

INSERT INTO `questions` (`id`, `active`, `add_line_chart`, `description`, `modified_by`, `response_type`, `status`, `use_anchor_date`, `use_stastic_data`, `allow_healthkit`) VALUES ('1', '1', 'No', 'hi', '1', '6', '1', '0', 'No', 'No');

INSERT INTO `resources` (`id`, `study_id`, `title`, `text_or_pdf`, `rich_text`, `pdf_url`, `resource_visibility`, `resource_text`, `action`, `study_protocol`, `created_by`, `modified_by`, `status`, `pdf_name`, `sequence_no`) VALUES ('1', '1374', 'resource', '1', 'a@gmail.com', 'abcd.pdf', '1', 'text2', '1', '0', '1', '1', '1', 'ab.pdf', '1');

INSERT INTO `study_sequence` (`study_sequence_id`, `study_id`, `actions`, `basic_info`, `check_list`, `comprehension_test`, `consent_edu_info`, `e_consent`, `eligibility`, `miscellaneous_branding`, `miscellaneous_notification`, `miscellaneous_resources`, `over_view`, `setting_admins`, `study_dashboard_chart`, `study_dashboard_stats`, `study_exc_active_task`, `study_exc_questionnaries`) VALUES (10633, 678574, 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N');