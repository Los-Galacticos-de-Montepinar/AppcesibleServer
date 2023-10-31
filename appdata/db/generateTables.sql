-- sqlite3 appcesible.db < generateTables.sql
-- https://docs.google.com/document/d/16ObhjsTEj8iQGyab82sR5aKmuhVUmAu2/edit

DROP TABLE user;
DROP TABLE teacher;

-- he puesto como INT el tipo de usuario y la clase porque creo que va a ser más sencillo en la implementación
CREATE TABLE user (
	id INTEGER PRIMARY KEY,
	userName TEXT,
	passwd TEXT,
	idProfileImg INTEGER,
	userType INTEGER,
	idClass INTEGER,
	age INTEGER
-- La edad no aparece en las tablas, la quitamos? de hecho, si fuera necesario deberiamos usar fecha de nacimiento
);

CREATE TABLE teacher (
	id INTEGER PRIMARY KEY,
	idUser INTEGER,
	FOREIGN KEY (idUser) REFERENCES user(id)
);
