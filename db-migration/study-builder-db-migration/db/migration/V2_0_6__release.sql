/* Issue #3551 Standardize use of title case and lower case across screens, 
   and update text accordingly. */
   
UPDATE fda_hphc.statistic_master_images SET value = 'Heart rate' WHERE value = 'Heart Rate';
UPDATE fda_hphc.statistic_master_images SET value = 'Blood glucose' WHERE value = 'Blood Glucose';
UPDATE fda_hphc.statistic_master_images SET value = 'Active task' WHERE value = 'Active Task';
UPDATE fda_hphc.statistic_master_images SET value = 'Baby kicks' WHERE value = 'Baby Kicks';

UPDATE fda_hphc.roles SET role_name = 'Project lead' WHERE role_name = 'Project Lead';

UPDATE fda_hphc.reference_tables SET str_value = 'Biologics safety' WHERE str_value = 'Biologics Safety';
UPDATE fda_hphc.reference_tables SET str_value = 'Clinical trials' WHERE str_value = 'Clinical Trials';
UPDATE fda_hphc.reference_tables SET str_value = 'Cosmetics safety' WHERE str_value = 'Cosmetics Safety';
UPDATE fda_hphc.reference_tables SET str_value = 'Drug safety' WHERE str_value = 'Drug Safety';
UPDATE fda_hphc.reference_tables SET str_value = 'Food safety' WHERE str_value = 'Food Safety';
UPDATE fda_hphc.reference_tables SET str_value = 'Medical device safety' WHERE str_value = 'Medical Device Safety';
UPDATE fda_hphc.reference_tables SET str_value = 'Observational studies' WHERE str_value = 'Observational Studies';
UPDATE fda_hphc.reference_tables SET str_value = 'Public health' WHERE str_value = 'Public Health';
UPDATE fda_hphc.reference_tables SET str_value = 'Radiation-emitting products' WHERE str_value = 'Radiation-Emitting Products';
UPDATE fda_hphc.reference_tables SET str_value = 'Tobacco use' WHERE str_value = 'Tobacco Use';
UPDATE fda_hphc.reference_tables SET str_value = 'University research institute' WHERE str_value = 'University Research Institute';
UPDATE fda_hphc.reference_tables SET category = 'Data partner' WHERE category = 'Data Partner';
UPDATE fda_hphc.reference_tables SET category = 'Research sponsors' WHERE category = 'Research Sponsors';



UPDATE fda_hphc.question_responsetype_master_info SET response_type = 'Continuous scale' WHERE response_type = 'Continuous Scale';
UPDATE fda_hphc.question_responsetype_master_info SET response_type = 'Text scale' WHERE response_type = 'Text Scale';
UPDATE fda_hphc.question_responsetype_master_info SET response_type = 'Value picker' WHERE response_type = 'Value Picker';
UPDATE fda_hphc.question_responsetype_master_info SET response_type = 'Image choice' WHERE response_type = 'Image Choice';
UPDATE fda_hphc.question_responsetype_master_info SET response_type = 'Text choice' WHERE response_type = 'Text Choice';



UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Body mass index' WHERE display_name = 'Body Mass Index';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Body fat percentage' WHERE display_name = 'Body Fat Percentage';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Body mass' WHERE display_name = 'Body Mass';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Lean body mass' WHERE display_name = 'Lean Body Mass';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Step count' WHERE display_name = 'Step Count';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Distance walk/run' WHERE display_name = 'Distance Walk/Run';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Distance cycling' WHERE display_name = 'Distance Cycling';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Distance wheelchair' WHERE display_name = 'Distance Wheelchair';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Basal energy burned' WHERE display_name = 'Basal Energy Burned';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Active energy burned' WHERE display_name = 'Active Energy Burned';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Flight climbed' WHERE display_name = 'Flight Climbed';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Nike fuel' WHERE display_name = 'Nike Fuel';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Exercise time' WHERE display_name = 'Exercise Time';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Push count' WHERE display_name = 'Push Count';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Distance swimming' WHERE display_name = 'Distance Swimming';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Swimming stroke count' WHERE display_name = 'Swimming Stroke Count';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Heart rate' WHERE display_name = 'Heart Rate';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Body temperature' WHERE display_name = 'Body Temperature';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Basal body temperature' WHERE display_name = 'Basal Body Temperature';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Blood pressure systolic' WHERE display_name = 'Blood Pressure Systolic';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Blood pressure diastolic' WHERE display_name = 'Blood Pressure Diastolic';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Respiratory rate' WHERE display_name = 'Respiratory Rate';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Oxygen saturation' WHERE display_name = 'Oxygen Saturation';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Peripheral perfusion index' WHERE display_name = 'Peripheral Perfusion Index';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Blood glucose' WHERE display_name = 'Blood Glucose';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Electrodermal activity' WHERE display_name = 'Electrodermal Activity';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Inhaler usage' WHERE display_name = 'Inhaler Usage';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Blood alcohol count' WHERE display_name = 'Blood Alcohol Count';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Forced vital capacity' WHERE display_name = 'Forced Vital Capacity';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Forced expiratory volume' WHERE display_name = 'Forced Expiratory Volume';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Peak expiratory flow rate' WHERE display_name = 'Peak Expiratory Flow Rate';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary fat' WHERE display_name = 'Dietary Fat';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary fat polyunsaturated' WHERE display_name = 'Dietary Fat Polyunsaturated';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary fat monounsaturated' WHERE display_name = 'Dietary Fat Monounsaturated';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary fat saturated' WHERE display_name = 'Dietary Fat Saturated';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary cholestrol' WHERE display_name = 'Dietary Cholestrol';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary sodium' WHERE display_name = 'Dietary Sodium';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary carbohydrate' WHERE display_name = 'Dietary Carbohydrate';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary fiber' WHERE display_name = 'Dietary Fiber';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary sugar' WHERE display_name = 'Dietary Sugar';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary energy consumed' WHERE display_name = 'Dietary Energy Consumed';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary protein' WHERE display_name = 'Dietary Protein';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary vitamin A' WHERE display_name = 'Dietary Vitamin A';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary vitamin B6' WHERE display_name = 'Dietary Vitamin B6';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary vitamin B12' WHERE display_name = 'Dietary Vitamin B12';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary vitamin C' WHERE display_name = 'Dietary Vitamin C';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary vitamin D' WHERE display_name = 'Dietary Vitamin D';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary vitamin E' WHERE display_name = 'Dietary Vitamin E';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary vitamin K' WHERE display_name = 'Dietary Vitamin K';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary calcium' WHERE display_name = 'Dietary Calcium';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary iron' WHERE display_name = 'Dietary Iron';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary thiamin' WHERE display_name = 'Dietary Thiamin';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary riboflavin' WHERE display_name = 'Dietary Riboflavin';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary niacin' WHERE display_name = 'Dietary Niacin';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary folate' WHERE display_name = 'Dietary Folate';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary biotin' WHERE display_name = 'Dietary Biotin';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary pantothenic acid' WHERE display_name = 'Dietary Pantothenic Acid';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary phosphorus' WHERE display_name = 'Dietary Phosphorus';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary iodine' WHERE display_name = 'Dietary Iodine';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary magnesium' WHERE display_name = 'Dietary Magnesium';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary zinc' WHERE display_name = 'Dietary Zinc';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary selenium' WHERE display_name = 'Dietary Selenium';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary copper' WHERE display_name = 'Dietary Copper';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary manganese' WHERE display_name = 'Dietary Manganese';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary chromium' WHERE display_name = 'Dietary Chromium';

UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary molybdenum' WHERE display_name = 'Dietary Molybdenum';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary chloride' WHERE display_name = 'Dietary Chloride';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary potassium' WHERE display_name = 'Dietary Potassium';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary caffeine' WHERE display_name = 'Dietary Caffeine';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary water' WHERE display_name = 'Dietary Water';
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'UV exposure' WHERE display_name = 'UV Exposure';



UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Number of disks' WHERE display_name = 'Number of Disks';
UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Puzzle solved/unsolved' WHERE display_name = 'Puzzle Solved/Unsolved';
UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Number of moves' WHERE display_name = 'Number of Moves';
UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Initial span' WHERE display_name = 'Initial Span';
UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Minimum span' WHERE display_name = 'Minimum Span';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Maximum span' WHERE display_name = 'Maximum Span';
UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Play speed' WHERE display_name = 'Play Speed';
UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Maximum tests' WHERE display_name = 'Maximum Tests';
UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Maximum consecutive failures' WHERE display_name = 'Maximum Consecutive Failures';
UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Require reversal?' WHERE display_name = 'Require Reversal?';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Number of games' WHERE display_name = 'Number of Games';
UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Number of failures' WHERE display_name = 'Number of Failures';



UPDATE fda_hphc.active_task_list SET task_name = 'Fetal kick counter' WHERE task_name = 'Fetal Kick Counter';
UPDATE fda_hphc.active_task_list SET task_name = 'Tower of hanoi' WHERE task_name = 'Tower Of Hanoi';
UPDATE fda_hphc.active_task_list SET task_name = 'Spatial span memory' WHERE task_name = 'Spatial Span Memory';


/* Added start time and end time in active task and questionnaire regular schedule*/

UPDATE fda_hphc.active_task_custom_frequencies SET frequency_start_time = frequency_time
  WHERE frequency_time IS NOT NULL;

