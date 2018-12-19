use base
select * from userInfo
select * from usertype

drop database base

INSERT  INTO  user (userName,userPassword,userType) VALUES ('xzm','123'，0); 
INSERT  INTO  userInfo (userName,userPassword,userType) VALUES ('xzm','123',1); 
update userinfo set userUsename = '徐泽民' where userId = 1