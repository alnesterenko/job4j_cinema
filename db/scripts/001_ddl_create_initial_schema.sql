CREATE TABLE files
(
    id serial PRIMARY KEY,
    name varchar NOT NULL,
    path varchar NOT NULL UNIQUE
);

CREATE TABLE genres
(
    id serial PRIMARY KEY,
    name varchar UNIQUE NOT NULL
);

CREATE TABLE films
(
    id serial PRIMARY KEY,
    name varchar NOT NULL,
    description varchar NOT NULL,
    year int NOT NULL,
    genre_id int REFERENCES genres (id) NOT NULL,
    minimal_age int NOT NULL,
    duration_in_minutes int NOT NULL,
    file_id int REFERENCES files (id) NOT NULL
);

CREATE TABLE halls
(
    id serial PRIMARY KEY,
    name varchar NOT NULL,
    row_count int NOT NULL,
    place_count int NOT NULL,
    description varchar NOT NULL
);

CREATE TABLE film_sessions
(
    id serial PRIMARY KEY,
    film_id int REFERENCES films (id) NOT NULL,
    halls_id int REFERENCES halls (id) NOT NULL,
    start_time timestamp NOT NULL,
    end_time timestamp NOT NULL,
    price int NOT NULL
);

CREATE TABLE users
(
    id serial PRIMARY KEY,
    full_name varchar NOT NULL,
    email varchar UNIQUE NOT NULL,
    password varchar NOT NULL
);

CREATE TABLE tickets
(
    id serial PRIMARY KEY,
    session_id int REFERENCES film_sessions (id) NOT NULL,
    row_number int NOT NULL,
    place_number int NOT NULL,
    user_id int NOT NULL,
    UNIQUE (session_id, row_number, place_number)
);
