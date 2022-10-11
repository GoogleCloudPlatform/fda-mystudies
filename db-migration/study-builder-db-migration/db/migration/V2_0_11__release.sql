/* Issue #2929 Standardize use of title case and lower case across screens, 
   and update text accordingly. */
   
ALTER TABLE `fda_hphc`.`consent` ADD COLUMN `signature_Removal` BIT(1) NULL DEFAULT NULL;
