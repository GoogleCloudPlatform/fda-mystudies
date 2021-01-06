-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.7.25-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for fda_hphc
DROP DATABASE IF EXISTS `fda_hphc`;
CREATE DATABASE IF NOT EXISTS `fda_hphc` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `fda_hphc`;

-- Dumping structure for table fda_hphc.activetask_formula
DROP TABLE IF EXISTS `activetask_formula`;
CREATE TABLE IF NOT EXISTS `activetask_formula` (
  `activetask_formula_id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) DEFAULT NULL,
  `formula` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`activetask_formula_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task
DROP TABLE IF EXISTS `active_task`;
CREATE TABLE IF NOT EXISTS `active_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `frequency` varchar(255) DEFAULT NULL,
  `task_name` varchar(100) DEFAULT NULL,
  `duration` varchar(100) DEFAULT NULL,
  `repeat_questionnaire` int(11) DEFAULT NULL,
  `active_task_lifetime_start` date DEFAULT NULL,
  `active_task_lifetime_end` date DEFAULT NULL,
  `day_of_the_week` varchar(255) DEFAULT NULL,
  `repeat_active_task` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `instruction` varchar(255) DEFAULT NULL,
  `short_title` varchar(255) DEFAULT NULL,
  `created_date` varchar(255) DEFAULT NULL,
  `modified_date` varchar(255) DEFAULT NULL,
  `task_title` varchar(255) DEFAULT NULL,
  `task_type` int(11) DEFAULT NULL,
  `task_type_id` int(11) DEFAULT NULL,
  `action` tinyint(4) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `version` float DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `is_live` int(11) DEFAULT NULL,
  `is_Change` int(11) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `anchor_date_id` int(11) DEFAULT NULL,
  `schedule_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_active_task_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2850 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_attrtibutes_values
DROP TABLE IF EXISTS `active_task_attrtibutes_values`;
CREATE TABLE IF NOT EXISTS `active_task_attrtibutes_values` (
  `active_task_attribute_id` int(11) NOT NULL AUTO_INCREMENT,
  `active_task_id` int(11) NOT NULL DEFAULT '0',
  `active_task_master_attr_id` int(11) NOT NULL,
  `attribute_val` varchar(100) DEFAULT NULL,
  `add_to_line_chart` char(50) DEFAULT NULL,
  `time_range_chart` varchar(100) DEFAULT NULL,
  `rollback_chat` varchar(100) DEFAULT NULL,
  `title_chat` varchar(100) DEFAULT NULL,
  `use_for_statistic` char(1) DEFAULT NULL,
  `identifier_name_stat` varchar(100) DEFAULT NULL,
  `display_name_stat` varchar(100) DEFAULT NULL,
  `display_units_stat` varchar(100) DEFAULT NULL,
  `upload_type_stat` varchar(100) DEFAULT NULL,
  `formula_applied_stat` varchar(100) DEFAULT NULL,
  `time_range_stat` varchar(100) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`active_task_attribute_id`),
  KEY `FK_active_task_attrtibutes_values_active_task_master_attribute` (`active_task_master_attr_id`),
  KEY `FK_active_task_attrtibutes_values_active_task` (`active_task_id`),
  CONSTRAINT `FK_active_task_attrtibutes_values_active_task` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`),
  CONSTRAINT `FK_active_task_attrtibutes_values_active_task_master_attribute` FOREIGN KEY (`active_task_master_attr_id`) REFERENCES `active_task_master_attribute` (`active_task_master_attr_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7576 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_custom_frequencies
DROP TABLE IF EXISTS `active_task_custom_frequencies`;
CREATE TABLE IF NOT EXISTS `active_task_custom_frequencies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `frequency_start_date` date DEFAULT NULL,
  `frequency_end_date` date DEFAULT NULL,
  `frequency_time` time DEFAULT NULL,
  `active_task_id` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `is_used` char(1) DEFAULT NULL,
  `time_period_from_days` int(11) DEFAULT NULL,
  `time_period_to_days` int(11) DEFAULT NULL,
  `x_days_sign` bit(1) DEFAULT NULL,
  `y_days_sign` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `active_task_id_FK` (`active_task_id`),
  CONSTRAINT `active_task_id_FK` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_frequencies
DROP TABLE IF EXISTS `active_task_frequencies`;
CREATE TABLE IF NOT EXISTS `active_task_frequencies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `active_task_id` int(11) DEFAULT NULL,
  `frequency_date` date DEFAULT NULL,
  `frequency_time` time DEFAULT NULL,
  `is_launch_study` tinyint(1) DEFAULT NULL,
  `is_study_life_time` tinyint(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `time_period_from_days` int(11) DEFAULT NULL,
  `time_period_to_days` int(11) DEFAULT NULL,
  `x_days_sign` bit(1) DEFAULT NULL,
  `y_days_sign` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `active_task_id_idx` (`active_task_id`),
  KEY `FKBBE7F3598EB972DD` (`active_task_id`),
  CONSTRAINT `FKBBE7F3598EB972DD` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`),
  CONSTRAINT `FK_active_task_fre_id` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7239 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_list
DROP TABLE IF EXISTS `active_task_list`;
CREATE TABLE IF NOT EXISTS `active_task_list` (
  `active_task_list_id` int(11) NOT NULL AUTO_INCREMENT,
  `task_name` varchar(100) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`active_task_list_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_master_attribute
DROP TABLE IF EXISTS `active_task_master_attribute`;
CREATE TABLE IF NOT EXISTS `active_task_master_attribute` (
  `active_task_master_attr_id` int(11) NOT NULL AUTO_INCREMENT,
  `task_type_id` int(11) NOT NULL,
  `order_by` int(11) DEFAULT NULL,
  `attribute_type` varchar(100) DEFAULT NULL,
  `attribute_name` varchar(100) DEFAULT NULL,
  `display_name` varchar(250) DEFAULT NULL,
  `attribute_data_type` varchar(100) DEFAULT NULL,
  `add_to_dashboard` char(1) DEFAULT NULL,
  `task_type` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`active_task_master_attr_id`),
  KEY `FK_active_task_master_attribute_active_task_list` (`task_type_id`),
  CONSTRAINT `FK_active_task_master_attribute_active_task_list` FOREIGN KEY (`task_type_id`) REFERENCES `active_task_list` (`active_task_list_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_select_options
DROP TABLE IF EXISTS `active_task_select_options`;
CREATE TABLE IF NOT EXISTS `active_task_select_options` (
  `active_task_select_options_id` int(11) NOT NULL AUTO_INCREMENT,
  `active_task_master_attr_id` int(11) NOT NULL,
  `option_val` varchar(100) NOT NULL,
  PRIMARY KEY (`active_task_select_options_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_steps
DROP TABLE IF EXISTS `active_task_steps`;
CREATE TABLE IF NOT EXISTS `active_task_steps` (
  `step_id` int(11) NOT NULL,
  `active_task_id` int(11) DEFAULT NULL,
  `active_task_stepscol` varchar(45) DEFAULT NULL,
  `sd_live_form_id` varchar(45) DEFAULT NULL COMMENT 'start complete / live / question form / instruction',
  `sequence_no` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`step_id`),
  KEY `active_task_id_idx` (`active_task_id`),
  KEY `FKAFC1CAC68EB972DD` (`active_task_id`),
  CONSTRAINT `FKAFC1CAC68EB972DD` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`),
  CONSTRAINT `FK_active_task_steps_id` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.anchordate_type
DROP TABLE IF EXISTS `anchordate_type`;
CREATE TABLE IF NOT EXISTS `anchordate_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `has_anchortype_draft` int(11) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `version` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.app_versions
DROP TABLE IF EXISTS `app_versions`;
CREATE TABLE IF NOT EXISTS `app_versions` (
  `av_id` int(11) NOT NULL AUTO_INCREMENT,
  `app_version` float DEFAULT NULL,
  `created_on` timestamp NULL DEFAULT NULL,
  `force_update` int(11) DEFAULT NULL,
  `os_type` varchar(255) DEFAULT NULL,
  `bundle_id` varchar(255) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`av_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.bar_chart
DROP TABLE IF EXISTS `bar_chart`;
CREATE TABLE IF NOT EXISTS `bar_chart` (
  `id` int(11) NOT NULL,
  `data_source` int(11) DEFAULT NULL COMMENT 'question id / active task id',
  `range_type` tinyint(1) DEFAULT NULL COMMENT 'Time based / Other',
  `custom` tinyint(1) DEFAULT NULL COMMENT 'Y / N',
  `custom_start` datetime DEFAULT NULL,
  `custom_end` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.bar_chart_axis
DROP TABLE IF EXISTS `bar_chart_axis`;
CREATE TABLE IF NOT EXISTS `bar_chart_axis` (
  `id` int(11) NOT NULL,
  `bar_chart_id` int(11) DEFAULT NULL,
  `range_start` varchar(50) DEFAULT NULL,
  `range_end` varchar(50) DEFAULT NULL,
  `display_name` varchar(50) DEFAULT NULL,
  `bar_color` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bar_chart_id_idx` (`bar_chart_id`),
  CONSTRAINT `FK_bar_chart_id` FOREIGN KEY (`bar_chart_id`) REFERENCES `bar_chart` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.branding
DROP TABLE IF EXISTS `branding`;
CREATE TABLE IF NOT EXISTS `branding` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `background` varchar(20) DEFAULT NULL,
  `font` varchar(20) DEFAULT NULL,
  `tint` varchar(20) DEFAULT NULL,
  `logo_image_path` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_branding_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.charts
DROP TABLE IF EXISTS `charts`;
CREATE TABLE IF NOT EXISTS `charts` (
  `id` int(11) NOT NULL,
  `study_id` int(11) DEFAULT NULL,
  `reference_id` int(11) DEFAULT NULL COMMENT 'Pie chart id / Bar chart id .. etc',
  `chart_title` varchar(200) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  `chart_type` varchar(100) DEFAULT NULL COMMENT 'Pie Chart / Bar Chart / Line Chart ..etc',
  `time_range` varchar(50) DEFAULT NULL COMMENT 'current day / current week / current month / custom range',
  `allow_previous_next` tinyint(1) DEFAULT NULL COMMENT 'Y / N',
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_charts_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.comprehension_test_question
DROP TABLE IF EXISTS `comprehension_test_question`;
CREATE TABLE IF NOT EXISTS `comprehension_test_question` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `question_text` varchar(500) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  `structure_of_correct_ans` tinyint(1) DEFAULT NULL COMMENT '0 - Any of one marked as correct answers, 1 -  All of the ones marked as correct answers',
  `created_by` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `consent_id_idx` (`study_id`),
  CONSTRAINT `FK_comprehension_test_question_studies` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=532 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.comprehension_test_response
DROP TABLE IF EXISTS `comprehension_test_response`;
CREATE TABLE IF NOT EXISTS `comprehension_test_response` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comprehension_test_question_id` int(11) DEFAULT NULL,
  `response_option` varchar(500) DEFAULT NULL,
  `correct_answer` tinyint(1) DEFAULT NULL COMMENT '1 - Yes, 2 -  No',
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `comprehension_test_question_id_idx` (`comprehension_test_question_id`),
  CONSTRAINT `comprehension_test_question_id` FOREIGN KEY (`comprehension_test_question_id`) REFERENCES `comprehension_test_question` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1996 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.consent
DROP TABLE IF EXISTS `consent`;
CREATE TABLE IF NOT EXISTS `consent` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `comprehension_test_minimum_score` int(11) DEFAULT NULL,
  `share_data_permissions` varchar(50) DEFAULT NULL,
  `title` varchar(250) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `is_live` int(11) DEFAULT NULL,
  `tagline_description` varchar(250) DEFAULT NULL,
  `short_description` varchar(250) DEFAULT NULL,
  `long_description` varchar(550) DEFAULT NULL,
  `learn_more_text` longtext,
  `consent_doc_type` varchar(10) DEFAULT NULL,
  `consent_doc_content` longtext,
  `allow_without_permission` varchar(50) DEFAULT NULL,
  `e_consent_firstname` varchar(10) DEFAULT NULL,
  `e_consent_lastname` varchar(10) DEFAULT NULL,
  `e_consent_agree` varchar(10) DEFAULT NULL,
  `e_consent_signature` varchar(10) DEFAULT NULL,
  `e_consent_datetime` varchar(10) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `consent_document_type` varchar(50) DEFAULT NULL,
  `html_consent` varchar(255) DEFAULT NULL,
  `affirmation_text` varchar(255) DEFAULT NULL,
  `denial_text` varchar(255) DEFAULT NULL,
  `text_of_the_permission` varchar(255) DEFAULT NULL,
  `version` float DEFAULT NULL,
  `need_comprehension_test` varchar(255) DEFAULT NULL,
  `aggrement_of_consent` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_consent_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=341 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.consent_info
DROP TABLE IF EXISTS `consent_info`;
CREATE TABLE IF NOT EXISTS `consent_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `consent_item_type` varchar(50) DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `content_type` varchar(50) DEFAULT NULL,
  `brief_summary` longtext,
  `elaborated` longtext,
  `html_content` longtext,
  `url` varchar(200) DEFAULT NULL,
  `visual_step` tinytext,
  `sequence_no` int(11) DEFAULT '0',
  `created_by` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `display_title` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `consent_item_title_id` int(11) DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `is_live` int(11) DEFAULT NULL,
  `version` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `consent_id_idx` (`study_id`),
  CONSTRAINT `FK_consent_info_studies` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1343 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.consent_master_info
DROP TABLE IF EXISTS `consent_master_info`;
CREATE TABLE IF NOT EXISTS `consent_master_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `code` varchar(250) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- Dumping structure for table fda_hphc.eligibility
DROP TABLE IF EXISTS `eligibility`;
CREATE TABLE IF NOT EXISTS `eligibility` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `eligibility_mechanism` tinyint(2) DEFAULT NULL COMMENT '1 - ID validation only,\n2 - ID validation + Eligibility Test,\n3 - Eligibility Test only',
  `instructional_text` varchar(2500) DEFAULT NULL,
  `failure_outcome_text` varchar(2500) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_el_study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1058 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.eligibility_test
DROP TABLE IF EXISTS `eligibility_test`;
CREATE TABLE IF NOT EXISTS `eligibility_test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `eligibility_id` int(11) DEFAULT NULL,
  `short_title` varchar(200) DEFAULT NULL,
  `question` varchar(1000) DEFAULT NULL,
  `response_format` varchar(20) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  `status` tinyint(2) DEFAULT NULL,
  `eligibility_test` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `response_no_option` bit(1) DEFAULT NULL,
  `response_yes_option` bit(1) DEFAULT NULL,
  `is_used` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `eligibility_id_idx` (`eligibility_id`),
  CONSTRAINT `FK_eligibility_id` FOREIGN KEY (`eligibility_id`) REFERENCES `eligibility` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=937 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.eligibility_test_response
DROP TABLE IF EXISTS `eligibility_test_response`;
CREATE TABLE IF NOT EXISTS `eligibility_test_response` (
  `response_id` int(11) NOT NULL,
  `eligibility_test_id` int(11) DEFAULT NULL,
  `response_option` varchar(500) DEFAULT NULL,
  `pass_fail` varchar(20) DEFAULT NULL,
  `destination_question` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`response_id`),
  KEY `destination_question_idx` (`destination_question`),
  KEY `eligibility_test_id_idx` (`eligibility_test_id`),
  CONSTRAINT `destination_question` FOREIGN KEY (`destination_question`) REFERENCES `eligibility_test` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `eligibility_test_id` FOREIGN KEY (`eligibility_test_id`) REFERENCES `eligibility_test` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.enrollment_token
DROP TABLE IF EXISTS `enrollment_token`;
CREATE TABLE IF NOT EXISTS `enrollment_token` (
  `token_id` int(11) NOT NULL AUTO_INCREMENT,
  `enrollment_token` varchar(256) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  PRIMARY KEY (`token_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.form
DROP TABLE IF EXISTS `form`;
CREATE TABLE IF NOT EXISTS `form` (
  `form_id` int(11) NOT NULL AUTO_INCREMENT,
  `form_order` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  `question_type` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`form_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.form_mapping
DROP TABLE IF EXISTS `form_mapping`;
CREATE TABLE IF NOT EXISTS `form_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `form_id` int(11) DEFAULT NULL,
  `question_id` int(11) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.gateway_info
DROP TABLE IF EXISTS `gateway_info`;
CREATE TABLE IF NOT EXISTS `gateway_info` (
  `id` int(11) NOT NULL,
  `video_url` varchar(200) DEFAULT NULL,
  `email_inbox_address` varchar(100) DEFAULT NULL,
  `fda_website_url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.gateway_welcome_info
DROP TABLE IF EXISTS `gateway_welcome_info`;
CREATE TABLE IF NOT EXISTS `gateway_welcome_info` (
  `id` int(11) NOT NULL,
  `app_title` varchar(100) DEFAULT NULL,
  `description` longtext,
  `image_path` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.groups
DROP TABLE IF EXISTS `groups`;
CREATE TABLE IF NOT EXISTS `groups` (
  `id` int(11) NOT NULL,
  `group_name` varchar(100) DEFAULT NULL,
  `group_created_on` datetime DEFAULT NULL,
  `group_created_by` int(11) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_group_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.group_step_mapping
DROP TABLE IF EXISTS `group_step_mapping`;
CREATE TABLE IF NOT EXISTS `group_step_mapping` (
  `id` int(11) NOT NULL,
  `group_id` int(11) DEFAULT NULL,
  `step_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `group_id_idx` (`group_id`),
  KEY `step_id_idx` (`step_id`),
  CONSTRAINT `FK_group_step_mapping_id` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `step_id` FOREIGN KEY (`step_id`) REFERENCES `questionnaires_steps` (`step_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.health_kit_keys_info
DROP TABLE IF EXISTS `health_kit_keys_info`;
CREATE TABLE IF NOT EXISTS `health_kit_keys_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `key_text` varchar(255) DEFAULT NULL,
  `result_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.instructions
DROP TABLE IF EXISTS `instructions`;
CREATE TABLE IF NOT EXISTS `instructions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instruction_title` varchar(250) DEFAULT NULL,
  `instruction_text` varchar(2500) DEFAULT NULL,
  `button_text` varchar(150) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6218 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.legal_text
DROP TABLE IF EXISTS `legal_text`;
CREATE TABLE IF NOT EXISTS `legal_text` (
  `id` int(11) NOT NULL,
  `mobile_app_terms` longtext,
  `mobile_app_terms_modified_datetime` datetime DEFAULT NULL,
  `mobile_app_privacy_policy` longtext,
  `mobile_app_privacy_policy_modified_datetime` datetime DEFAULT NULL,
  `web_app_terms` longtext,
  `web_app_terms_modified_datetime` datetime DEFAULT NULL,
  `web_app_privacy_policy` longtext,
  `web_app_privacy_policy_modified_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.line_chart
DROP TABLE IF EXISTS `line_chart`;
CREATE TABLE IF NOT EXISTS `line_chart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `line_chartcol` varchar(45) DEFAULT NULL,
  `no_data_text` varchar(100) DEFAULT NULL,
  `show_ver_hor_line` tinyint(1) DEFAULT NULL,
  `x_axis_color` varchar(10) DEFAULT NULL,
  `y_axis_color` varchar(10) DEFAULT NULL,
  `animation_needed` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.line_chart_datasource
DROP TABLE IF EXISTS `line_chart_datasource`;
CREATE TABLE IF NOT EXISTS `line_chart_datasource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data_source_id` int(11) DEFAULT NULL,
  `plot_color` varchar(10) DEFAULT NULL,
  `line_chart_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_line_chart_datasource_line_chart` (`line_chart_id`),
  CONSTRAINT `FK_line_chart_datasource_line_chart` FOREIGN KEY (`line_chart_id`) REFERENCES `line_chart` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.line_chart_x_axis
DROP TABLE IF EXISTS `line_chart_x_axis`;
CREATE TABLE IF NOT EXISTS `line_chart_x_axis` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) DEFAULT NULL,
  `line_chart_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_line_chart_x_axis_line_chart` (`line_chart_id`),
  CONSTRAINT `FK_line_chart_x_axis_line_chart` FOREIGN KEY (`line_chart_id`) REFERENCES `line_chart` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.live_ active_task_data_collected_master
DROP TABLE IF EXISTS `live_ active_task_data_collected_master`;
CREATE TABLE IF NOT EXISTS `live_ active_task_data_collected_master` (
  `id` int(11) NOT NULL,
  `task_name` varchar(100) DEFAULT NULL,
  `data_collected` varchar(250) DEFAULT NULL COMMENT 'eg. Device motion, Pedometer, Location, Heart rate',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.live_active_task
DROP TABLE IF EXISTS `live_active_task`;
CREATE TABLE IF NOT EXISTS `live_active_task` (
  `id` int(11) NOT NULL,
  `live_task_description` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.live_active_task_details
DROP TABLE IF EXISTS `live_active_task_details`;
CREATE TABLE IF NOT EXISTS `live_active_task_details` (
  `id` int(11) NOT NULL,
  `live_active_task_id` int(11) DEFAULT NULL,
  `parameter` varchar(100) DEFAULT NULL,
  `parameter_display_name` varchar(100) DEFAULT NULL,
  `parameter_description` varchar(1000) DEFAULT NULL,
  `editable` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `live_active_task_id_idx` (`live_active_task_id`),
  CONSTRAINT `live_active_task_id` FOREIGN KEY (`live_active_task_id`) REFERENCES `live_active_task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.live_active_task_master
DROP TABLE IF EXISTS `live_active_task_master`;
CREATE TABLE IF NOT EXISTS `live_active_task_master` (
  `id` int(11) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `task_name` varchar(100) DEFAULT NULL,
  `parameter` varchar(100) DEFAULT NULL,
  `parameter_display_name` varchar(100) DEFAULT NULL,
  `Parameter_description` varchar(1000) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  `editable` tinyint(1) DEFAULT NULL,
  `parameter_type` varchar(50) DEFAULT NULL COMMENT 'String / Number / Int',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.master_data
DROP TABLE IF EXISTS `master_data`;
CREATE TABLE IF NOT EXISTS `master_data` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) DEFAULT NULL,
  `terms_text` text,
  `privacy_policy_text` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.notification
DROP TABLE IF EXISTS `notification`;
CREATE TABLE IF NOT EXISTS `notification` (
  `notification_id` int(11) NOT NULL AUTO_INCREMENT,
  `notification_type` varchar(255) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `notification_subType` varchar(255) DEFAULT NULL,
  `is_anchor_date` tinyint(1) DEFAULT NULL,
  `resource_id` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` timestamp NULL DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` timestamp NULL DEFAULT NULL,
  `schedule_date` date DEFAULT NULL,
  `schedule_time` time DEFAULT NULL,
  `notification_action` tinyint(1) DEFAULT NULL,
  `notification_done` tinyint(1) DEFAULT NULL,
  `notification_schedule_type` varchar(255) DEFAULT NULL,
  `notification_sent` tinyint(1) DEFAULT NULL,
  `notification_status` tinyint(1) DEFAULT NULL,
  `notification_text` varchar(1024) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `x_days` int(11) DEFAULT NULL,
  `questionnarie_id` int(11) DEFAULT NULL,
  `active_task_id` int(11) DEFAULT NULL,
  `app_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1511 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.notification_history
DROP TABLE IF EXISTS `notification_history`;
CREATE TABLE IF NOT EXISTS `notification_history` (
  `history_id` int(11) NOT NULL AUTO_INCREMENT,
  `notification_sent_date_time` varchar(50) DEFAULT NULL,
  `notification_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`history_id`),
  KEY `notification_history_id` (`notification_id`),
  CONSTRAINT `notification_history_id` FOREIGN KEY (`notification_id`) REFERENCES `notification` (`notification_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=165 DEFAULT CHARSET=utf8;


-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.pie_chart
DROP TABLE IF EXISTS `pie_chart`;
CREATE TABLE IF NOT EXISTS `pie_chart` (
  `id` int(11) NOT NULL,
  `data_source` int(11) DEFAULT NULL COMMENT 'question id / active task id',
  `distribution_type` tinyint(1) DEFAULT NULL COMMENT 'U - Unique responses, P - Pre defined range',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.pie_chart_segments
DROP TABLE IF EXISTS `pie_chart_segments`;
CREATE TABLE IF NOT EXISTS `pie_chart_segments` (
  `id` int(11) NOT NULL,
  `min_range` int(11) DEFAULT NULL,
  `max_range` int(11) DEFAULT NULL,
  `display_name` varchar(100) DEFAULT NULL,
  `segment_color` varchar(10) DEFAULT NULL,
  `pie_chart_id` int(11) DEFAULT NULL,
  `data_type` varchar(100) DEFAULT NULL COMMENT 'Device Motion',
  `choose_data` varchar(100) DEFAULT NULL COMMENT 'Step count',
  PRIMARY KEY (`id`),
  KEY `pie_chart_id_idx` (`pie_chart_id`),
  CONSTRAINT `FK_pie_chart_id` FOREIGN KEY (`pie_chart_id`) REFERENCES `pie_chart` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questionnaires
DROP TABLE IF EXISTS `questionnaires`;
CREATE TABLE IF NOT EXISTS `questionnaires` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `frequency` varchar(30) DEFAULT NULL,
  `title` varchar(500) DEFAULT NULL,
  `study_lifetime_start` date DEFAULT NULL,
  `study_lifetime_end` date DEFAULT NULL,
  `short_title` varchar(255) DEFAULT NULL,
  `day_of_the_week` varchar(255) DEFAULT NULL,
  `repeat_questionnaire` int(11) DEFAULT '0',
  `created_by` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `branching` bit(1) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `modifiedDate` varchar(255) DEFAULT NULL,
  `modifiedBy` varchar(255) DEFAULT NULL,
  `createdDate` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `is_live` int(11) DEFAULT NULL,
  `version` float DEFAULT NULL,
  `is_Change` tinyint(1) DEFAULT NULL,
  `schedule_type` varchar(50) DEFAULT NULL,
  `anchor_date_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_quest_study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11093 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questionnaires_custom_frequencies
DROP TABLE IF EXISTS `questionnaires_custom_frequencies`;
CREATE TABLE IF NOT EXISTS `questionnaires_custom_frequencies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `frequency_start_date` date DEFAULT NULL,
  `frequency_end_date` date DEFAULT NULL,
  `frequency_time` time DEFAULT NULL,
  `questionnaires_id` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `is_used` char(1) DEFAULT NULL,
  `time_period_from_days` int(11) DEFAULT NULL,
  `time_period_to_days` int(11) DEFAULT NULL,
  `x_days_sign` bit(1) DEFAULT NULL,
  `y_days_sign` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questionnaires_frequencies
DROP TABLE IF EXISTS `questionnaires_frequencies`;
CREATE TABLE IF NOT EXISTS `questionnaires_frequencies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `questionnaires_id` int(11) DEFAULT NULL,
  `frequency_date` date DEFAULT NULL,
  `frequency_time` time DEFAULT NULL,
  `is_launch_study` tinyint(1) DEFAULT NULL,
  `is_study_life_time` tinyint(1) DEFAULT NULL,
  `repeat_questionnaire` int(11) DEFAULT NULL,
  `hours_intervals` int(11) DEFAULT NULL,
  `day_of_the_week` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `time_period_from_days` int(11) DEFAULT NULL,
  `time_period_to_days` int(11) DEFAULT NULL,
  `x_days_sign` bit(1) DEFAULT NULL,
  `y_days_sign` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `questionnaires_id_idx` (`questionnaires_id`),
  CONSTRAINT `FK_questionnaires_fre_id` FOREIGN KEY (`questionnaires_id`) REFERENCES `questionnaires` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=13711 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questionnaires_steps
DROP TABLE IF EXISTS `questionnaires_steps`;
CREATE TABLE IF NOT EXISTS `questionnaires_steps` (
  `step_id` int(11) NOT NULL AUTO_INCREMENT,
  `questionnaires_id` int(11) DEFAULT NULL,
  `instruction_form_id` int(11) DEFAULT NULL COMMENT 'Instruction Id / Form Id',
  `step_short_title` varchar(255) DEFAULT NULL,
  `step_type` varchar(50) DEFAULT NULL COMMENT 'Instuction/Form/Question',
  `randomization` varchar(1) DEFAULT NULL COMMENT 'Y / N',
  `sequence_no` int(11) DEFAULT NULL,
  `id` int(11) DEFAULT NULL,
  `destination_step` int(11) DEFAULT NULL,
  `repeatable` varchar(255) DEFAULT NULL,
  `repeatable_text` varchar(255) DEFAULT NULL,
  `skiappable` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`step_id`),
  KEY `questionnaires_id_idx` (`questionnaires_id`),
  CONSTRAINT `FK_questionnaires_qsteps_id` FOREIGN KEY (`questionnaires_id`) REFERENCES `questionnaires` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=60022 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questions
DROP TABLE IF EXISTS `questions`;
CREATE TABLE IF NOT EXISTS `questions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `add_line_chart` varchar(255) DEFAULT NULL,
  `allow_rollback_chart` varchar(255) DEFAULT NULL,
  `chart_title` varchar(255) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `line_chart_timerange` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  `question` varchar(512) DEFAULT NULL,
  `response_type` int(11) DEFAULT NULL,
  `short_title` varchar(255) DEFAULT NULL,
  `skippable` varchar(255) DEFAULT NULL,
  `stat_display_name` varchar(255) DEFAULT NULL,
  `stat_diaplay_units` varchar(255) DEFAULT NULL,
  `stat_formula` int(11) DEFAULT NULL,
  `stat_short_name` varchar(255) DEFAULT NULL,
  `stat_type` int(11) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `use_anchor_date` bit(1) DEFAULT NULL,
  `use_stastic_data` varchar(255) DEFAULT NULL,
  `allow_healthkit` varchar(255) DEFAULT NULL,
  `healthkit_datatype` varchar(255) DEFAULT NULL,
  `anchor_date_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=84462 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questions_response_type
DROP TABLE IF EXISTS `questions_response_type`;
CREATE TABLE IF NOT EXISTS `questions_response_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parameter_name` varchar(255) DEFAULT NULL,
  `parameter_value` varchar(255) DEFAULT NULL,
  `question_id` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.question_condtion_branching
DROP TABLE IF EXISTS `question_condtion_branching`;
CREATE TABLE IF NOT EXISTS `question_condtion_branching` (
  `condition_id` int(11) NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `input_type` varchar(255) DEFAULT NULL,
  `input_type_value` varchar(255) DEFAULT NULL,
  `parent_sequence_no` int(11) DEFAULT NULL,
  `question_id` int(11) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  PRIMARY KEY (`condition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.question_responsetype_master_info
DROP TABLE IF EXISTS `question_responsetype_master_info`;
CREATE TABLE IF NOT EXISTS `question_responsetype_master_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `anchor_date` bit(1) DEFAULT NULL,
  `choice_based_branching` bit(1) DEFAULT NULL,
  `dashboard_allowed` bit(1) DEFAULT NULL,
  `data_type` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `formula_based_logic` bit(1) DEFAULT NULL,
  `healthkit_alternative` bit(1) DEFAULT NULL,
  `response_type` varchar(255) DEFAULT NULL,
  `response_type_code` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.reference_tables
DROP TABLE IF EXISTS `reference_tables`;
CREATE TABLE IF NOT EXISTS `reference_tables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `str_value` varchar(100) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL COMMENT 'Roles / Categories / Research Sponsors / Response formats ',
  `type` varchar(50) DEFAULT NULL COMMENT 'Pre-defined / Custom',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.rep_questions
DROP TABLE IF EXISTS `rep_questions`;
CREATE TABLE IF NOT EXISTS `rep_questions` (
  `id` int(11) NOT NULL,
  `short_title` varchar(200) DEFAULT NULL,
  `question` varchar(1000) DEFAULT NULL,
  `response_format` varchar(20) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL COMMENT 'Eligibility / Questionnaire',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.rep_resources
DROP TABLE IF EXISTS `rep_resources`;
CREATE TABLE IF NOT EXISTS `rep_resources` (
  `id` int(11) NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `text_or_pdf` tinyint(1) DEFAULT NULL,
  `rich_text` mediumtext,
  `pdf_url` varchar(200) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL COMMENT 'Study / Gateway',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.rep_response
DROP TABLE IF EXISTS `rep_response`;
CREATE TABLE IF NOT EXISTS `rep_response` (
  `id` int(11) NOT NULL,
  `questions_id` int(11) DEFAULT NULL,
  `response_option` varchar(500) DEFAULT NULL,
  `destination_question` int(11) DEFAULT NULL,
  `result` varchar(5) DEFAULT NULL COMMENT 'Pass / Fail',
  PRIMARY KEY (`id`),
  KEY `rep_questions_id_idx` (`questions_id`),
  KEY `destination_question_idx` (`destination_question`),
  CONSTRAINT `FK_destination_question` FOREIGN KEY (`destination_question`) REFERENCES `rep_questions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_rep_questions_id` FOREIGN KEY (`questions_id`) REFERENCES `rep_questions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.resources
DROP TABLE IF EXISTS `resources`;
CREATE TABLE IF NOT EXISTS `resources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `text_or_pdf` tinyint(1) DEFAULT NULL,
  `rich_text` mediumtext,
  `pdf_url` varchar(200) DEFAULT NULL,
  `pdfName` varchar(200) DEFAULT NULL,
  `resource_visibility` tinyint(1) DEFAULT NULL,
  `time_period_from_days` int(11) DEFAULT NULL,
  `time_period_to_days` int(11) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `resource_text` varchar(255) DEFAULT NULL,
  `action` tinyint(1) DEFAULT NULL,
  `study_protocol` tinyint(1) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `pdf_name` varchar(255) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `resource_type` tinyint(1) DEFAULT NULL,
  `anchor_date` varchar(255) DEFAULT NULL,
  `x_days_sign` tinyint(1) DEFAULT '0',
  `y_days_sign` tinyint(1) DEFAULT '0',
  `sequence_no` int(11) DEFAULT NULL,
  `anchor_date_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_resources_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10508 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.responses
DROP TABLE IF EXISTS `responses`;
CREATE TABLE IF NOT EXISTS `responses` (
  `id` int(11) NOT NULL,
  `question_id` int(11) DEFAULT NULL,
  `response_option` varchar(100) DEFAULT NULL,
  `destination_step` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `question_id_idx` (`question_id`),
  CONSTRAINT `question_response_id` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.response_sub_type_value
DROP TABLE IF EXISTS `response_sub_type_value`;
CREATE TABLE IF NOT EXISTS `response_sub_type_value` (
  `response_sub_type_value_id` int(11) NOT NULL AUTO_INCREMENT,
  `destination_step_id` int(11) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `exclusive` varchar(50) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `response_type_id` int(11) DEFAULT NULL,
  `selected_image` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `image_content` tinyblob,
  `selected_image_content` longblob,
  `description` varchar(255) DEFAULT NULL,
  `operator` varchar(255) DEFAULT NULL,
  `value_of_x` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`response_sub_type_value_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1368 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.response_type_master
DROP TABLE IF EXISTS `response_type_master`;
CREATE TABLE IF NOT EXISTS `response_type_master` (
  `id` int(11) NOT NULL,
  `response_type_option` varchar(100) DEFAULT NULL COMMENT 'question-scale / question-continuousScale / question-textScale\n\n',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.response_type_parameter_master
DROP TABLE IF EXISTS `response_type_parameter_master`;
CREATE TABLE IF NOT EXISTS `response_type_parameter_master` (
  `id` int(11) NOT NULL,
  `question_type` varchar(100) DEFAULT NULL COMMENT 'question-scale / question-continuousScale / question-textScale .. etc',
  `parameter` varchar(100) DEFAULT NULL COMMENT 'maxValue / minValue / default',
  `parameter_type` varchar(100) DEFAULT NULL COMMENT 'Int / Boolean / String / Number',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.response_type_value
DROP TABLE IF EXISTS `response_type_value`;
CREATE TABLE IF NOT EXISTS `response_type_value` (
  `response_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `questions_response_type_id` int(11) DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  `default_date` varchar(255) DEFAULT NULL,
  `default_value` varchar(255) DEFAULT NULL,
  `image_size` varchar(255) DEFAULT NULL,
  `invalid_message` varchar(255) DEFAULT NULL,
  `max_date` varchar(255) DEFAULT NULL,
  `max_desc` varchar(255) DEFAULT NULL,
  `max_fraction_digits` int(11) DEFAULT NULL,
  `max_image` varchar(255) DEFAULT NULL,
  `max_length` int(50) DEFAULT NULL,
  `max_value` varchar(50) DEFAULT NULL,
  `measurement_system` varchar(255) DEFAULT NULL,
  `min_date` varchar(255) DEFAULT NULL,
  `min_desc` varchar(255) DEFAULT NULL,
  `min_image` varchar(255) DEFAULT NULL,
  `min_value` varchar(50) DEFAULT NULL,
  `multiple_lines` bit(1) DEFAULT NULL,
  `placeholder` varchar(255) DEFAULT NULL,
  `selection_style` varchar(255) DEFAULT NULL,
  `step` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `style` varchar(255) DEFAULT NULL,
  `text_choices` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `use_current_location` bit(1) DEFAULT NULL,
  `validation_regex` text,
  `vertical` bit(1) DEFAULT NULL,
  `defalut_time` varchar(255) DEFAULT NULL,
  `formula_based_logic` varchar(255) DEFAULT NULL,
  `validation_characters` varchar(255) DEFAULT NULL,
  `validation_condition` varchar(255) DEFAULT NULL,
  `validation_except_text` text,
  `condition_formula` varchar(255) DEFAULT NULL,
  `other_description` varchar(255) DEFAULT NULL,
  `other_destination_step_id` int(11) DEFAULT NULL,
  `other_exclusive` varchar(255) DEFAULT NULL,
  `other_include_text` varchar(255) DEFAULT NULL,
  `other_participant_fill` varchar(255) DEFAULT NULL,
  `other_placeholder_text` varchar(255) DEFAULT NULL,
  `other_text` varchar(255) DEFAULT NULL,
  `other_type` varchar(255) DEFAULT NULL,
  `other_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`response_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=436 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.roles
DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.start_complete_step
DROP TABLE IF EXISTS `start_complete_step`;
CREATE TABLE IF NOT EXISTS `start_complete_step` (
  `id` int(11) NOT NULL,
  `start_complete_step` varchar(50) DEFAULT NULL COMMENT 'start / complete',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.statistics
DROP TABLE IF EXISTS `statistics`;
CREATE TABLE IF NOT EXISTS `statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `short_title` varchar(100) DEFAULT NULL,
  `display_name` varchar(100) DEFAULT NULL,
  `stat_type` varchar(100) DEFAULT NULL,
  `display_unit` varchar(100) DEFAULT NULL,
  `formula` varchar(45) DEFAULT NULL,
  `data_source` int(11) DEFAULT NULL,
  `time_range` varchar(50) DEFAULT NULL,
  `custom` tinyint(1) DEFAULT NULL,
  `custom_start` datetime DEFAULT NULL,
  `custom_end` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.statistic_master_images
DROP TABLE IF EXISTS `statistic_master_images`;
CREATE TABLE IF NOT EXISTS `statistic_master_images` (
  `statistic_image_id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`statistic_image_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.studies
DROP TABLE IF EXISTS `studies`;
CREATE TABLE IF NOT EXISTS `studies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `custom_study_id` varchar(50) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `full_name` varchar(250) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `platform` varchar(20) DEFAULT NULL,
  `category` varchar(200) DEFAULT NULL,
  `research_sponsor` varchar(200) DEFAULT NULL,
  `tentative_duration` int(11) DEFAULT NULL,
  `tentative_duration_weekmonth` varchar(20) DEFAULT NULL,
  `description` longtext,
  `enrolling_participants` varchar(3) DEFAULT NULL,
  `retain_participant` varchar(50) DEFAULT NULL,
  `allow_rejoin` varchar(3) DEFAULT NULL,
  `irb_review` varchar(3) DEFAULT NULL,
  `inbox_email_address` varchar(500) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `status` tinytext,
  `sequence_number` varchar(255) DEFAULT NULL,
  `thumbnail_image` varchar(255) DEFAULT NULL,
  `media_link` varchar(500) DEFAULT NULL,
  `allow_rejoin_text` varchar(255) DEFAULT NULL,
  `study_website` varchar(255) DEFAULT NULL,
  `study_tagline` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `study_lunched_date` varchar(255) DEFAULT NULL,
  `study_pre_active_flag` char(1) DEFAULT NULL,
  `has_activity_draft` int(11) DEFAULT NULL,
  `has_consent_draft` int(11) DEFAULT NULL,
  `has_study_draft` int(11) DEFAULT NULL,
  `is_live` int(11) DEFAULT NULL,
  `version` float DEFAULT NULL,
  `has_activitetask_draft` int(11) DEFAULT NULL,
  `has_questionnaire_draft` int(11) DEFAULT NULL,
  `enrollmentdate_as_anchordate` char(1) DEFAULT NULL,
  `app_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1063 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_activity_version
DROP TABLE IF EXISTS `study_activity_version`;
CREATE TABLE IF NOT EXISTS `study_activity_version` (
  `study_activity_id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_id` int(11) DEFAULT NULL,
  `activity_type` varchar(255) DEFAULT NULL,
  `activity_version` float DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `short_title` varchar(255) DEFAULT NULL,
  `study_version` float DEFAULT NULL,
  PRIMARY KEY (`study_activity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8095 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_checklist
DROP TABLE IF EXISTS `study_checklist`;
CREATE TABLE IF NOT EXISTS `study_checklist` (
  `checklist_id` int(10) NOT NULL AUTO_INCREMENT,
  `study_id` int(10) DEFAULT NULL,
  `checkbox1` tinyint(4) DEFAULT NULL,
  `checkbox2` tinyint(4) DEFAULT NULL,
  `checkbox3` tinyint(4) DEFAULT NULL,
  `checkbox4` tinyint(4) DEFAULT NULL,
  `checkbox5` tinyint(4) DEFAULT NULL,
  `checkbox6` tinyint(4) DEFAULT NULL,
  `checkbox7` tinyint(4) DEFAULT NULL,
  `checkbox8` tinyint(4) DEFAULT NULL,
  `checkbox9` tinyint(4) DEFAULT NULL,
  `checkbox10` tinyint(4) DEFAULT NULL,
  `study_version` varchar(255) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `checkbox11` bit(1) DEFAULT NULL,
  `checkbox12` bit(1) DEFAULT NULL,
  PRIMARY KEY (`checklist_id`),
  KEY `FK1_study_checklist_id` (`study_id`),
  CONSTRAINT `FK1_study_checklist_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_page
DROP TABLE IF EXISTS `study_page`;
CREATE TABLE IF NOT EXISTS `study_page` (
  `page_id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `image_path` varchar(100) DEFAULT NULL,
  `description` longtext,
  `created_on` datetime DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`page_id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1866 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_permission
DROP TABLE IF EXISTS `study_permission`;
CREATE TABLE IF NOT EXISTS `study_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `view_permission` tinyint(1) DEFAULT NULL COMMENT '0 - View only, 1 - View and Edit',
  `project_lead` varchar(11) DEFAULT NULL COMMENT 'Y - Yes, N -  No(userId we need to store)',
  `delFlag` tinyint(1) DEFAULT NULL COMMENT '0 - inactive, 1 - active',
  PRIMARY KEY (`id`),
  KEY `user_id_idx` (`user_id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4700 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_sequence
DROP TABLE IF EXISTS `study_sequence`;
CREATE TABLE IF NOT EXISTS `study_sequence` (
  `study_sequence_id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `actions` char(1) DEFAULT NULL,
  `basic_info` char(1) DEFAULT NULL,
  `check_list` char(1) DEFAULT NULL,
  `comprehension_test` char(1) DEFAULT NULL,
  `consent_edu_info` char(1) DEFAULT NULL,
  `e_consent` char(1) DEFAULT NULL,
  `eligibility` char(1) DEFAULT NULL,
  `miscellaneous_branding` char(1) DEFAULT NULL,
  `miscellaneous_notification` char(1) DEFAULT NULL,
  `miscellaneous_resources` char(1) DEFAULT NULL,
  `over_view` char(1) DEFAULT NULL,
  `setting_admins` char(1) DEFAULT NULL,
  `study_dashboard_chart` char(1) DEFAULT NULL,
  `study_dashboard_stats` char(1) DEFAULT NULL,
  `study_exc_active_task` char(1) DEFAULT NULL,
  `study_exc_questionnaries` char(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`study_sequence_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1063 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_version
DROP TABLE IF EXISTS `study_version`;
CREATE TABLE IF NOT EXISTS `study_version` (
  `version_id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_version` float DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `study_version` float DEFAULT NULL,
  `consent_version` float DEFAULT NULL,
  PRIMARY KEY (`version_id`)
) ENGINE=InnoDB AUTO_INCREMENT=979 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(512) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `accountNonExpired` tinyint(4) DEFAULT NULL,
  `accountNonLocked` tinyint(4) DEFAULT NULL,
  `created_date_time` varchar(255) DEFAULT NULL,
  `credentialsNonExpired` tinyint(4) DEFAULT NULL,
  `modified_date_time` varchar(255) DEFAULT NULL,
  `password_expiry_datetime` varchar(255) DEFAULT NULL,
  `security_token` varchar(255) DEFAULT NULL,
  `token_expiry_date` varchar(255) DEFAULT NULL,
  `token_used` tinyint(4) DEFAULT NULL,
  `force_logout` char(1) DEFAULT NULL,
  `user_login_datetime` varchar(255) DEFAULT NULL,
  `email_changed` tinyint(1) unsigned zerofill NOT NULL DEFAULT '0',
  `access_level` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `role_id_idx` (`role_id`),
  CONSTRAINT `role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.users_password_history
DROP TABLE IF EXISTS `users_password_history`;
CREATE TABLE IF NOT EXISTS `users_password_history` (
  `password_history_id` int(11) NOT NULL AUTO_INCREMENT,
  `created_date` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `password` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`password_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.users_temp
DROP TABLE IF EXISTS `users_temp`;
CREATE TABLE IF NOT EXISTS `users_temp` (
  `user_temp_id` int(11) NOT NULL AUTO_INCREMENT,
  `access_code` varchar(255) DEFAULT NULL,
  `accountNonExpired` bit(1) DEFAULT NULL,
  `accountNonLocked` bit(1) DEFAULT NULL,
  `asp_hi_id` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_date_time` varchar(255) DEFAULT NULL,
  `credentialsNonExpired` bit(1) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `fax_number` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_date_time` varchar(255) DEFAULT NULL,
  `password_expiry_datetime` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `security_token` varchar(255) DEFAULT NULL,
  `super_admin_id` int(11) DEFAULT NULL,
  `token_expiry_date` varchar(255) DEFAULT NULL,
  `token_used` bit(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_temp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.user_attempts
DROP TABLE IF EXISTS `user_attempts`;
CREATE TABLE IF NOT EXISTS `user_attempts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attempts` int(11) DEFAULT NULL,
  `last_modified` varchar(255) DEFAULT NULL,
  `email_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.user_permissions
DROP TABLE IF EXISTS `user_permissions`;
CREATE TABLE IF NOT EXISTS `user_permissions` (
  `permission_id` int(11) NOT NULL AUTO_INCREMENT,
  `permissions` varchar(45) NOT NULL,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `permission_id` (`permission_id`),
  UNIQUE KEY `permissions` (`permissions`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.user_permissions_users
DROP TABLE IF EXISTS `user_permissions_users`;
CREATE TABLE IF NOT EXISTS `user_permissions_users` (
  `user_permissions_permission_id` int(11) NOT NULL,
  `users_user_id` int(11) NOT NULL,
  PRIMARY KEY (`user_permissions_permission_id`,`users_user_id`),
  KEY `FK3CB60B1986B4070C` (`user_permissions_permission_id`),
  KEY `FK3CB60B19B9441C99` (`users_user_id`),
  KEY `FK3CB60B19B6DE1B0C` (`user_permissions_permission_id`),
  KEY `FK3CB60B1991B38899` (`users_user_id`),
  CONSTRAINT `FK3CB60B1986B4070C` FOREIGN KEY (`user_permissions_permission_id`) REFERENCES `user_permissions` (`permission_id`),
  CONSTRAINT `FK3CB60B1991B38899` FOREIGN KEY (`users_user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK3CB60B19B6DE1B0C` FOREIGN KEY (`user_permissions_permission_id`) REFERENCES `user_permissions` (`permission_id`),
  CONSTRAINT `FK3CB60B19B9441C99` FOREIGN KEY (`users_user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.user_permission_mapping
DROP TABLE IF EXISTS `user_permission_mapping`;
CREATE TABLE IF NOT EXISTS `user_permission_mapping` (
  `user_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`permission_id`),
  KEY `FKFEC4BF5294586FD0` (`user_id`),
  KEY `FKFEC4BF528CE62AFB` (`permission_id`),
  KEY `FKFEC4BF526CC7DBD0` (`user_id`),
  KEY `FKFEC4BF52BD103EFB` (`permission_id`),
  CONSTRAINT `FKFEC4BF526CC7DBD0` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKFEC4BF528CE62AFB` FOREIGN KEY (`permission_id`) REFERENCES `user_permissions` (`permission_id`),
  CONSTRAINT `FKFEC4BF5294586FD0` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKFEC4BF52BD103EFB` FOREIGN KEY (`permission_id`) REFERENCES `user_permissions` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.version_info
DROP TABLE IF EXISTS `version_info`;
CREATE TABLE IF NOT EXISTS `version_info` (
  `version_info_id` int(11) NOT NULL AUTO_INCREMENT,
  `android` varchar(255) DEFAULT NULL,
  `ios` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`version_info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
