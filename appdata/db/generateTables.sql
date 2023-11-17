-- sqlite3 appcesible.db < generateTables.sql
-- https://docs.google.com/document/d/16ObhjsTEj8iQGyab82sR5aKmuhVUmAu2/edit

DROP TABLE loginInfo;
DROP TABLE user;
DROP TABLE student;
DROP TABLE task;
DROP TABLE taskStep;
DROP TABLE taskAssignment;
DROP TABLE item;
DROP TABLE itemTaskEntry;
DROP TABLE gallery;

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

-- user:
--	0 fija
--	1 pedido
CREATE TABLE task (
	id INTEGER PRIMARY KEY,
	title TEXT,
	taskDesc TEXT,
	taskType INTEGER
);

CREATE TABLE taskStep(
	id INTEGER PRIMARY KEY,
	stepDesc TEXT,
	stepMedia TEXT,
	taskOrder INTEGER,
	idTask INTEGER,
	FOREIGN KEY (idTask) REFERENCES task(id)
);

CREATE TABLE item(
	id INTEGER PRIMARY KEY,
	itemName TEXT,
	imageName TEXT
);

CREATE TABLE itemTaskEntry(
	id INTEGER PRIMARY KEY,
	idTask INTEGER,
	quantity INTEGER,
	idItem INTEGER,
	FOREIGN KEY (idTask) REFERENCES task(id),
	FOREIGN KEY (idItem) REFERENCES item(id)
);

CREATE TABLE taskAssignment(
	id INTEGER PRIMARY KEY,
	idTask INTEGER,
	idUser INTEGER,
	finishDate TEXT,
	FOREIGN KEY (idTask) REFERENCES task(id),
	FOREIGN KEY (idUser) REFERENCES user(id)
);

CREATE TABLE gallery(
	id INTEGER PRIMARY KEY,
	imageType INTEGER,
	imageUrl TEXT,
	imageData BLOB,
	imageDesc TEXT
);