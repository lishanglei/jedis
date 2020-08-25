## Redis

1. #### NoSQL

   - High performance对数据库高并发读写的需求
   - Huge storage对海量数据高效率的存储和访问的需求
   - High Scalability && High Availability 对数据库的高扩展性和高可用性的需求

2. #### 数据类型

   redis是C语言开发的一个开源的高性能的键值对(key-value)内存数据库,目前Redis支持的键值数据类型如下:

   - 字符串类型string
   - 散列类型hash
   - 列表类型list
   - 集合类型set
   - 有序集合类型zset

3. #### 特点

   - redis支持数据的持久化,即可以将内存中的数据保存到磁盘中,重启时可以再次加载使用
   - redis不仅仅支持简单的key-value类型的数据,同时还提供list,set,zset,hash等数据结构存储
   - redis支持数据的备份,即master-slave模式的数据备份

4. #### 应用场景

   - 取最新N个数据(排行榜)
   - 需要精确设定过期时间的应用
   - 计数器应用
   - 实时性要求的高并发读写
   - 消息队列的pub/sub
   - 构建队列
   - 缓存

5. #### 优缺点

   1. 优点

      - 读数据高并发读写(基于内存)
      - 对海量数据的高效率存储和访问
      - 对数据的可高扩展性和高可用性
        - 垂直扩展:提升硬件
        - 水平扩展:集群

   2. 缺点

      redis无法做到太复杂的数据关系模型(ACID处理非常简单)

6. #### Redis面向互联网的解决方案

   - 主从:一主多从,主机可写,从机备份,类似于mysql的读写分离,存在问题是一旦主节点down掉,整个redis不可用

   - 哨兵(2.x):启用一个哨兵程序(节点),监控其余节点状态,根据选举策略,进行主从切换.

     缺点:每个节点的数据依旧是一致的,仍无法实现分布式的数据库

   - 集群(3.x):结合上述两种模式,多主多从,实现高可用分布式数据存储

7. #### Redis安装

   1. ###### 因为Redis是C语言开发的,首先安装C语言运行环境

      yum install gcc-c++

   2. ###### 官网下载最新安装包

      cd /usr/local

      wget http://download.redis.io/releases/redis-5.0.8.tar.gz

   3. ###### 解压

      tar zxvf redis-5.0.8.tar.gz\?_ga\=2.64859303.942109333.1597134648-971247183.159713464

      mv redis-5.0.8 redis

   4. ###### 进入到redis目录执行编译命令

      cd redis

      make

      make PREFIX-/usr/local/redis install

   5. ###### 启动服务端

      src/redis-server
      ![image-20200811171341364](D:\mingbyte\typora\image-20200811171341364.png)

   6. ###### 启动客户端

      src/redis-cli

      ![image-20200811171435086](D:\mingbyte\typora\image-20200811171435086.png)

   7. ###### 现在是前台启动,我们修改配置文件指定配置文件后台启动

      vim redis.conf

      ![image-20200811172109884](D:\mingbyte\typora\image-20200811172109884.png)

      src/redis-server ./redis.conf

   8. ###### 关闭redis

      src/redis-cli shutdown

8. ##### redis的持久化

   - RDB方式

     RedisDataBase(RDB)就是在指定时间间隔内将内存中的数据集快照写入磁盘,数据恢复时将快照文件直接再读取到内存.

     RDB保存了在某个时间点的数据集,存储在一个二进制文件中,只有一个文件,默认是dump.rdb.RDB技术非常适合做备份,可以保存最近一个小时,一天,一个月的全部数据,保存数据是在单独的进程中写文件,不影响Redis正常使用,RDB回复数据比AOF快

   - AOF方式

     Append-onlFile(AOF),Redis每次接收到一条改变数据的命令时,它将把该命令写到一个AOF文件中(只记录写操作,不记录读操作),当redis重启时,它通过执行AOF文件中的所有命令来回复数据

9. ###### redis集群简介

   - redis是一个开源的key value存储系统,redis集群采用P2P模式,完全去中心化,不存在中心节点或者代理节点
   - redis集群是没有一个统一入口的,客户端client连接集群的时候连接集群中的任意节点即可,集群内部的节点是相互通信的(PING-PONG机制),每个节点都是一个redis实例
   - 为了实现集群的高可用,即判断节点是否健康,redis-cluster由这么一个投票容错机制;如果集群中超过半数的节点投票认为某个节点挂了,那么这个节点就挂了.
   - 那么如何判断集群是否挂了呢?如果集群中任意一个节点挂了,而且该节点没有从节点,那么这个集群就挂了.
   - 那么为什么任意一个节点挂了且没有从节点就代表这个集群挂了呢?因为集群内设置了16384个hash slot(哈希槽),并且把所有的物理节点银蛇到这16384[0,16383]个solt上,或者说把这些solt均等分给各个节点.当需要在Redis集群存放一个数据(key-value)时,redis会先对这个key进行crc16算法,然后得到一个结果,在把这个结果对16384取余,这个余数就会对应[0-16383]其中的一个槽,进而决定key-value存储到哪个节点中.所以一旦某个节点挂了,该节点对应的slot就无法使用,那么就会导致集群无法正常工作
   - 综上所述,每个Redis集群理论上最多可以有16384个节点.

