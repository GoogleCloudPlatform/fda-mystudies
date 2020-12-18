USE `fda_hphc`;
INSERT INTO version_info (version_info_id,android,ios) VALUES (1,'1.0.0','1.0.0');

/*!40000 ALTER TABLE `activetask_formula` DISABLE KEYS */;
INSERT INTO `activetask_formula` (`activetask_formula_id`, `value`, `formula`) VALUES
	(1, 'Summation of responses gathered over specified time range', 'Summation'),
	(2, 'Average of responses gathered over specified time range', 'Average'),
	(3, 'Maximum of a set of responses gathered over specified time range', 'Maximum'),
	(4, 'Minimum of a set of responses gathered over specified time range', 'Minimum');
/*!40000 ALTER TABLE `activetask_formula` ENABLE KEYS */;

/*!40000 ALTER TABLE `active_task_list` DISABLE KEYS */;
INSERT INTO `active_task_list` (`active_task_list_id`, `task_name`, `type`) VALUES
	(1, 'Fetal Kick Counter', 'fetalKickCounter'),
	(2, 'Tower Of Hanoi', 'towerOfHanoi'),
	(3, 'Spatial Span Memory', 'spatialSpanMemory');
/*!40000 ALTER TABLE `active_task_list` ENABLE KEYS */;

/*!40000 ALTER TABLE `active_task_master_attribute` DISABLE KEYS */;
INSERT INTO `active_task_master_attribute` (`active_task_master_attr_id`, `task_type_id`, `order_by`, `attribute_type`, `attribute_name`, `display_name`, `attribute_data_type`, `add_to_dashboard`, `task_type`, `study_version`) VALUES
	(1, 1, 1, 'configure_type', 'duration_fetal', 'Number of kicks recorded', 'time_picker', 'Y', NULL, NULL),
	(2, 1, 2, 'configure_type', 'duration_kick_count_fetal', 'Number of kicks to be recorded (N)', 'numeric', 'Y', NULL, NULL),
	(3, 1, 3, 'result_type', 'number_of_kicks_recorded_fetal', 'Time taken to record N kicks (in minutes)', 'numeric', 'Y', NULL, NULL),
	(4, 2, 1, 'configure_type', 'number_of_disks_tower', 'Number of Disks', 'numeric', 'Y', NULL, NULL),
	(5, 2, 2, 'result_type', 'puzzle_solved_unsolved_tower', 'Puzzle Solved/Unsolved', 'boolean', 'N', NULL, NULL),
	(6, 2, 3, 'result_type', 'number_of_moves_tower', 'Number of Moves', 'numeric', 'Y', NULL, NULL),
	(7, 3, 1, 'configure_type', 'Initial_Span_spatial', 'Initial Span', 'numeric', 'Y', NULL, NULL),
	(8, 3, 2, 'configure_type', 'Minimum_Span_spatial', 'Minimum Span', 'numeric', 'Y', NULL, NULL),
	(9, 3, 3, 'configure_type', 'Maximum_Span_spatial', 'Maximum Span', 'numeric', 'Y', NULL, NULL),
	(10, 3, 4, 'configure_type', 'Play_Speed_spatial', 'Play Speed', 'single select', 'Y', NULL, NULL),
	(11, 3, 5, 'configure_type', 'Maximum_Tests_spatial', 'Maximum Tests', 'numeric', 'Y', NULL, NULL),
	(12, 3, 6, 'configure_type', 'Maximum_Consecutive_Failuress_spatial', 'Maximum Consecutive Failures', 'numeric', 'Y', NULL, NULL),
	(13, 3, 7, 'configure_type', 'Require_reversal_spatial', 'Require Reversal?', 'boolean', 'Y', NULL, NULL),
	(14, 3, 8, 'result_type', 'Score_spatial', 'Score', 'numeric', 'Y', NULL, NULL),
	(15, 3, 9, 'result_type', 'Number_of_Games_spatial', 'Number of Games', 'numeric', 'Y', NULL, NULL),
	(16, 3, 10, 'result_type', 'Number_of_Failures_spatial', 'Number of Failures', 'numeric', 'Y', NULL, NULL);
/*!40000 ALTER TABLE `active_task_master_attribute` ENABLE KEYS */;

