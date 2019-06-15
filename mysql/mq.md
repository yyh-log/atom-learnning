### Create Index
mysql索引有两种类型，b树索引和哈希索引，默认值为b树索引  
* 创建表时创建索引：  
create table table_name(
field type,  
field type,  
   ... ,  
[UNIQUE|FULLTEXT|SPATIAL] INDEX [indexname] (field [length][ASC|DESC])
)  
创建一个名为adress的唯一索引  
create table score(  
id int(11) auto_increment primary key not null,  
name varchar(50) null,  
UNIQUE INDEX adress(id ASC)  
);  

创建空间索引需要设置SPATIAL参数，必只有MyISAM类型表示支持该类型的索引。  
* 在已经建立的表中创建索引  
create [UNIQUE|FULLTEXT|SPATIAL] INDEX [indexname] on table_name (field [length] [ASC|DESC])
* 修改数据表结构添加索引  
alter table table_name add [UNIQUE|FULLTEXT|SPATIAL] INDEX [indexname] (field [length][ASC|DESC])  
* 删除索引  
drop index index_name on table_name;
### Create View
查看创建视图的权限  
select Selete_priv,Create_view_priv From mysql.user where user='用户名';  
创建视图  
create [ALGORITHM={UNDEFINED|MERGE|TEMPTABLE}] view view_name(field1,field2,field3) as select 语句 [WIHT[CASCADED|LOCAL]CHECK OPTION]  
create view book_view(a_sort,a,talk,a_books) as select sort,talk,books from tb_book;  
视图操作  
describe view_name;  
show table status like view_name;  
show create view view_name;  
修改视图  
alter view view_name (column1,column2,..) as select column1,column2,.. from table;
更新视图  
update view_name set column='';  
视图很多情况下都无法更新，例如创建视图采用COUNT()，SUM()等聚合函数，含有GROUP BY，HAVING等关键字，select含有子查询等等都无法更新视图。  
删除视图  
drop view if exists view_name;
### Create user
create user user_name identified by 'passward';  
drop user user_name;  
rename user old_user to new new_user;  
授权  
权限级别可以分为多个层级 
全局层级   
数据库层级  
表层级  
列层级  
子程序层级  
grant all privileges on * to 'user_name' indentified by 'password';  
revoke all privileges on * from 'user_name';  
创建没有任何权限的用户  
grant usage on * to to 'user_name' indentified by 'password';   
查看权限  
show grants for 'user_name';  
设置用户名密码  
set password for user_name = password('password');  
set password for 'test'@'%' = password('test1234');  
grant usage在* . *指定某个账户密码，而不影响当前用户的权限  
grant usage on * . * to 'test'@'%' identified by 'test1234';  
创建的用户都时存储在mysql.user表中，可以直接操作此表创建用户和修改用户  
insert into user(Host,User,Password) values('%','user_name','password');  
update set password = passward('test1234') where Host='%' and User='user_name';  
