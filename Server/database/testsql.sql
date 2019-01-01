use base;
select * from userInfo;
select * from apkversion;
select * from filePath;
select * from uploadRecord;
select * from userInfo where userName = 'xzm' and userPassword = '123' and userId = '1';


select * from uploadRecord where userFor = '0'  and uploadTime = '2018-12-21 01:02:31.074'  and userId = '1' 
select * from uploadRecord where userFor = '0'  and uploadTime = '2018-12-21 01:08:11.321'  and userId = '1' 
drop database base;

INSERT  INTO  userInfo (userName,userPassword,userType) VALUES ('xzm','123',1); 

update userinfo set userUsename = '徐泽民' where userId = 1;