ALTER TABLE player ADD COLUMN goal_nb INTEGER;

UPDATE player SET goal_nb = 0 WHERE name = 'Thibaut Courtois';
UPDATE player SET goal_nb = 2 WHERE name = 'Dani Carvajal';
UPDATE player SET goal_nb = 5 WHERE name = 'Jude Bellingham';

UPDATE player SET goal_nb = NULL WHERE name = 'Robert Lewandowski';
UPDATE player SET goal_nb = NULL WHERE name = 'Antoine Griezmann';