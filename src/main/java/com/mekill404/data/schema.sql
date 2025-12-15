CREATE TYPE Position AS ENUM (
    'GK',
    'DEF',
    'MIDF'
    'STR',
)

CREATE TYPE Continent AS ENUM (
    'AFRICA',
    'EUROPA',
    'ASIA'
    'AMERICA',
)
CREATE TABLE Team(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    continent Continent NOT NULL DEFAULT 'AFRICA',
);

CREATE TABLE Player(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age INT NOT NULL CHECK (age BETWEEN 15 AND 45),
    position Position NOT NULL DEFAULT 'GK',
    team_id INT NOT NULL,
    CONSTRAINT fk_player_team FOREIGN KEY (id_team) REFERENCES Team(id) ON DELETE RESTRICT
);

