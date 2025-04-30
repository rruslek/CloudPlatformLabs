-- Создание таблицы без партиционирования

DROP TABLE IF EXISTS movies_plain CASCADE;

CREATE TABLE movies_plain (
    title VARCHAR(100),
    year INT,
    certificate VARCHAR(10), 
	duration_min INT, 
	genre VARCHAR(50), 
	rating DECIMAL, 
	description TEXT, 
	metascore INT, 
	director VARCHAR(70),
	star1 VARCHAR(70), 
	star2 VARCHAR(70), 
	star3 VARCHAR(70), 
	star4 VARCHAR(70), 
	votes INT, 
	gross INT,
    PRIMARY KEY (title)
);

-- Создание таблицы с партиционированием по годам

DROP TABLE IF EXISTS movies CASCADE;

CREATE TABLE movies (
    title VARCHAR(100),
    year INT,
    certificate VARCHAR(10), 
	duration_min INT, 
	genre VARCHAR(50), 
	rating DECIMAL, 
	description TEXT, 
	metascore INT, 
	director VARCHAR(70),
	star1 VARCHAR(70), 
	star2 VARCHAR(70), 
	star3 VARCHAR(70), 
	star4 VARCHAR(70), 
	votes INT, 
	gross INT,
    PRIMARY KEY (title, year)
) PARTITION BY RANGE (year);

-- Добавление партициий

CREATE TABLE movies_1930s PARTITION OF movies
	FOR VALUES FROM (1930) TO (1940);
	
CREATE TABLE movies_1940s PARTITION OF movies
	FOR VALUES FROM (1940) TO (1950);
	
CREATE TABLE movies_1950s PARTITION OF movies
	FOR VALUES FROM (1950) TO (1960);
	
CREATE TABLE movies_1960s PARTITION OF movies
	FOR VALUES FROM (1960) TO (1970);
	
CREATE TABLE movies_1970s PARTITION OF movies
	FOR VALUES FROM (1970) TO (1980);
	
CREATE TABLE movies_1980s PARTITION OF movies
	FOR VALUES FROM (1980) TO (1990);
	
CREATE TABLE movies_1990s PARTITION OF movies
	FOR VALUES FROM (1990) TO (2000);
	
CREATE TABLE movies_2000s PARTITION OF movies
	FOR VALUES FROM (2000) TO (2010);
	
CREATE TABLE movies_2010s PARTITION OF movies
	FOR VALUES FROM (2010) TO (2020);
	
-- Удаление партициий

DROP TABLE movies_1930s CASCADE;
DROP TABLE movies_1940s CASCADE;
DROP TABLE movies_1950s CASCADE;
DROP TABLE movies_1960s CASCADE;
DROP TABLE movies_1970s CASCADE;
DROP TABLE movies_1980s CASCADE;
DROP TABLE movies_1990s CASCADE;
DROP TABLE movies_2000s CASCADE;
DROP TABLE movies_2010s CASCADE;
DROP TABLE movies_2020s CASCADE;

-- Создание локального индекса

CREATE INDEX idx_movies_rating_1980s ON movies_1980s (rating);

-- Использование локального индекса

SELECT title FROM movies
WHERE rating > 8 AND year = 1985;

-- Создание глобального индекса

CREATE INDEX idx_movies_rating ON movies (rating);

-- Использование глобального индекса

SELECT title FROM movies
WHERE rating > 8;

-- Выборка данных

SELECT * FROM movies WHERE year > 1992;

-- Выборка данных с анализом затраченного времени

EXPLAIN ANALYZE
SELECT * FROM movies WHERE year > 1992;