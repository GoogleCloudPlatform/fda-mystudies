/* ISSUE #3115 Emails sent to participants of a specific study (for study invitations) must contain
 the support contact email address specific to the study, and as configured in the Study Builder */
alter table study_info add contact_email varchar(320) DEFAULT NULL;