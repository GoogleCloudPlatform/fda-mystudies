UPDATE fda_hphc.question_responsetype_master_info SET response_type = 'Continuous scale' WHERE response_type = 'Continuous Scale';
UPDATE fda_hphc.question_responsetype_master_info SET response_type = 'Text scale' WHERE response_type = 'Text Scale';
UPDATE fda_hphc.question_responsetype_master_info SET response_type = 'Value picker' WHERE response_type = 'Value Picker';
UPDATE fda_hphc.question_responsetype_master_info SET response_type = 'Image choice' WHERE response_type = 'Image Choice';
UPDATE fda_hphc.question_responsetype_master_info SET response_type = 'Text choice' WHERE response_type = 'Text Choice';