UPDATE fda_hphc.active_task_custom_frequencies SET frequency_end_time = frequency_time
  WHERE frequency_time IS NOT NULL;

ALTER TABLE fda_hphc.active_task_custom_frequencies  
  MODIFY frequency_start_time varchar(255) AFTER frequency_end_date;

ALTER TABLE fda_hphc.active_task_custom_frequencies  
  MODIFY frequency_end_time varchar(255) AFTER frequency_start_time;

ALTER TABLE fda_hphc.active_task_custom_frequencies  
  DROP COLUMN frequency_time;

UPDATE fda_hphc.questionnaires_custom_frequencies SET frequency_start_time = frequency_time
  WHERE frequency_time IS NOT NULL;

UPDATE fda_hphc.questionnaires_custom_frequencies SET frequency_end_time = frequency_time
  WHERE frequency_time IS NOT NULL;

ALTER TABLE fda_hphc.questionnaires_custom_frequencies  
MODIFY frequency_start_time varchar(255) AFTER frequency_end_date;

ALTER TABLE fda_hphc.questionnaires_custom_frequencies  
  MODIFY frequency_end_time varchar(255) AFTER frequency_start_time;

ALTER TABLE fda_hphc.questionnaires_custom_frequencies  
  DROP COLUMN frequency_time;

  
/* #1020 Data integrity checks missing from WCP and WCP-WS codebase and
#3114 Provision for import/export of studies*/

USE `fda_hphc`;

ALTER TABLE notification CONVERT TO CHARACTER SET UTF8;

ALTER TABLE questions CONVERT TO CHARACTER SET UTF8;

ALTER TABLE study_checklist CONVERT TO CHARACTER SET UTF8;

ALTER TABLE `active_task_attrtibutes_values` DROP FOREIGN KEY `FK_active_task_attrtibutes_values_active_task_master_attribute`;
ALTER TABLE `active_task_attrtibutes_values` CHANGE COLUMN `active_task_master_attr_id` `active_task_master_attr_id` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `active_task_master_attribute` 
CHANGE COLUMN `active_task_master_attr_id` `active_task_master_attr_id` VARCHAR(255) NOT NULL ;
ALTER TABLE `active_task_attrtibutes_values` ADD CONSTRAINT `FK_active_task_attrtibutes_values_active_task_master_attribute` 
FOREIGN KEY (`active_task_master_attr_id`) REFERENCES `active_task_master_attribute` (`active_task_master_attr_id`);

ALTER TABLE `questionnaires_frequencies` DROP FOREIGN KEY `FK_questionnaires_fre_id`;
ALTER TABLE `questionnaires_frequencies` CHANGE COLUMN `questionnaires_id` `questionnaires_id` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `questionnaires_steps` DROP FOREIGN KEY `FK_questionnaires_qsteps_id`;
ALTER TABLE `questionnaires_steps` CHANGE COLUMN `questionnaires_id` `questionnaires_id` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `questionnaires` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;
ALTER TABLE `questionnaires_frequencies` ADD CONSTRAINT `FK_questionnaires_fre_id` FOREIGN KEY (`questionnaires_id`) REFERENCES `questionnaires` (`id`);
ALTER TABLE `questionnaires_steps` ADD CONSTRAINT `FK_questionnaires_qsteps_id` FOREIGN KEY (`questionnaires_id`) REFERENCES `questionnaires` (`id`);

ALTER TABLE `users` DROP FOREIGN KEY `role_id`;
ALTER TABLE `users` CHANGE COLUMN `role_id` `role_id` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `roles` CHANGE COLUMN `role_id` `role_id` VARCHAR(255) NOT NULL ;
ALTER TABLE `users` ADD CONSTRAINT `role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`);

