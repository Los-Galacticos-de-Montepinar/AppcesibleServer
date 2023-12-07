-- sqlite3 appcesible.db < fillTables.sql
DELETE FROM user;
DELETE FROM student;
DELETE FROM loginInfo;
DELETE FROM item;

INSERT INTO user (id,userName,idProfileImg,userType,idClass) VALUES (NULL,'Johnny Joestar',2,1,0);
INSERT INTO user (id,userName,idProfileImg,userType,idClass) VALUES (NULL,'Gyro Zeppeli',3,0,0);
INSERT INTO loginInfo (id,idUser,method,textPass,passPart0,passPart1,passPart2) VALUES (NULL,1,1,NULL,'img','img','img');
INSERT INTO loginInfo (id,idUser,method,textPass,passPart0,passPart1,passPart2) VALUES (NULL,2,0,'pass33',NULL,NULL,NULL);
INSERT INTO student (id,idUser,letterSize,interactionFormat) VALUES (NULL,1,20,1);

INSERT INTO item (id, itemName, imageName) VALUES (NULL,'Lápiz','img');
INSERT INTO item (id, itemName, imageName) VALUES (NULL,'Boli','img');
INSERT INTO item (id, itemName, imageName) VALUES (NULL,'Regla','img');
INSERT INTO item (id, itemName, imageName) VALUES (NULL,'Goma','img');
INSERT INTO item (id, itemName, imageName) VALUES (NULL,'Hoja','img');