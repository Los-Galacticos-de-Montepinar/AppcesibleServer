-- sqlite3 appcesible.db < fillTables.sql
DELETE FROM user;
DELETE FROM student;
DELETE FROM loginInfo;
DELETE FROM item;

INSERT INTO user (id,userName,idProfileImg,userType,idClass) VALUES (NULL,'Johnny Joestar',2,1,0);
INSERT INTO user (id,userName,idProfileImg,userType,idClass) VALUES (NULL,'Gyro Zeppeli',3,0,0);
INSERT INTO loginInfo (id,idUser,loginMethod,textPass,passPart0,passPart1,passPart2) VALUES (NULL,1,1,NULL,1,1,1);
INSERT INTO loginInfo (id,idUser,loginMethod,textPass,passPart0,passPart1,passPart2) VALUES (NULL,2,0,'pass33',NULL,NULL,NULL);
INSERT INTO student (id,idUser,letterSize,interactionFormat) VALUES (NULL,1,20,1);

INSERT INTO item (id, itemName, imageName, count) VALUES (NULL,'Lápiz',2,10);
INSERT INTO item (id, itemName, imageName, count) VALUES (NULL,'Boli',2,20);
INSERT INTO item (id, itemName, imageName, count) VALUES (NULL,'Regla',2,30);
INSERT INTO item (id, itemName, imageName, count) VALUES (NULL,'Goma',2,30);
INSERT INTO item (id, itemName, imageName, count) VALUES (NULL,'Hoja',2,20);

INSERT INTO class(id, className) VALUES (NULL ,'Clase 1');
INSERT INTO class(id, className) VALUES (NULL ,'Clase 2');
INSERT INTO class(id, className) VALUES (NULL ,'Clase 3');