/*!40000 ALTER TABLE `consent_master_info` DISABLE KEYS */;
INSERT INTO `consent_master_info` (`id`, `title`, `type`, `code`, `study_version`) VALUES
	(2, 'Overview', 'ResearchKit', 'overview', NULL),
	(3, 'Data gathering', 'ResearchKit', 'dataGathering', NULL),
	(4, 'Privacy ', 'ResearchKit', 'privacy', NULL),
	(5, 'Data use', 'ResearchKit', 'dataUse', NULL),
	(6, 'Time commitment', 'ResearchKit', 'timeCommitment', NULL),
	(7, 'Surveys', 'ResearchKit', 'studySurvey', NULL),
	(8, 'Tasks ', 'ResearchKit', 'studyTasks', NULL),
	(9, 'Withdrawal', 'ResearchKit', 'withdrawing', NULL);
/*!40000 ALTER TABLE `consent_master_info` ENABLE KEYS */;

/*!40000 ALTER TABLE `health_kit_keys_info` DISABLE KEYS */;
INSERT INTO `health_kit_keys_info` (`id`, `category`, `display_name`, `key_text`, `result_type`) VALUES
	(1, 'Body\r\nMeasurements', 'Body Mass Index', 'HKQuantityTypeIdentifierBodyMassIndex', 'Count'),
	(2, 'Body\r\nMeasurements', 'Body Fat Percentage', 'HKQuantityTypeIdentifierBodyFatPercentage', 'Percentage'),
	(3, 'Body\r\nMeasurements', 'Height', 'HKQuantityTypeIdentifierHeight', 'Length'),
	(4, 'Body\r\nMeasurements', 'Body Mass', 'HKQuantityTypeIdentifierBodyMass', 'Mass'),
	(5, 'Body\r\nMeasurements', 'Lean Body Mass', 'HKQuantityTypeIdentifierLeanBodyMass', 'Mass'),
	(6, 'Fitness', 'Step Count', 'HKQuantityTypeIdentifierStepCount', 'Count'),
	(7, 'Fitness', 'Distance Walk/Run', 'HKQuantityTypeIdentifierDistanceWalkingRunning', 'Length'),
	(8, 'Fitness', 'Distance Cycling', 'HKQuantityTypeIdentifierDistanceCycling', 'Length'),
	(9, 'Fitness', 'Distance Wheelchair', 'HKQuantityTypeIdentifierDistanceWheelchair', 'Length'),
	(10, 'Fitness', 'Basal Energy Burned', 'HKQuantityTypeIdentifierBasalEnergyBurned', 'Energy'),
	(11, 'Fitness', 'Active Energy Burned', 'HKQuantityTypeIdentifierActiveEnergyBurned', 'Energy'),
	(12, 'Fitness', 'Flight Climbed', 'HKQuantityTypeIdentifierFlightsClimbed', 'Count'),
	(13, 'Fitness', 'Nike Fuel', 'HKQuantityTypeIdentifierNikeFuel', 'Count'),
	(14, 'Fitness', 'Exercise Time', 'HKQuantityTypeIdentifierAppleExerciseTime', 'Time'),
	(15, 'Fitness', 'Push Count', 'HKQuantityTypeIdentifierPushCount', 'Count'),
	(16, 'Fitness', 'Distance Swimming', 'HKQuantityTypeIdentifierDistanceSwimming', 'Length'),
	(17, 'Fitness', 'Swimming Stroke Count', 'HKQuantityTypeIdentifierSwimmingStrokeCount', 'Count'),
	(18, 'Vitals', 'Heart Rate', 'HKQuantityTypeIdentifierHeartRate', 'Count/Time'),
	(19, 'Vitals', 'Body Temperature', 'HKQuantityTypeIdentifierBodyTemperature', 'Temperature'),
	(20, 'Vitals', 'Basal Body Temperature', 'HKQuantityTypeIdentifierBasalBodyTemperature', 'Temperature'),
	(21, 'Vitals', 'Blood Pressure Systolic', 'HKQuantityTypeIdentifierBloodPressureSystolic', 'Pressure'),
	(23, 'Vitals', 'Blood Pressure Diastolic', 'HKQuantityTypeIdentifierBloodPressureDiastolic', 'Pressure'),
	(24, 'Vitals', 'Respiratory Rate', 'HKQuantityTypeIdentifierRespiratoryRate', 'Count/Time'),
	(25, 'Results', 'Oxygen Saturation', 'HKQuantityTypeIdentifierOxygenSaturation', 'Percentage'),
	(26, 'Results', 'Peripheral Perfusion Index', 'HKQuantityTypeIdentifierPeripheralPerfusionIndex', 'Percentage'),
	(27, 'Results', 'Blood Glucose', 'HKQuantityTypeIdentifierBloodGlucose', 'Mass/Volume'),
	(28, 'Results', 'Number of times fallen', 'HKQuantityTypeIdentifierNumberOfTimesFallen', 'Count'),
	(29, 'Results', 'Electrodermal Activity', 'HKQuantityTypeIdentifierElectrodermalActivity', 'Conductance'),
	(30, 'Results', 'Inhaler Usage', 'HKQuantityTypeIdentifierInhalerUsage', 'Count'),
	(31, 'Results', 'Blood Alcohol Count', 'HKQuantityTypeIdentifierBloodAlcoholContent', 'Percentage'),
	(32, 'Results', 'Forced Vital Capacity', 'HKQuantityTypeIdentifierForcedVitalCapacity', 'Volume'),
	(33, 'Results', 'Forced Expiratory Volume', 'HKQuantityTypeIdentifierForcedExpiratoryVolume1', 'Volume'),
	(34, 'Results', 'Peak Expiratory Flow Rate', 'HKQuantityTypeIdentifierPeakExpiratoryFlowRate', 'Volume/Time'),
	(35, 'Nutrition', 'Dietary Fat', 'HKQuantityTypeIdentifierDietaryFatTotal', 'Mass'),
	(36, 'Nutrition', 'Dietary Fat Polyunsaturated', 'HKQuantityTypeIdentifierDietaryFatPolyunsaturated', 'Mass'),
	(37, 'Nutrition', 'Dietary Fat Monounsaturated', 'HKQuantityTypeIdentifierDietaryFatMonounsaturated', 'Mass'),
	(38, 'Nutrition', 'Dietary Fat Saturated', 'HKQuantityTypeIdentifierDietaryFatSaturated', 'Mass'),
	(39, 'Nutrition', 'Dietary Cholestrol', 'HKQuantityTypeIdentifierDietaryCholesterol', 'Mass'),
	(40, 'Nutrition', 'Dietary Sodium', 'HKQuantityTypeIdentifierDietarySodium', 'Mass'),
	(41, 'Nutrition', 'Dietary Carbohydrate', 'HKQuantityTypeIdentifierDietaryCarbohydrates', 'Mass'),
	(42, 'Nutrition', 'Dietary Fiber', 'HKQuantityTypeIdentifierDietaryFiber', 'Mass'),
	(43, 'Nutrition', 'Dietary Sugar', 'HKQuantityTypeIdentifierDietarySugar', 'Mass'),
	(44, 'Nutrition', 'Dietary Energy Consumed', 'HKQuantityTypeIdentifierDietaryEnergyConsumed', 'Energy'),
	(45, 'Nutrition', 'Dietary Protein', 'HKQuantityTypeIdentifierDietaryProtein', 'Mass'),
	(46, 'Nutrition', 'Dietary Vitamin A', 'HKQuantityTypeIdentifierDietaryVitaminA', 'Mass'),
	(47, 'Nutrition', 'Dietary Vitamin B6', 'HKQuantityTypeIdentifierDietaryVitaminB6', 'Mass'),
	(48, 'Nutrition', 'Dietary Vitamin B12', 'HKQuantityTypeIdentifierDietaryVitaminB12', 'Mass'),
	(49, 'Nutrition', 'Dietary Vitamin C', 'HKQuantityTypeIdentifierDietaryVitaminC', 'Mass'),
	(50, 'Nutrition', 'Dietary Vitamin D', 'HKQuantityTypeIdentifierDietaryVitaminD', 'Mass'),
	(51, 'Nutrition', 'Dietary Vitamin E', 'HKQuantityTypeIdentifierDietaryVitaminE', 'Mass'),
	(52, 'Nutrition', 'Dietary Vitamin K', 'HKQuantityTypeIdentifierDietaryVitaminK', 'Mass'),
	(53, 'Nutrition', 'Dietary Calcium', 'HKQuantityTypeIdentifierDietaryCalcium', 'Mass'),
	(54, 'Nutrition', 'Dietary Iron', 'HKQuantityTypeIdentifierDietaryIron', 'Mass'),
	(55, 'Nutrition', 'Dietary Thiamin', 'HKQuantityTypeIdentifierDietaryThiamin', 'Mass'),
	(56, 'Nutrition', 'Dietary Riboflavin', 'HKQuantityTypeIdentifierDietaryRiboflavin', 'Mass'),
	(57, 'Nutrition', 'Dietary Niacin', 'HKQuantityTypeIdentifierDietaryNiacin', 'Mass'),
	(58, 'Nutrition', 'Dietary Folate', 'HKQuantityTypeIdentifierDietaryFolate', 'Mass'),
	(59, 'Nutrition', 'Dietary Biotin', 'HKQuantityTypeIdentifierDietaryBiotin', 'Mass'),
	(60, 'Nutrition', 'Dietary Pantothenic Acid', 'HKQuantityTypeIdentifierDietaryPantothenicAcid', 'Mass'),
	(61, 'Nutrition', 'Dietary Phosphorus', 'HKQuantityTypeIdentifierDietaryPhosphorus', 'Mass'),
	(62, 'Nutrition', 'Dietary Iodine', 'HKQuantityTypeIdentifierDietaryIodine', 'Mass'),
	(63, 'Nutrition', 'Dietary Magnesium', 'HKQuantityTypeIdentifierDietaryMagnesium', 'Mass'),
	(64, 'Nutrition', 'Dietary Zinc', 'HKQuantityTypeIdentifierDietaryZinc', 'Mass'),
	(65, 'Nutrition', 'Dietary Selenium', 'HKQuantityTypeIdentifierDietarySelenium', 'Mass'),
	(66, 'Nutrition', 'Dietary Copper', 'HKQuantityTypeIdentifierDietaryCopper', 'Mass'),
	(67, 'Nutrition', 'Dietary Manganese', 'HKQuantityTypeIdentifierDietaryManganese', 'Mass'),
	(68, 'Nutrition', 'Dietary Chromium', 'HKQuantityTypeIdentifierDietaryChromium', 'Mass'),
	(69, 'Nutrition', 'Dietary Molybdenum', 'HKQuantityTypeIdentifierDietaryMolybdenum', 'Mass'),
	(70, 'Nutrition', 'Dietary Chloride', 'HKQuantityTypeIdentifierDietaryChloride', 'Mass'),
	(71, 'Nutrition', 'Dietary Potassium', 'HKQuantityTypeIdentifierDietaryPotassium', 'Mass'),
	(72, 'Nutrition', 'Dietary Caffeine', 'HKQuantityTypeIdentifierDietaryCaffeine', 'Mass'),
	(73, 'Nutrition', 'Dietary Water', 'HKQuantityTypeIdentifierDietaryWater', 'Volume'),
	(74, 'Environment', 'UV Exposure', 'HKQuantityTypeIdentifierUVExposure', 'Count');
