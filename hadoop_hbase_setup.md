####Hadoop, HDFS and HBase installation
- extra
- getting root
sudo -i



sudo apt-get update

sudo apt-get install default-jdk

-testing
java -version

java version "1.7.0_91"

sudo addgroup hadoop

sudo adduser --ingroup hadoop hduser

default
sudo apt-get install ssh
--testing
which ssh
--creating and seting up ssh certificates
su hduser
hduser@ubuntu:/home/maverick/Downloads$ ssh-keygen -t rsa -P ""
Generating public/private rsa key pair.
Enter file in which to save the key (/home/hduser/.ssh/id_rsa): 
Created directory '/home/hduser/.ssh'.
Your identification has been saved in /home/hduser/.ssh/id_rsa.
Your public key has been saved in /home/hduser/.ssh/id_rsa.pub.
The key fingerprint is:
71:2e:42:cc:e8:22:7a:cf:b9:67:02:9c:b6:f5:9a:ad hduser@ubuntu
The key's randomart image is:
+--[ RSA 2048]----+
|                 |
|     +           |
|    . + . .      |
|   . .   +       |
|..... . S .      |
|..=..  . .       |
|...+ .           |
| ..o.++          |
|    EB.          |
+-----------------+

Hadoop uses SSH (to access its nodes) which would normally require the user to enter a password. However, this requirement can be eliminated by creating and setting up SSH certificates using the following commands. If asked for a filename just leave it blank and press the enter key to continue. 

cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys

--testing ssh without password
hduser@ubuntu:/home/maverick/Downloads$ ssh localhost
The authenticity of host 'localhost (127.0.0.1)' can't be established.
ECDSA key fingerprint is 38:c6:5d:f2:75:5f:c3:59:77:38:2b:31:6f:0e:00:82.
Are you sure you want to continue connecting (yes/no)? yes
Warning: Permanently added 'localhost' (ECDSA) to the list of known hosts.
Welcome to Ubuntu 14.04.3 LTS (GNU/Linux 3.19.0-47-generic x86_64)

 * Documentation:  https://help.ubuntu.com/

