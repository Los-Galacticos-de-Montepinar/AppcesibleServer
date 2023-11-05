DELETE FROM user;
DELETE FROM student;

INSERT INTO user (id,userName, passwd,idProfileImg,userType,idClass) VALUES (NULL,'Johnny Joestar','pass22',2,1,0);
INSERT INTO user (id,userName, passwd,idProfileImg,userType,idClass) VALUES (NULL,'Gyro Zeppeli','pass33',3,0,0);
INSERT INTO student (id,idUser, letterSize,loginType,interactionFormat) VALUES (NULL,1,20,1,1);