USE `fda_hphc`;
DROP PROCEDURE IF EXISTS `deleteInActiveActivity`;
DELIMITER //
CREATE PROCEDURE `deleteInActiveActivity`(
	IN `studyId` INT(11)
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
	IN `questionnaireId` INT(11),
	IN `modifiedOn` VARCHAR(255),
	IN `modifiedBy` INT(11),
	IN `studyId` INT(50)
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
	IN `questionnaireId` INT(11)
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
	IN `questionnaireId` INT(11),
	IN `modifiedOn` VARCHAR(255),
	IN `modifiedBy` INT(11),
	IN `sequenceNo` INT(11),
	IN `stepId` INT(11),
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
