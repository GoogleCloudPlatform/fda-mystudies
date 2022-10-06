
-- fhir history record
CREATE TABLE `fhir_history` (
  `id` varchar(255) NOT NULL,
  `did_status` char(1) DEFAULT NULL,
  `patient_reference` varchar(255) NOT NULL,
  `questionnaire_reference` varchar(255) NOT NULL,
  `study_id` varchar(255) NOT NULL,
  `timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