The programs included with the Ubuntu system are free software;
the exact distribution terms for each program are described in the
individual files in /usr/share/doc/*/copyright.

--installing hadoop

Ubuntu comes with ABSOLUTELY NO WARRANTY, to the extent permitted by
applicable law.

hduser@ubuntu:~$ wget http://www.apache.org/dyn/closer.cgi/hadoop/common/hadoop-2.6.3/hadoop-2.6.3.tar.gz
--2016-01-20 00:06:35--  http://www.apache.org/dyn/closer.cgi/hadoop/common/hadoop-2.6.3/hadoop-2.6.3.tar.gz
Resolving www.apache.org (www.apache.org)... 88.198.26.2, 140.211.11.105, 2a01:4f8:130:2192::2
Connecting to www.apache.org (www.apache.org)|88.198.26.2|:80... connected.
HTTP request sent, awaiting response... 200 OK
Length: unspecified [text/html]
Saving to: ‘hadoop-2.6.3.tar.gz’

    [ <=>                                                             ] 23,922      --.-K/s   in 0.1s    

2016-01-20 00:06:36 (162 KB/s) - ‘hadoop-2.6.3.tar.gz’ saved [23922]


maverick@ubuntu:/home/hduser$ sudo adduser hduser sudo
[sudo] password for maverick: 
Adding user `hduser' to group `sudo' ...
Adding user hduser to group sudo
Done.



tar -xvf hadoop-2.6.3.tar.gz

hduser@ubuntu:~/hadoop-2.6.3$ sudo mkdir /usr/local/hadoop
hduser@ubuntu:~/hadoop-2.6.3$ sudo mv * /usr/local/hadoop
hduser@ubuntu:~/hadoop-2.6.3$ sudo chown -R hduser:hadoop /usr/local/hadoop


Setup Configuration Files
The following files will have to be modified to complete the Hadoop setup:

~/.bashrc
/usr/local/hadoop/etc/hadoop/hadoop-env.sh
/usr/local/hadoop/etc/hadoop/core-site.xml
/usr/local/hadoop/etc/hadoop/mapred-site.xml.template
/usr/local/hadoop/etc/hadoop/hdfs-site.xml

--Obtaining Java Path

hduser@ubuntu:~/hadoop-2.6.3$ update-alternatives --config java
There is only one alternative in link group java (providing /usr/bin/java): /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java

--append the following to ~/.bashrc
#HADOOP VARIABLES START
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
export HADOOP_INSTALL=/usr/local/hadoop
export PATH=$PATH:$HADOOP_INSTALL/bin
export PATH=$PATH:$HADOOP_INSTALL/sbin
export HADOOP_MAPRED_HOME=$HADOOP_INSTALL
export HADOOP_COMMON_HOME=$HADOOP_INSTALL
export HADOOP_HDFS_HOME=$HADOOP_INSTALL
export YARN_HOME=$HADOOP_INSTALL
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_INSTALL/lib/native
export HADOOP_OPTS="-Djava.library.path=$HADOOP_INSTALL/lib"
#HADOOP VARIABLES END

hduser@ubuntu:~$ source ~/.bashrc

--testing
hduser@ubuntu:~$ javac -version
javac 1.7.0_91


--setting JAVA_HOME oh hadoop environment
hduser@ubuntu:~$ vi /usr/local/hadoop/etc/hadoop/hadoop-env.sh

# The java implementation to use.
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java


sudo mkdir -p /app/hadoop/tmp
sudo chown hduser:hadoop /app/hadoop/tmp


hduser@ubuntu:~$ vi /usr/local/hadoop/etc/hadoop/core-site.xml

<configuration>

 <property>
  <name>hadoop.tmp.dir</name>
  <value>/app/hadoop/tmp</value>
  <description>A base for other temporary directories.</description>
 </property>

 <property>
  <name>fs.default.name</name>
  <value>hdfs://localhost:54310</value>
  <description>The name of the default file system.  A URI whose
  scheme and authority determine the FileSystem implementation.  The
  uri's scheme determines the config property (fs.SCHEME.impl) naming
  the FileSystem implementation class.  The uri's authority is used to
  determine the host, port, etc. for a filesystem.</description>
 </property>


cp /usr/local/hadoop/etc/hadoop/mapred-site.xml.template /usr/local/hadoop/etc/hadoop/mapred-site.xml



hduser@ubuntu:~$ vi /usr/local/hadoop/etc/hadoop/mapred-site.xml
<property>
  <name>mapred.job.tracker</name>
  <value>localhost:54311</value>
  <description>The host and port that the MapReduce job tracker runs
  at.  If "local", then jobs are run in-process as a single map
  and reduce task.
  </description>
 </property>

</configuration>


--creating namenode and datanode HDFS
sudo mkdir -p /usr/local/hadoop_store/hdfs/namenode
sudo mkdir -p /usr/local/hadoop_store/hdfs/datanode
sudo chown -R hduser:hadoop /usr/local/hadoop_store

vi /usr/local/hadoop/etc/hadoop/hdfs-site.xml


<property>
  <name>dfs.replication</name>
  <value>1</value>
  <description>Default block replication.
  The actual number of replications can be specified when the file is created.
  The default is used if replication is not specified in create time.
  </description>
 </property>
 <property>
   <name>dfs.namenode.name.dir</name>
   <value>file:/usr/local/hadoop_store/hdfs/namenode</value>
 </property>
 <property>
   <name>dfs.datanode.data.dir</name>
   <value>file:/usr/local/hadoop_store/hdfs/datanode</value>
 </property>


Format the New Hadoop Filesystem


hduser@ubuntu:~$ hadoop namenode -format
DEPRECATED: Use of this script to execute hdfs command is deprecated.
Instead use the hdfs command for it.

16/01/20 00:55:08 INFO namenode.NameNode: STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = ubuntu/127.0.1.1
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 2.6.3
STARTUP_MSG:   classpath = /usr/local/hadoop/etc/hadoop:/usr/local/hadoop/share/hadoop/common/lib/xmlenc-0.52.jar:/usr/local/hadoop/share/hadoop/common/lib/asm-3.2.jar:/usr/local/hadoop/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/usr/local/hadoop/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/usr/local/hadoop/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/usr/local/hadoop/share/hadoop/common/lib/slf4j-log4j12-1.7.5.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-compress-1.4.1.jar:/usr/local/hadoop/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/usr/local/hadoop/share/hadoop/common/lib/jsch-0.1.42.jar:/usr/local/hadoop/share/hadoop/common/lib/httpcore-4.2.5.jar:/usr/local/hadoop/share/hadoop/common/lib/jersey-json-1.9.jar:/usr/local/hadoop/share/hadoop/common/lib/jasper-compiler-5.5.23.jar:/usr/local/hadoop/share/hadoop/common/lib/xz-1.0.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-lang-2.6.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-io-2.4.jar:/usr/local/hadoop/share/hadoop/common/lib/jsr305-1.3.9.jar:/usr/local/hadoop/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop/share/hadoop/common/lib/hamcrest-core-1.3.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-net-3.1.jar:/usr/local/hadoop/share/hadoop/common/lib/paranamer-2.3.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-cli-1.2.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-el-1.0.jar:/usr/local/hadoop/share/hadoop/common/lib/slf4j-api-1.7.5.jar:/usr/local/hadoop/share/hadoop/common/lib/curator-client-2.6.0.jar:/usr/local/hadoop/share/hadoop/common/lib/activation-1.1.jar:/usr/local/hadoop/share/hadoop/common/lib/httpclient-4.2.5.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-logging-1.1.3.jar:/usr/local/hadoop/share/hadoop/common/lib/junit-4.11.jar:/usr/local/hadoop/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/usr/local/hadoop/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/usr/local/hadoop/share/hadoop/common/lib/servlet-api-2.5.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-collections-3.2.2.jar:/usr/local/hadoop/share/hadoop/common/lib/jetty-util-6.1.26.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-httpclient-3.1.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/usr/local/hadoop/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-configuration-1.6.jar:/usr/local/hadoop/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/usr/local/hadoop/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-digester-1.8.jar:/usr/local/hadoop/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-codec-1.4.jar:/usr/local/hadoop/share/hadoop/common/lib/jersey-server-1.9.jar:/usr/local/hadoop/share/hadoop/common/lib/jsp-api-2.1.jar:/usr/local/hadoop/share/hadoop/common/lib/stax-api-1.0-2.jar:/usr/local/hadoop/share/hadoop/common/lib/netty-3.6.2.Final.jar:/usr/local/hadoop/share/hadoop/common/lib/curator-framework-2.6.0.jar:/usr/local/hadoop/share/hadoop/common/lib/jettison-1.1.jar:/usr/local/hadoop/share/hadoop/common/lib/log4j-1.2.17.jar:/usr/local/hadoop/share/hadoop/common/lib/avro-1.7.4.jar:/usr/local/hadoop/share/hadoop/common/lib/mockito-all-1.8.5.jar:/usr/local/hadoop/share/hadoop/common/lib/jasper-runtime-5.5.23.jar:/usr/local/hadoop/share/hadoop/common/lib/jetty-6.1.26.jar:/usr/local/hadoop/share/hadoop/common/lib/curator-recipes-2.6.0.jar:/usr/local/hadoop/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/usr/local/hadoop/share/hadoop/common/lib/hadoop-auth-2.6.3.jar:/usr/local/hadoop/share/hadoop/common/lib/gson-2.2.4.jar:/usr/local/hadoop/share/hadoop/common/lib/jersey-core-1.9.jar:/usr/local/hadoop/share/hadoop/common/lib/hadoop-annotations-2.6.3.jar:/usr/local/hadoop/share/hadoop/common/lib/commons-math3-3.1.1.jar:/usr/local/hadoop/share/hadoop/common/lib/zookeeper-3.4.6.jar:/usr/local/hadoop/share/hadoop/common/lib/htrace-core-3.0.4.jar:/usr/local/hadoop/share/hadoop/common/lib/jets3t-0.9.0.jar:/usr/local/hadoop/share/hadoop/common/lib/guava-11.0.2.jar:/usr/local/hadoop/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/usr/local/hadoop/share/hadoop/common/hadoop-common-2.6.3-tests.jar:/usr/local/hadoop/share/hadoop/common/hadoop-common-2.6.3.jar:/usr/local/hadoop/share/hadoop/common/hadoop-nfs-2.6.3.jar:/usr/local/hadoop/share/hadoop/hdfs:/usr/local/hadoop/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/asm-3.2.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/commons-io-2.4.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/jsr305-1.3.9.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/commons-el-1.0.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/jsp-api-2.1.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/jasper-runtime-5.5.23.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/htrace-core-3.0.4.jar:/usr/local/hadoop/share/hadoop/hdfs/lib/guava-11.0.2.jar:/usr/local/hadoop/share/hadoop/hdfs/hadoop-hdfs-2.6.3.jar:/usr/local/hadoop/share/hadoop/hdfs/hadoop-hdfs-2.6.3-tests.jar:/usr/local/hadoop/share/hadoop/hdfs/hadoop-hdfs-nfs-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/lib/asm-3.2.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/usr/local/hadoop/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/usr/local/hadoop/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jersey-json-1.9.jar:/usr/local/hadoop/share/hadoop/yarn/lib/xz-1.0.jar:/usr/local/hadoop/share/hadoop/yarn/lib/commons-lang-2.6.jar:/usr/local/hadoop/share/hadoop/yarn/lib/commons-io-2.4.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jsr305-1.3.9.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop/share/hadoop/yarn/lib/commons-cli-1.2.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jline-0.9.94.jar:/usr/local/hadoop/share/hadoop/yarn/lib/activation-1.1.jar:/usr/local/hadoop/share/hadoop/yarn/lib/aopalliance-1.0.jar:/usr/local/hadoop/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/usr/local/hadoop/share/hadoop/yarn/lib/servlet-api-2.5.jar:/usr/local/hadoop/share/hadoop/yarn/lib/commons-collections-3.2.2.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/usr/local/hadoop/share/hadoop/yarn/lib/guice-3.0.jar:/usr/local/hadoop/share/hadoop/yarn/lib/commons-httpclient-3.1.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop/share/hadoop/yarn/lib/commons-codec-1.4.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jersey-server-1.9.jar:/usr/local/hadoop/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/usr/local/hadoop/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jettison-1.1.jar:/usr/local/hadoop/share/hadoop/yarn/lib/log4j-1.2.17.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jetty-6.1.26.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/usr/local/hadoop/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jersey-client-1.9.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jersey-core-1.9.jar:/usr/local/hadoop/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/usr/local/hadoop/share/hadoop/yarn/lib/guava-11.0.2.jar:/usr/local/hadoop/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/usr/local/hadoop/share/hadoop/yarn/lib/javax.inject-1.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-server-tests-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-api-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-common-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-client-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-server-common-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-registry-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.6.3.jar:/usr/local/hadoop/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.6.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/asm-3.2.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/xz-1.0.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/junit-4.11.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/guice-3.0.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/hadoop-annotations-2.6.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/lib/javax.inject-1.jar:/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.6.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.6.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.6.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.6.3-tests.jar:/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.6.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.6.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.6.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.6.3.jar:/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.6.3.jar:/contrib/capacity-scheduler/*.jar:/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r cc865b490b9a6260e9611a5b8633cab885b3d247; compiled by 'jenkins' on 2015-12-18T01:19Z
STARTUP_MSG:   java = 1.7.0_91
************************************************************/
16/01/20 00:55:08 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
16/01/20 00:55:08 INFO namenode.NameNode: createNameNode [-format]
16/01/20 00:55:09 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Formatting using clusterid: CID-881d7de9-d0bf-4097-b871-8d6d9d32c4a3
16/01/20 00:55:09 INFO namenode.FSNamesystem: No KeyProvider found.
16/01/20 00:55:09 INFO namenode.FSNamesystem: fsLock is fair:true
16/01/20 00:55:09 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
16/01/20 00:55:09 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
16/01/20 00:55:09 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
16/01/20 00:55:09 INFO blockmanagement.BlockManager: The block deletion will start around 2016 Jan 20 00:55:09
16/01/20 00:55:09 INFO util.GSet: Computing capacity for map BlocksMap
16/01/20 00:55:09 INFO util.GSet: VM type       = 64-bit
16/01/20 00:55:09 INFO util.GSet: 2.0% max memory 966.7 MB = 19.3 MB
16/01/20 00:55:09 INFO util.GSet: capacity      = 2^21 = 2097152 entries
16/01/20 00:55:09 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
16/01/20 00:55:09 INFO blockmanagement.BlockManager: defaultReplication         = 1
16/01/20 00:55:09 INFO blockmanagement.BlockManager: maxReplication             = 512
16/01/20 00:55:09 INFO blockmanagement.BlockManager: minReplication             = 1
16/01/20 00:55:09 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
16/01/20 00:55:09 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
16/01/20 00:55:09 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
16/01/20 00:55:09 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
16/01/20 00:55:10 INFO namenode.FSNamesystem: fsOwner             = hduser (auth:SIMPLE)
16/01/20 00:55:10 INFO namenode.FSNamesystem: supergroup          = supergroup
16/01/20 00:55:10 INFO namenode.FSNamesystem: isPermissionEnabled = true
16/01/20 00:55:10 INFO namenode.FSNamesystem: HA Enabled: false
16/01/20 00:55:10 INFO namenode.FSNamesystem: Append Enabled: true
16/01/20 00:55:10 INFO util.GSet: Computing capacity for map INodeMap
16/01/20 00:55:10 INFO util.GSet: VM type       = 64-bit
16/01/20 00:55:10 INFO util.GSet: 1.0% max memory 966.7 MB = 9.7 MB
16/01/20 00:55:10 INFO util.GSet: capacity      = 2^20 = 1048576 entries
16/01/20 00:55:10 INFO namenode.NameNode: Caching file names occuring more than 10 times
16/01/20 00:55:10 INFO util.GSet: Computing capacity for map cachedBlocks
16/01/20 00:55:10 INFO util.GSet: VM type       = 64-bit
16/01/20 00:55:10 INFO util.GSet: 0.25% max memory 966.7 MB = 2.4 MB
16/01/20 00:55:10 INFO util.GSet: capacity      = 2^18 = 262144 entries
16/01/20 00:55:10 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
16/01/20 00:55:10 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
16/01/20 00:55:10 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
16/01/20 00:55:10 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
16/01/20 00:55:10 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
16/01/20 00:55:10 INFO util.GSet: Computing capacity for map NameNodeRetryCache
16/01/20 00:55:10 INFO util.GSet: VM type       = 64-bit
16/01/20 00:55:10 INFO util.GSet: 0.029999999329447746% max memory 966.7 MB = 297.0 KB
16/01/20 00:55:10 INFO util.GSet: capacity      = 2^15 = 32768 entries
16/01/20 00:55:10 INFO namenode.NNConf: ACLs enabled? false
16/01/20 00:55:10 INFO namenode.NNConf: XAttrs enabled? true
16/01/20 00:55:10 INFO namenode.NNConf: Maximum size of an xattr: 16384
16/01/20 00:55:10 INFO namenode.FSImage: Allocated new BlockPoolId: BP-531368764-127.0.1.1-1453280110338
16/01/20 00:55:10 INFO common.Storage: Storage directory /usr/local/hadoop_store/hdfs/namenode has been successfully formatted.
16/01/20 00:55:10 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
16/01/20 00:55:10 INFO util.ExitUtil: Exiting with status 0
16/01/20 00:55:10 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at ubuntu/127.0.1.1
************************************************************/

