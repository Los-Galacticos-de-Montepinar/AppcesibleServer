-- sqlite appcesible.db < generateTables.sql

-- he puesto como INT el tipo de usuario y la clase porque creo que va a ser más sencillo en la implementación
CREATE TABLE user (
	id INTEGER PRIMARY KEY,
	userName TEXT,
	passwd TEXT,
	idProfileImg INTEGER,
	userType INTEGER,
	idClass INTEGER,
	age INTEGER
);