/*!40000 ALTER TABLE `health_kit_keys_info` ENABLE KEYS */;

/*!40000 ALTER TABLE `question_responsetype_master_info` DISABLE KEYS */;
INSERT INTO `question_responsetype_master_info` (`id`, `anchor_date`, `choice_based_branching`, `dashboard_allowed`, `data_type`, `description`, `formula_based_logic`, `healthkit_alternative`, `response_type`, `response_type_code`, `study_version`) VALUES
	(1, b'0', b'0', b'1', 'Double', 'Represents a response format that includes a slider control.', b'1', b'0', 'Scale', 'scale', NULL),
	(2, b'0', b'0', b'1', 'Double', 'Represents a response format that lets participants select a value on a continuous scale.', b'1', b'0', 'Continuous Scale', 'continuousScale', NULL),
	(3, b'0', b'1', b'0', 'String', 'Represents a response format that includes a discrete slider control with a text label next to each step.', b'0', b'0', 'Text Scale', 'textScale', NULL),
	(4, b'0', b'0', b'0', 'String', 'Represents a response format that lets participants use a value picker to choose from a fixed set of text choices.', b'0', b'0', 'Value Picker', 'valuePicker', NULL),
	(5, b'0', b'1', b'0', 'String', 'Represents a response format that lets participants choose one image from a fixed set of images in a single choice question.', b'0', b'0', 'Image Choice', 'imageChoice', NULL),
	(6, b'0', b'1', b'0', 'String', 'Represents a response format that lets participants choose from a fixed set of text choices in a multiple or single choice question.', b'0', b'0', 'Text Choice', 'textChoice', NULL),
	(7, b'0', b'1', b'0', 'Boolean', 'Represents a response format that lets participants choose from Yes and No options', b'0', b'0', 'Boolean', 'boolean', NULL),
	(8, b'0', b'0', b'1', 'Double', 'Represents a numeric response format that participants enter using a numeric keyboard.', b'1', b'1', 'Numeric', 'numeric', NULL),
	(9, b'0', b'0', b'0', 'String; \'HH:mm:ss\'', 'Represents the response format for questions that require users to enter a time of day.', b'0', b'0', 'Time of the day', 'timeOfDay', NULL),
	(10, b'1', b'0', b'0', 'String; \'yyyy-MM-dd\'T\'HH:mm:ss.SSSZ\'', 'Represents the response format for questions that require users to enter a date, or a date and time.', b'0', b'0', 'Date', 'date', NULL),
	(11, b'0', b'0', b'0', 'String', 'Represents the response format for questions that collect a text response from the user.', b'0', b'0', 'Text', 'text', NULL),
	(12, b'0', b'0', b'0', 'String', 'Represents the response format for questions that collect an email response from the user.', b'0', b'0', 'Email', 'email', NULL),
	(13, b'0', b'0', b'1', 'Double', 'Represents the response format for questions that ask users to specify a time interval. This is suitable for time intervals up to 24 hours', b'1', b'0', 'Time interval', 'timeInterval', NULL),
	(14, b'0', b'0', b'1', 'Double', 'Represents the response format for questions that ask users to specify a height value.', b'1', b'1', 'Height', 'height', NULL),
	(15, b'0', b'0', b'0', 'String; (lat,long)', 'Represents the response format for questions that collect a location response from the user. Displays a map on screen.', b'0', b'0', 'Location', 'location', NULL);