Starting Hadoop
cd /usr/local/hadoop/sbin
hduser@ubuntu:~$ cd /usr/local/hadoop/sbin
hduser@ubuntu:/usr/local/hadoop/sbin$ start-all.sh
This script is Deprecated. Instead use start-dfs.sh and start-yarn.sh
16/01/20 00:57:42 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Starting namenodes on [localhost]
localhost: starting namenode, logging to /usr/local/hadoop/logs/hadoop-hduser-namenode-ubuntu.out
localhost: starting datanode, logging to /usr/local/hadoop/logs/hadoop-hduser-datanode-ubuntu.out
Starting secondary namenodes [0.0.0.0]
The authenticity of host '0.0.0.0 (0.0.0.0)' can't be established.
ECDSA key fingerprint is 38:c6:5d:f2:75:5f:c3:59:77:38:2b:31:6f:0e:00:82.
Are you sure you want to continue connecting (yes/no)? yes
0.0.0.0: Warning: Permanently added '0.0.0.0' (ECDSA) to the list of known hosts.
0.0.0.0: starting secondarynamenode, logging to /usr/local/hadoop/logs/hadoop-hduser-secondarynamenode-ubuntu.out
16/01/20 00:58:07 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
starting yarn daemons
starting resourcemanager, logging to /usr/local/hadoop/logs/yarn-hduser-resourcemanager-ubuntu.out
localhost: starting nodemanager, logging to /usr/local/hadoop/logs/yarn-hduser-nodemanager-ubuntu.out

