/* Issue #3551 Standardize use of title case and lower case across screens, 
   and update text accordingly. */

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
UPDATE fda_hphc.health_kit_keys_info SET display_name = 'Dietary fat polyunsaturated' WHERE display_name = 'Dietary Fat Polyunsaturated';
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



