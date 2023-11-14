DELETE FROM user;
DELETE FROM student;
DELETE FROM loginInfo;

INSERT INTO user (id,userName,idProfileImg,userType,idClass) VALUES (NULL,'Johnny Joestar',2,1,0);
INSERT INTO user (id,userName,idProfileImg,userType,idClass) VALUES (NULL,'Gyro Zeppeli',3,0,0);
INSERT INTO loginInfo (id,idUser,method,textPass,passPart0,passPart1,passPart2) VALUES (NULL,1,1,NULL,'img','img','img');
INSERT INTO loginInfo (id,idUser,method,textPass,passPart0,passPart1,passPart2) VALUES (NULL,2,0,'pass33',NULL,NULL,NULL);
INSERT INTO student (id,idUser,letterSize,interactionFormat) VALUES (NULL,1,20,1);