hduser@ubuntu:/usr/local/hadoop/sbin$ jps
4020 Jps
3312 DataNode
3731 NodeManager
3482 SecondaryNameNode
3172 NameNode
3616 ResourceManager


--change the following line in
hduser@ubuntu:/usr/local/hbase/bin$ sudo vi ../conf/hbase-env.sh

# The java implementation to use.  Java 1.7+ required.
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64



--changing permissions
--avoid to handle su, root, sudo, it took too much time
--use the same user and group as hduser:hadoop
sudo chown -R hduser:hadoop hbase
hduser@ubuntu:/usr/local/hbase/bin$ ./start-hbase.sh

--It should show the following
hduser@ubuntu:/usr/local/hbase/bin$ jps
3596 HQuorumPeer
2390 NameNode
3767 HRegionServer
2718 SecondaryNameNode
2539 DataNode
2904 ResourceManager
3997 Jps
3652 HMaster
3029 NodeManager

--Testing hbase

hduser@ubuntu:/usr/local/hbase/bin$ sudo vi ../conf/hbase-env.sh
hduser@ubuntu:/usr/local/hbase/bin$ ./hbase shell
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/usr/local/hbase/lib/slf4j-log4j12-1.7.5.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/usr/local/hadoop/share/hadoop/common/lib/slf4j-log4j12-1.7.5.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
2016-01-31 21:42:47,489 WARN  [main] util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
HBase Shell; enter 'help<RETURN>' for list of supported commands.
Type "exit<RETURN>" to leave the HBase Shell
Version 1.1.2, rcc2b70cf03e3378800661ec5cab11eb43fafe0fc, Wed Aug 26 20:11:27 PDT 2015

