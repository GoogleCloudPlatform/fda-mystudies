ALTER TABLE fda_hphc.studies ADD export_time DATETIME DEFAULT NULL;

/* ISSUE #3855 [Mobile] > Notification list should display only the notifications which are related to that particular platform */
ALTER TABLE fda_hphc.notification ADD platform VARCHAR(20) DEFAULT NULL;