ALTER TABLE `eligibility_test_response` DROP FOREIGN KEY `destination_question`, DROP FOREIGN KEY `eligibility_test_id`;
ALTER TABLE `eligibility_test_response` CHANGE COLUMN `eligibility_test_id` `eligibility_test_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `destination_question` `destination_question` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `eligibility_test` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;
ALTER TABLE `eligibility_test_response` ADD CONSTRAINT `destination_question` FOREIGN KEY (`destination_question`) REFERENCES `eligibility_test` (`id`),
ADD CONSTRAINT `eligibility_test_id` FOREIGN KEY (`eligibility_test_id`) REFERENCES `eligibility_test` (`id`);

ALTER TABLE `eligibility_test` DROP FOREIGN KEY `FK_eligibility_id`;
ALTER TABLE `eligibility_test` CHANGE COLUMN `eligibility_id` `eligibility_id` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `eligibility` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;
ALTER TABLE `eligibility_test` ADD CONSTRAINT `FK_eligibility_id` FOREIGN KEY (`eligibility_id`) REFERENCES `eligibility` (`id`);

ALTER TABLE `active_task` DROP FOREIGN KEY `FK_study_active_task_id`;
ALTER TABLE `active_task` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `branding` DROP FOREIGN KEY `FK_study_branding_id`;
ALTER TABLE `branding` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `charts` DROP FOREIGN KEY `FK_study_charts_id`;
ALTER TABLE `charts` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `comprehension_test_question` DROP FOREIGN KEY `FK_comprehension_test_question_studies`;
ALTER TABLE `comprehension_test_question` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `consent` DROP FOREIGN KEY `FK_study_consent_id`;
ALTER TABLE `consent` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `consent_info` DROP FOREIGN KEY `FK_consent_info_studies`;
ALTER TABLE `consent_info` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `eligibility` DROP FOREIGN KEY `FK_el_study_id`;
ALTER TABLE `eligibility` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `groups` DROP FOREIGN KEY `FK_study_group_id`;
ALTER TABLE `groups` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `questionnaires` DROP FOREIGN KEY `FK_quest_study_id`;
ALTER TABLE `questionnaires` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `resources` DROP FOREIGN KEY `FK_study_resources_id`;
ALTER TABLE `resources` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `study_checklist` DROP FOREIGN KEY `FK1_study_checklist_id`;
ALTER TABLE `study_checklist` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `study_page` DROP FOREIGN KEY `study_id`;
ALTER TABLE `study_page` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `study_permission` DROP FOREIGN KEY `FK_study_id`;
ALTER TABLE `study_permission` CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `studies` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `active_task` ADD CONSTRAINT `FK_study_active_task_id`FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `branding` ADD CONSTRAINT `FK_study_branding_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `charts` ADD CONSTRAINT `FK_study_charts_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `comprehension_test_question` ADD CONSTRAINT `FK_comprehension_test_question_studies` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `consent` ADD CONSTRAINT `FK_study_consent_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `consent_info` ADD CONSTRAINT `FK_consent_info_studies` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `eligibility` ADD CONSTRAINT `FK_el_study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `groups` ADD CONSTRAINT `FK_study_group_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `questionnaires` ADD CONSTRAINT `FK_quest_study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `resources` ADD CONSTRAINT `FK_study_resources_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `study_checklist` ADD CONSTRAINT `FK1_study_checklist_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `study_page` ADD CONSTRAINT `study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `study_permission` ADD CONSTRAINT `FK_study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `active_task_master_attribute` DROP FOREIGN KEY `FK_active_task_master_attribute_active_task_list`;
ALTER TABLE `active_task_master_attribute` CHANGE COLUMN `task_type_id` `task_type_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `active_task_list` CHANGE COLUMN `active_task_list_id` `active_task_list_id` VARCHAR(255) NOT NULL ;
 
ALTER TABLE `active_task_master_attribute` ADD CONSTRAINT `FK_active_task_master_attribute_active_task_list` FOREIGN KEY (`task_type_id`) REFERENCES `active_task_list` (`active_task_list_id`);

ALTER TABLE `comprehension_test_response` DROP FOREIGN KEY `comprehension_test_question_id`;
ALTER TABLE `comprehension_test_response` CHANGE COLUMN `comprehension_test_question_id` `comprehension_test_question_id` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `comprehension_test_question` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;
ALTER TABLE `comprehension_test_response` ADD CONSTRAINT `comprehension_test_question_id` FOREIGN KEY (`comprehension_test_question_id`) REFERENCES `comprehension_test_question` (`id`);