10. ##### redis集群搭建

    | id   | 端口 | 备注   |
    | ---- | ---- | ------ |
    | 1    | 8001 | 主节点 |
    | 2    | 8002 | 主节点 |
    | 3    | 8003 | 主节点 |
    | 4    | 8004 | 从节点 |
    | 5    | 8005 | 从节点 |
    | 6    | 8006 | 从节点 |
    |      |      |        |

    

    1. ###### 创建集群目录

       mkdir rediscluster

    2. ###### 创建6个节点目录

       cd rediscluster 

       mkdir 8001 

    3. ###### 复制配置文件

       cp /usr/local/redis-5.0.8/redis.conf  ../rediscluster/8001

    4. ###### 修改配置文件

       vim redis.conf

       port 8001

       dir /usr/local/rediscluster/8001

       cluster-enabled yes

       cluster-config-file nodes-8001.conf

       daemonize yes

       protected-mode no

       appendonly yes
       requirepass 111111

       masterauth 111111

    5. ###### 复制节点

       cp -r 8001/ 8002

       cp -r 8001/ 8003

       cp -r 8001/ 8004

       cp -r 8001/ 8005

       cp -r 8001/ 8006

    6. ###### 批量替换配置文件中的端口配置

       :%s/8001/8002/g

       :%s/8001/8003/g

       :%s/8001/8004/g

       :%s/8001/8005/g

       :%s/8001/8006/g

    7. ###### 依据不同的配置文件启动redis集群

       ./src/redis-server ../rediscluster/8001/redis.conf

       ./src/redis-server ../rediscluster/8002/redis.conf

       ./src/redis-server ../rediscluster/8003/redis.conf

       ./src/redis-server ../rediscluster/8004/redis.conf

       ./src/redis-server ../rediscluster/8005/redis.conf

       ./src/redis-server ../rediscluster/8006/redis.conf

    8. ###### 集群关联启动客户端

       ./src/redis-cli -a 111111 --cluster create --cluster-replicas 1  192.168.5.62:8001 192.168.5.62:8002 192.168.5.62:8003 192.168.5.62:8004 192.168.5.62:8005 192.168.5.62:8006

       ![image-20200824163846942](D:\mingbyte\typora\image-20200824163846942.png)

    9. ###### 连接

       ./src/redis-cli  -a 111111 -c -h 192.168.5.62 -p 8001

    10. ###### 使用java客户端jedis连接

        ```xml
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>3.0.1</version>
        </dependency>
        ```

        ```java
        public class RedisCluster {
        
            public static void main(String[] args) {
        
                Set<HostAndPort> clusterNodes =new HashSet<>();
                clusterNodes.add(new HostAndPort("192.168.5.62",8001));
                clusterNodes.add(new HostAndPort("192.168.5.62",8002));
                clusterNodes.add(new HostAndPort("192.168.5.62",8003));
                clusterNodes.add(new HostAndPort("192.168.5.62",8004));
                clusterNodes.add(new HostAndPort("192.168.5.62",8005));
                clusterNodes.add(new HostAndPort("192.168.5.62",8006));
        
                JedisPoolConfig jedisPoolConfig =new JedisPoolConfig();
                jedisPoolConfig.setMaxTotal(100);
                jedisPoolConfig.setMaxIdle(10);
                jedisPoolConfig.setTestOnBorrow(true);
        
                JedisCluster jedisCluster =new JedisCluster(clusterNodes,6000,5000,10,"111111", jedisPoolConfig);
                System.out.println(jedisCluster.set("name","zhangsan"));
                System.out.println(jedisCluster.set("age","18"));
                System.out.println(jedisCluster.get("name"));
                System.out.println(jedisCluster.get("age"));
                jedisCluster.close();
            }
        
        }
        ```

    11. ###### 添加新节点

        复制配置文件后启动

        ./src/redis-server ../rediscluster/8007/redis.conf

        ./src/redis-cli -a 111111 --cluster add-node 192.168.5.62:8007 192.168.5.62:8001

        **查看节点信息**

        ./src/redis-cli -a 111111 -c -h 192.168.5.62 -p 8001

        ![image-20200824171430696](D:\mingbyte\typora\image-20200824171430696.png)

        ./src/redis-cli -a 111111 --cluster reshard 192.168.5.62:8002

        #你想要从原集群移动多少节点至新节点

        ![image-20200824171924790](D:\mingbyte\typora\image-20200824171924790.png)

        #接收节点的id是多少

        ![image-20200824172042643](D:\mingbyte\typora\image-20200824172042643.png)

        #从哪个源节点移出

        ![image-20200824172133092](D:\mingbyte\typora\image-20200824172133092.png)

        输入done即添加新节点完成

        ![image-20200824172212468](D:\mingbyte\typora\image-20200824172212468.png)

        新增加的节点已经分配到哈希槽了

        ![image-20200824172354212](D:\mingbyte\typora\image-20200824172354212.png)