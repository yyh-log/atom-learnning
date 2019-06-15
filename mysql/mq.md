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
删除视图  