ALTER TABLE `active_task_attrtibutes_values` DROP FOREIGN KEY `FK_active_task_attrtibutes_values_active_task`;
ALTER TABLE `active_task_attrtibutes_values` CHANGE COLUMN `active_task_id` `active_task_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `active_task_custom_frequencies` DROP FOREIGN KEY `active_task_id_FK`;
ALTER TABLE `active_task_custom_frequencies` CHANGE COLUMN `active_task_id` `active_task_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `active_task_frequencies` DROP FOREIGN KEY `FK_active_task_fre_id`, DROP FOREIGN KEY `FKBBE7F3598EB972DD`;
ALTER TABLE `active_task_frequencies` CHANGE COLUMN `active_task_id` `active_task_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `active_task_steps` DROP FOREIGN KEY `FK_active_task_steps_id`, DROP FOREIGN KEY `FKAFC1CAC68EB972DD`;
ALTER TABLE `active_task_steps` CHANGE COLUMN `active_task_id` `active_task_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `active_task` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `active_task_attrtibutes_values` ADD CONSTRAINT FK_active_task_attrtibutes_values_active_task FOREIGN KEY (active_task_id) REFERENCES active_task (id);
ALTER TABLE `active_task_custom_frequencies` ADD CONSTRAINT `active_task_id_FK` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`);
ALTER TABLE `active_task_frequencies` ADD CONSTRAINT `FK_active_task_fre_id` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`), ADD CONSTRAINT `FKBBE7F3598EB972DD` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`);
ALTER TABLE `active_task_steps` ADD CONSTRAINT `FK_active_task_steps_id` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`), ADD CONSTRAINT `FKAFC1CAC68EB972DD` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`);

ALTER TABLE `study_permission` DROP FOREIGN KEY `FK_user_id`;
ALTER TABLE `study_permission` CHANGE COLUMN `user_id` `user_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `user_permissions_users` DROP FOREIGN KEY `FK3CB60B19B9441C99`, DROP FOREIGN KEY `FK3CB60B1991B38899`;
ALTER TABLE `user_permissions_users` CHANGE COLUMN `users_user_id`  `users_user_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `user_permission_mapping` DROP FOREIGN KEY `FKFEC4BF526CC7DBD0`, DROP FOREIGN KEY `FKFEC4BF5294586FD0`;
ALTER TABLE `user_permission_mapping` CHANGE COLUMN `user_id` `user_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `users` CHANGE COLUMN `user_id` `user_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `study_permission` ADD CONSTRAINT `FK_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
ALTER TABLE `user_permissions_users` ADD CONSTRAINT `FK3CB60B19B9441C99` FOREIGN KEY (`users_user_id`) REFERENCES `users` (`user_id`), ADD CONSTRAINT `FK3CB60B1991B38899` FOREIGN KEY (`users_user_id`) REFERENCES `users` (`user_id`);
ALTER TABLE `user_permission_mapping` ADD CONSTRAINT `FKFEC4BF526CC7DBD0` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`), ADD CONSTRAINT `FKFEC4BF5294586FD0` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
ALTER TABLE `user_permissions_users` DROP FOREIGN KEY `FK3CB60B1986B4070C`, DROP FOREIGN KEY `FK3CB60B19B6DE1B0C`;
ALTER TABLE `user_permissions_users` CHANGE COLUMN `user_permissions_permission_id` `user_permissions_permission_id` VARCHAR(255) NOT NULL ;
ALTER TABLE `user_permission_mapping` DROP FOREIGN KEY `FKFEC4BF528CE62AFB`, DROP FOREIGN KEY `FKFEC4BF52BD103EFB`;
ALTER TABLE `user_permission_mapping` CHANGE COLUMN `permission_id` `permission_id` VARCHAR(255) NOT NULL ;
ALTER TABLE `user_permissions` CHANGE COLUMN `permission_id` `permission_id` VARCHAR(255) NOT NULL ;
ALTER TABLE `user_permissions_users` ADD CONSTRAINT `FK3CB60B1986B4070C` FOREIGN KEY (`user_permissions_permission_id`) REFERENCES `user_permissions` (`permission_id`), ADD CONSTRAINT `FK3CB60B19B6DE1B0C` FOREIGN KEY (`user_permissions_permission_id`) REFERENCES `user_permissions` (`permission_id`);
ALTER TABLE `user_permission_mapping` ADD CONSTRAINT `FKFEC4BF528CE62AFB` FOREIGN KEY (`permission_id`) REFERENCES `user_permissions` (`permission_id`), ADD CONSTRAINT `FKFEC4BF52BD103EFB` FOREIGN KEY (`permission_id`) REFERENCES `user_permissions` (`permission_id`);

ALTER TABLE `notification_history` DROP FOREIGN KEY `notification_history_id`;
ALTER TABLE `notification_history` CHANGE COLUMN `notification_id` `notification_id` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `notification` CHANGE COLUMN `notification_id` `notification_id` VARCHAR(255) NOT NULL ;
ALTER TABLE `notification_history` ADD CONSTRAINT `notification_history_id` FOREIGN KEY (`notification_id`) REFERENCES `notification` (`notification_id`);

ALTER TABLE `responses` DROP FOREIGN KEY `question_response_id`;
ALTER TABLE `responses` CHANGE COLUMN `question_id` `question_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `questions` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;
ALTER TABLE `responses` ADD CONSTRAINT `question_response_id` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`);

ALTER TABLE `rep_response` DROP FOREIGN KEY `FK_destination_question`, DROP FOREIGN KEY `FK_rep_questions_id`;
ALTER TABLE `rep_response` CHANGE COLUMN `questions_id` `questions_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `destination_question` `destination_question` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `rep_questions` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `rep_response` ADD CONSTRAINT `FK_destination_question` FOREIGN KEY (`destination_question`) REFERENCES `rep_questions` (`id`),
ADD CONSTRAINT `FK_rep_questions_id` FOREIGN KEY (`questions_id`) REFERENCES `rep_questions` (`id`);