/*!40000 ALTER TABLE `question_responsetype_master_info` ENABLE KEYS */;

/*!40000 ALTER TABLE `reference_tables` DISABLE KEYS */;
INSERT INTO `reference_tables` (`id`, `str_value`, `category`, `type`) VALUES
	(1, 'Biologics Safety', 'Categories', 'Pre-defined'),
	(2, 'Clinical Trials', 'Categories', 'Pre-defined'),
	(3, 'Cosmetics Safety', 'Categories', 'Pre-defined'),
	(4, 'Drug Safety', 'Categories', 'Pre-defined'),
	(5, 'Food Safety', 'Categories', 'Pre-defined'),
	(6, 'Medical Device Safety', 'Categories', 'Pre-defined'),
	(7, 'Observational Studies', 'Categories', 'Pre-defined'),
	(8, 'Public Health', 'Categories', 'Pre-defined'),
	(9, 'Radiation-Emitting Products', 'Categories', 'Pre-defined'),
	(10, 'Tobacco Use', 'Categories', 'Pre-defined'),
	(11, 'University Research Institute', 'Data Partner', 'Pre-defined'),
	(12, 'FDA', 'Research Sponsors', 'Pre-defined');
/*!40000 ALTER TABLE `reference_tables` ENABLE KEYS */;

