### hadoop安装
1.1 配置SSH免密码登录  
ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa  
该命令会在.ssh文件夹创建id_dsa及id_dsa.pub两个文件，这是ssh的一对公钥私钥  
cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys  

1.2伪分布式安装  
指定JDK安装位置  
vi {hadoop_home}/conf/Hadoop-env.sh  
export JAVA_HOME=/usr/lib/jdk-1.8  
conf/core-site.xml  
配置hadoop的地址及端口  
\<configuration\>  
  \<property\>  
    \<name\>fs.default.name\</name\>  
    \<value\>jdfs://localhost:9000\</value\>  
  \</property\>  
\</configuration\>  
