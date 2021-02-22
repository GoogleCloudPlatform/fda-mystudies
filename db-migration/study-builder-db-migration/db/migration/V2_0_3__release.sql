/* Issue #2929 Standardize use of title case and lower case across screens, 
   and update text accordingly. */
UPDATE active_task_master_attribute SET display_name = 'Number of disks' 
WHERE display_name = 'Number of Disks';

UPDATE active_task_master_attribute SET display_name = 'Puzzle solved/unsolved' 
WHERE display_name = 'Puzzle Solved/Unsolved';

UPDATE active_task_master_attribute SET display_name = 'Number of moves' 
WHERE display_name = 'Number of Moves';

UPDATE active_task_master_attribute SET display_name = 'Initial span' 
WHERE display_name = 'Initial Span';

UPDATE active_task_master_attribute SET display_name = 'Minimum span' 
WHERE display_name = 'Minimum Span';

UPDATE active_task_master_attribute SET display_name = 'Maximum span' 
WHERE display_name = 'Maximum Span';

UPDATE active_task_master_attribute SET display_name = 'Play speed' 
WHERE display_name = 'Play Speed';

UPDATE active_task_master_attribute SET display_name = 'Maximum tests' 
WHERE display_name = 'Maximum Tests';

UPDATE active_task_master_attribute SET display_name = 'Maximum consecutive failures' 
WHERE display_name = 'Maximum Consecutive Failures';

UPDATE active_task_master_attribute SET display_name = 'Require reversal' 
WHERE display_name = 'Require Reversal';

UPDATE active_task_master_attribute SET display_name = 'Number of games' 
WHERE display_name = 'Number of Games';

UPDATE active_task_master_attribute SET display_name = 'Number of failures' 
WHERE display_name = 'Number of Failures';