ALTER TABLE `group_step_mapping` DROP FOREIGN KEY `step_id`;
ALTER TABLE `group_step_mapping` CHANGE COLUMN `step_id` `step_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `questionnaires_steps` CHANGE COLUMN `step_id` `step_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `group_step_mapping` ADD CONSTRAINT `step_id`FOREIGN KEY (`step_id`) REFERENCES `questionnaires_steps` (`step_id`);

ALTER TABLE `pie_chart_segments` DROP FOREIGN KEY `FK_pie_chart_id`;
ALTER TABLE `pie_chart_segments` CHANGE COLUMN `pie_chart_id` `pie_chart_id` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `pie_chart` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;
ALTER TABLE `pie_chart_segments` ADD CONSTRAINT `FK_pie_chart_id` FOREIGN KEY (`pie_chart_id`) REFERENCES `pie_chart` (`id`);

ALTER TABLE `live_active_task_details` DROP FOREIGN KEY `live_active_task_id`;
ALTER TABLE `live_active_task_details` CHANGE COLUMN `live_active_task_id` `live_active_task_id` VARCHAR(100) NULL DEFAULT NULL ;
ALTER TABLE `live_active_task` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;
ALTER TABLE `live_active_task_details` ADD CONSTRAINT `live_active_task_id` FOREIGN KEY (`live_active_task_id`) REFERENCES `live_active_task` (`id`);

ALTER TABLE `bar_chart_axis` DROP FOREIGN KEY `FK_bar_chart_id`;
ALTER TABLE `bar_chart_axis` CHANGE COLUMN `bar_chart_id` `bar_chart_id` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `bar_chart` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;
ALTER TABLE `bar_chart_axis` ADD CONSTRAINT `FK_bar_chart_id` FOREIGN KEY (`bar_chart_id`) REFERENCES `bar_chart` (`id`);

ALTER TABLE `group_step_mapping` DROP FOREIGN KEY `FK_group_step_mapping_id`;
ALTER TABLE `group_step_mapping` CHANGE COLUMN `group_id` `group_id` VARCHAR(255)  NULL DEFAULT NULL ;
ALTER TABLE `groups` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;
ALTER TABLE `group_step_mapping` ADD CONSTRAINT `FK_group_step_mapping_id` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`);

ALTER TABLE `line_chart_datasource` DROP FOREIGN KEY `FK_line_chart_datasource_line_chart`;
ALTER TABLE `line_chart_datasource` CHANGE COLUMN `line_chart_id` `line_chart_id` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `line_chart_x_axis` DROP FOREIGN KEY `FK_line_chart_x_axis_line_chart`;
ALTER TABLE `line_chart_x_axis` CHANGE COLUMN `line_chart_id` `line_chart_id` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `line_chart` CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;
ALTER TABLE `line_chart_datasource` ADD CONSTRAINT `FK_line_chart_datasource_line_chart` FOREIGN KEY (`line_chart_id`) REFERENCES `line_chart` (`id`);
ALTER TABLE `line_chart_x_axis` ADD CONSTRAINT `FK_line_chart_x_axis_line_chart` FOREIGN KEY (`line_chart_id`) REFERENCES `line_chart` (`id`);

ALTER TABLE `active_task` 
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `anchor_date_id` `anchor_date_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `task_type_id` `task_type_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `active_task_attrtibutes_values` 
CHANGE COLUMN `active_task_attribute_id` `active_task_attribute_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `active_task_custom_frequencies` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `active_task_frequencies` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `active_task_steps` 
CHANGE COLUMN `step_id` `step_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `activetask_formula` 
CHANGE COLUMN `activetask_formula_id` `activetask_formula_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `anchordate_type` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `comprehension_test_question` 
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `comprehension_test_response` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `consent` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `consent_info` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `consent_item_title_id` `consent_item_title_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `consent_master_info` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `eligibility` 
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `eligibility_test_response` 
CHANGE COLUMN `response_id` `response_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `enrollment_token` 
CHANGE COLUMN `token_id` `token_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `form` 
CHANGE COLUMN `form_id` `form_id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `form_mapping` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `form_id` `form_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `question_id` `question_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `health_kit_keys_info` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `instructions` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `master_data` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `notification` 
CHANGE COLUMN `active_task_id` `active_task_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `questionnarie_id` `questionnarie_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `resource_id` `resource_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `notification_history` 
CHANGE COLUMN `history_id` `history_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `questionnaires` 
CHANGE COLUMN `anchor_date_id` `anchor_date_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `questionnaires_custom_frequencies` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `questionnaires_id` `questionnaires_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `questionnaires_frequencies` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `questionnaires_steps` 
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `destination_step` `destination_step` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `instruction_form_id` `instruction_form_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `questions` 
CHANGE COLUMN `anchor_date_id` `anchor_date_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `stat_formula` `stat_formula` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `stat_type` `stat_type` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `reference_tables` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `resources` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `anchor_date_id` `anchor_date_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `response_sub_type_value` 
CHANGE COLUMN `response_sub_type_value_id` `response_sub_type_value_id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `destination_step_id` `destination_step_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `response_type_id` `response_type_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `response_type_value` 
CHANGE COLUMN `response_type_id` `response_type_id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `other_destination_step_id` `other_destination_step_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `questions_response_type_id` `questions_response_type_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `statistic_master_images` 
CHANGE COLUMN `statistic_image_id` `statistic_image_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `statistics` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `studies` 
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `study_activity_version` 
CHANGE COLUMN `study_activity_id` `study_activity_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `study_checklist` 
CHANGE COLUMN `checklist_id` `checklist_id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `study_page` 
CHANGE COLUMN `page_id` `page_id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `study_permission` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `study_sequence` 
CHANGE COLUMN `study_sequence_id` `study_sequence_id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `study_id` `study_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `study_version` 
CHANGE COLUMN `version_id` `version_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `user_attempts` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `users` 
CHANGE COLUMN `created_by` `created_by` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `modified_by` `modified_by` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `users_password_history` 
CHANGE COLUMN `password_history_id` `password_history_id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `user_id` `user_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `active_task_select_options` 
CHANGE COLUMN `active_task_select_options_id` `active_task_select_options_id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `active_task_master_attr_id` `active_task_master_attr_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `app_versions` 
CHANGE COLUMN `av_id` `av_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `version_info` 
CHANGE COLUMN `version_info_id` `version_info_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `branding` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `charts` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `gateway_info` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `gateway_welcome_info` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `legal_text` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `line_chart_datasource` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `line_chart_x_axis` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `question_condtion_branching` 
CHANGE COLUMN `condition_id` `condition_id` VARCHAR(255) NOT NULL ,
CHANGE COLUMN `question_id` `question_id` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `statistics` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `bar_chart_axis` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `group_step_mapping` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `users_temp` 
CHANGE COLUMN `user_temp_id` `user_temp_id` VARCHAR(255) NOT NULL ;

