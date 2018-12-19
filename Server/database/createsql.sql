CREATE DATABASE Base;
use  Base;
create  TABLE  userInfo (userId int NOT NULL auto_increment primary key AUTO_INCREMENT,userName VARCHAR(20) NOT NULL unique
					,userPassword VARCHAR(20) NOT NULL,Sex int,userType int Not NULL,lastLogintime timestamp
                    ,macAddress VARCHAR(20),userUsename VARCHAR(20),personNumber varchar(20),personName VARCHAR(20));
create  TABLE  userType (typeId int NOT NULL auto_increment primary key,typeName VARCHAR(20));
INSERT  INTO  userInfo (userName,userPassword,userType) VALUES ('huzhiheng','123456',1); 
INSERT  INTO  userType (typeName) VALUES ('personnal'); 
INSERT  INTO  userType (typeName) VALUES ('company'); 