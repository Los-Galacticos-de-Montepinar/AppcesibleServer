-- sqlite3 appcesible.db < generateTables.sql
-- https://docs.google.com/document/d/16ObhjsTEj8iQGyab82sR5aKmuhVUmAu2/edit

DROP TABLE user;
DROP TABLE teacher;
DROP TABLE student;

-- he puesto como INT el tipo de usuario y la clase porque creo que va a ser más sencillo en la implementación
-- user:
--	0 profesor
--	1 estudiante
--	2 administrador
CREATE TABLE user (
	id INTEGER PRIMARY KEY,
	userName TEXT,
	passwd TEXT,
	idProfileImg INTEGER,
	userType INTEGER,
	idClass INTEGER
);

-- CREATE TABLE teacher (
-- 	id INTEGER PRIMARY KEY,
-- 	idUser INTEGER,
-- 	FOREIGN KEY (idUser) REFERENCES user(id)
-- );

CREATE TABLE student(
	id INTEGER PRIMARY KEY,
	idUser INTEGER,
	letterSize INTEGER,
	loginType INTEGER,
	interactionFormat INTEGER,
	FOREIGN KEY (idUser) REFERENCES user(id)
);