ALTER TABLE `start_complete_step` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `response_type_parameter_master` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `response_type_master` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `rep_resources` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `rep_response` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `reference_tables` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `question_responsetype_master_info` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `questions_response_type` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `pie_chart_segments` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `live_ active_task_data_collected_master` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `live_active_task_details` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `live_active_task_master` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;

ALTER TABLE `responses` 
CHANGE COLUMN `id` `id` VARCHAR(255) NOT NULL ;



-- PROCEDURE

DROP PROCEDURE IF EXISTS `deleteInActiveActivity`;
DELIMITER //
CREATE PROCEDURE `deleteInActiveActivity`(
	IN `studyId` VARCHAR(255)
)
BEGIN

DELETE
FROM active_task_attrtibutes_values
WHERE active=0 AND active_task_id IN(
SELECT id
FROM active_task
WHERE study_id=studyId);

DELETE
FROM active_task
WHERE active= 0 AND study_id=studyId;

DELETE
FROM questions
WHERE active =0 AND id IN(
SELECT question_id
FROM form_mapping
WHERE active=0 AND form_id IN (
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Form' AND questionnaires_id IN (
SELECT id
FROM questionnaires q
WHERE active=0 AND study_id=studyId)));

DELETE
FROM response_type_value
WHERE questions_response_type_id IN(
SELECT question_id
FROM form_mapping
WHERE active=0 AND form_id IN (
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Form' AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId)));

DELETE
FROM response_sub_type_value
WHERE response_type_id IN(
SELECT question_id
FROM form_mapping
WHERE active=0 AND form_id IN (
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Form' AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId)));

DELETE
FROM questions
WHERE active =0 AND id IN (
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Question' AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId));

DELETE
FROM response_type_value
WHERE questions_response_type_id IN(
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Question' AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId));

DELETE
FROM response_sub_type_value
WHERE response_type_id IN(
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Question' AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId));

DELETE
FROM instructions
WHERE active=0 AND id IN (
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId));

DELETE
FROM questionnaires_steps
WHERE active=0 AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId);

DELETE
FROM questionnaires_frequencies
WHERE questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId);

DELETE
FROM questionnaires_custom_frequencies
WHERE questionnaires_id IN(
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId);

DELETE
FROM questionnaires
WHERE active=0 AND study_id=studyId;

END//
DELIMITER ;

-- Dumping structure for procedure fda_hphc.deleteQuestionnaire
DROP PROCEDURE IF EXISTS `deleteQuestionnaire`;
DELIMITER //
CREATE PROCEDURE `deleteQuestionnaire`(
	IN `questionnaireId` VARCHAR(255),
	IN `modifiedOn` VARCHAR(255),
	IN `modifiedBy` VARCHAR(255),
	IN `studyId` VARCHAR(255)
)
BEGIN

update questionnaires qbo set qbo.active=0,qbo.modified_by=modifiedBy,qbo.modified_date=modifiedOn where qbo.study_id=studyId and qbo.id=questionnaireId and qbo.active=1;

update instructions ibo,questionnaires_steps qsbo set ibo.active=0,ibo.modified_by=modifiedBy,ibo.modified_on=modifiedOn where ibo.id=qsbo.instruction_form_id and qsbo.questionnaires_id=questionnaireId and qsbo.active=1 and qsbo.step_type='Instruction' and ibo.active=1;

update questions qbo,questionnaires_steps qsbo set qbo.active=0,qbo.modified_by=modifiedBy,qbo.modified_on=modifiedOn where
qbo.id=qsbo.instruction_form_id and qsbo.questionnaires_id=questionnaireId and qsbo.active=1 and qsbo.step_type='Question' and qbo.active=1; 

update questions qbo,form_mapping fmbo,questionnaires_steps qsbo  set qbo.active=0,qbo.modified_by=modifiedBy,qbo.modified_on=modifiedOn,fmbo.active=0 where qbo.id=fmbo.question_id and fmbo.form_id=qsbo.instruction_form_id and qsbo.questionnaires_id=questionnaireId and qsbo.step_type='Form' and qsbo.active=1 and qbo.active=1;

update form fbo,questionnaires_steps qsbo set fbo.active=0,fbo.modified_by=modifiedBy,fbo.modified_on=modifiedOn where fbo.form_id=qsbo.instruction_form_id and qsbo.step_type='Form' and qsbo.questionnaires_id=questionnaireId and qsbo.active=1 and fbo.active=1;

update questionnaires_steps qs set qs.active=0,qs.modified_by=modifiedBy,qs.modified_on=modifiedOn where qs.questionnaires_id=questionnaireId and qs.active=1;

END//
DELIMITER ;

-- Dumping structure for procedure fda_hphc.deleteQuestionnaireFrequencies
DROP PROCEDURE IF EXISTS `deleteQuestionnaireFrequencies`;
DELIMITER //
CREATE PROCEDURE `deleteQuestionnaireFrequencies`(
	IN `questionnaireId` VARCHAR(255)
)
BEGIN

delete from questionnaires_custom_frequencies where questionnaires_id=questionnaireId;
delete from questionnaires_frequencies where questionnaires_id=questionnaireId;

END//
DELIMITER ;

-- Dumping structure for procedure fda_hphc.deleteQuestionnaireStep
DROP PROCEDURE IF EXISTS `deleteQuestionnaireStep`;
DELIMITER //
CREATE PROCEDURE `deleteQuestionnaireStep`(
	IN `questionnaireId` VARCHAR(255),
	IN `modifiedOn` VARCHAR(255),
	IN `modifiedBy` VARCHAR(255),
	IN `sequenceNo` INT(11),
	IN `stepId` VARCHAR(255),
	IN `steptype` VARCHAR(255)
)
BEGIN
update questionnaires_steps qs set qs.sequence_no=qs.sequence_no-1,qs.modified_on=modifiedOn,qs.modified_by=modifiedBy where qs.questionnaires_id=questionnaireId and qs.active=1 and qs.sequence_no>=sequenceNo;

if steptype='Instruction' then
update instructions ibo set ibo.active=0,ibo.modified_by=modifiedBy,ibo.modified_on=modifiedOn where ibo.id=stepId and ibo.active=1;

elseif steptype='Question' then
update questions q set q.active=0,q.modified_by=modifiedBy,q.modified_on=modifiedOn where q.id=stepId and q.active=1;
update response_type_value rt set rt.active=0 where rt.questions_response_type_id=stepId and rt.active=1;
Update response_sub_type_value qrsbo set qrsbo.active=0 where qrsbo.response_type_id=stepId and qrsbo.active=1;

elseif steptype='Form' then
update questions QBO,form_mapping FMBO set QBO.active=0,QBO.modified_by=modifiedBy,QBO.modified_on=modifiedOn,FMBO.active=0 where QBO.id=FMBO.question_id and FMBO.form_id=stepId and QBO.active=1 and FMBO.active=1;
Update response_type_value QRBO,form_mapping FMBO set QRBO.active=0 where QRBO.questions_response_type_id=FMBO.question_id and FMBO.form_id=stepId and QRBO.active=1;
Update response_sub_type_value QRSBO,form_mapping FMBO set QRSBO.active=0 where QRSBO.response_type_id=FMBO.question_id and FMBO.form_id=stepId and QRSBO.active=1;
Update form fm set fm.active=0,fm.modified_by=modifiedBy,fm.modified_on=modifiedOn where fm.form_id=stepId and fm.active=1;

END IF;
END//
DELIMITER ;

