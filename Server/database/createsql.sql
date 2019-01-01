drop DATABASE base;
CREATE DATABASE Base;
USE Base;
create  TABLE  userInfo (userId int NOT NULL auto_increment primary key,userName VARCHAR(20) NOT NULL unique
					,userPassword VARCHAR(20) NOT NULL,Sex int,userType int Not NULL,lastLogintime timestamp
                    ,macAddress VARCHAR(20),userUsename VARCHAR(20) unique,personNumber varchar(20));
create  TABLE  userType (typeId int NOT NULL auto_increment primary key,typeName VARCHAR(20) unique);
create  TABLE  apkversion (Id int NOT NULL auto_increment primary key,versionName VARCHAR(20),
versionCode int(10),pathurl VARCHAR(20) ,package VARCHAR(20),packageName VARCHAR(20),lastLogintime timestamp unique);

create  TABLE  uploadRecord (Id int NOT NULL auto_increment primary key, userId int ,userFor int,uploadTime timestamp);
create  TABLE  filePath (Id int NOT NULL auto_increment primary key,uploadid int ,userid int,filePath VARCHAR(20),filename varchar(20));

INSERT  INTO  userInfo (userName,userPassword,userType) VALUES ('xzm','123',1); 
INSERT  INTO  userType (typeName) VALUES ('personnal'); 
INSERT  INTO  userType (typeName) VALUES ('company'); 
INSERT  INTO  apkversion (versionName,versionCode,pathurl,package,packageName) VALUES ('1.0.0',1,"com.android.example","/webapp/","Lottery"); 