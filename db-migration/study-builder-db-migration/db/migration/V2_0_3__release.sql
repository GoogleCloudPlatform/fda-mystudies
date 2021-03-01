/* Issue #2929 Standardize use of title case and lower case across screens, 
   and update text accordingly. */
UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Number of disks' 
WHERE display_name = 'Number of Disks';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Puzzle solved/unsolved' 
WHERE display_name = 'Puzzle Solved/Unsolved';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Number of moves' 
WHERE display_name = 'Number of Moves';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Initial span' 
WHERE display_name = 'Initial Span';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Minimum span' 
WHERE display_name = 'Minimum Span';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Maximum span' 
WHERE display_name = 'Maximum Span';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Play speed' 
WHERE display_name = 'Play Speed';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Maximum tests' 
WHERE display_name = 'Maximum Tests';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Maximum consecutive failures' 
WHERE display_name = 'Maximum Consecutive Failures';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Require reversal' 
WHERE display_name = 'Require Reversal';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Number of games' 
WHERE display_name = 'Number of Games';

UPDATE fda_hphc.active_task_master_attribute SET display_name = 'Number of failures' 
WHERE display_name = 'Number of Failures';

UPDATE fda_hphc.roles SET role_name = 'Superadmin' 
WHERE role_name = 'Project Lead';

UPDATE fda_hphc.roles SET role_name = 'Study admin' 
WHERE role_name = 'Coordinator';

DELETE FROM fda_hphc.user_permission_mapping WHERE permission_id IN ('5','7') AND user_id not IN
 (SELECT user_id FROM fda_hphc.users WHERE access_level ='SUPERADMIN');
 
UPDATE fda_hphc.users SET role_id='2', access_level='STUDY ADMIN'   WHERE access_level !='SUPERADMIN';
