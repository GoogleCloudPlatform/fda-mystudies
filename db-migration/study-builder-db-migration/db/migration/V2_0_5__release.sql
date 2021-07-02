UPDATE fda_hphc.anchordate_type SET name = 'Enrollment date' WHERE name = 'Enrollment Date';
ALTER TABLE fda_hphc.notification MODIFY COLUMN schedule_timestamp varchar(255) DEFAULT NULL;
ALTER TABLE fda_hphc.notification MODIFY COLUMN schedule_date varchar(255) DEFAULT NULL;
ALTER TABLE fda_hphc.notification MODIFY COLUMN schedule_time varchar(255) DEFAULT NULL;