/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` (`role_id`, `role_name`) VALUES
	(1, 'Project Lead'),
	(2, 'Coordinator');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;

/*!40000 ALTER TABLE `statistic_master_images` DISABLE KEYS */;
INSERT INTO `statistic_master_images` (`statistic_image_id`, `value`) VALUES
	(1, 'Activity'),
	(2, 'Sleep'),
	(3, 'Weight'),
	(4, 'Nutrition'),
	(5, 'Heart Rate'),
	(6, 'Blood Glucose'),
	(7, 'Active Task'),
	(8, 'Baby Kicks'),
	(9, 'Other');
/*!40000 ALTER TABLE `statistic_master_images` ENABLE KEYS */;

/*!40000 ALTER TABLE `user_permissions` DISABLE KEYS */;
INSERT INTO `user_permissions` (`permission_id`, `permissions`) VALUES
	(8, 'ROLE_CREATE_MANAGE_STUDIES'),
	(6, 'ROLE_MANAGE_APP_WIDE_NOTIFICATION_EDIT'),
	(4, 'ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW'),
	(2, 'ROLE_MANAGE_STUDIES'),
	(5, 'ROLE_MANAGE_USERS_EDIT'),
	(7, 'ROLE_MANAGE_USERS_VIEW'),
	(1, 'ROLE_SUPERADMIN');
/*!40000 ALTER TABLE `user_permissions` ENABLE KEYS */;