hbase(main):001:0> 


--problem
ERROR: Can't get master address from ZooKeeper; znode data == null
http://stackoverflow.com/questions/22663484/get-error-cant-get-master-address-from-zookeeper-znode-data-null-when-us

--I have fixed this using the following configuration (taken from core-site.xml hadoop)
--cloudera provides good info
-- http://www.cloudera.com/documentation/archive/cdh/4-x/4-3-0/CDH4-Installation-Guide/cdh4ig_topic_20_5.html

hduser@ubuntu:/usr/local/hbase/bin$ sudo vi ../conf/hbase-site.xml

<configuration>

<property>
  <name>hbase.rootdir</name>
  <value>hdfs://localhost:54310/hbase</value>
</property>

<property>
  <name>hbase.cluster.distributed</name>
  <value>true</value>
</property>

</configuration>

--hbase connects with hdfs
--it created the directory for hbase database

hduser@ubuntu:/usr/local/hbase/bin$ hadoop fs -ls /hbase
16/01/31 22:32:46 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Found 7 items
drwxr-xr-x   - hduser supergroup          0 2016-01-31 22:28 /hbase/.tmp
drwxr-xr-x   - hduser supergroup          0 2016-01-31 22:28 /hbase/MasterProcWALs
drwxr-xr-x   - hduser supergroup          0 2016-01-31 22:28 /hbase/WALs
drwxr-xr-x   - hduser supergroup          0 2016-01-31 22:28 /hbase/data
-rw-r--r--   1 hduser supergroup         42 2016-01-31 22:28 /hbase/hbase.id
-rw-r--r--   1 hduser supergroup          7 2016-01-31 22:28 /hbase/hbase.version
drwxr-xr-x   - hduser supergroup          0 2016-01-31 22:28 /hbase/oldWALs
hduser@ubuntu:/usr/local/hbase/bin$ 


--testing hbase
hbase(main):001:0> create 'maverick', 'cf'
0 row(s) in 1.5450 seconds

=> Hbase::Table - maverick
hbase(main):002:0> list 'maverick'
TABLE                                                                                                                                                  
maverick                                                                                                                                               
1 row(s) in 0.0290 seconds

=> ["maverick"]
hbase(main):003:0> 

