-- sqlite3 appcesible.db < generateTables.sql
-- https://docs.google.com/document/d/16ObhjsTEj8iQGyab82sR5aKmuhVUmAu2/edit

DROP TABLE loginInfo;
DROP TABLE user;
DROP TABLE student;
DROP TABLE task;
DROP TABLE taskStep;

-- method:
--	0 texto
--	1 imagenes
CREATE TABLE loginInfo(
	id INTEGER PRIMARY KEY,
	idUser INT,
	method INT NOT NULL,
	textPass TEXT,
	passPart0 TEXT,
	passPart1 TEXT,
	passPart2 TEXT,
	FOREIGN KEY (idUser) REFERENCES user(id)
);

-- user:
--	0 profesor
--	1 estudiante
--	2 administrador
CREATE TABLE user (
	id INTEGER PRIMARY KEY,
	userName TEXT NOT NULL,
	idProfileImg INTEGER,
	userType INTEGER NOT NULL,
	idClass INTEGER
);

CREATE TABLE student(
	id INTEGER PRIMARY KEY,
	idUser INTEGER,
	letterSize INTEGER,
	interactionFormat INTEGER,
	FOREIGN KEY (idUser) REFERENCES user(id)
);

CREATE TABLE task (
	id INTEGER PRIMARY KEY,
	title TEXT,
	taskDesc TEXT
);

CREATE TABLE taskStep(
	id INTEGER PRIMARY KEY,
	stepDesc TEXT,
	stepMedia TEXT,
	taskOrder INTEGER,
	idTask INTEGER,
	FOREIGN KEY (idTask) REFERENCES task(id)
);