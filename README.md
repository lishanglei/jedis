## Redis

Redis

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

      wget http://download.redis.io/releases/redis-5.0.8.tar.gz?_ga=2.64859303.942109333.1597134648-971247183.1597134648

   3. ###### 解压

      tar zxvf redis-5.0.8.tar.gz\?_ga\=2.64859303.942109333.1597134648-971247183.159713464

      mv redis-5.0.8 redis

   4. ###### 进入到redis目录执行编译命令

      cd redis

      make

      make PREFIX-/usr/local/redis install

   5. ###### 启动服务端

      src/redis-server

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200811204545486.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0MDY5ODM5,size_16,color_FFFFFF,t_70)


   6. ###### 启动客户端

      src/redis-cli

      ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200811204559538.png)


   7. ###### 现在是前台启动,我们修改配置文件指定配置文件后台启动

      vim redis.conf

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200811204607508.png)


      src/redis-server ./redis.conf

   8. ###### 关闭redis

      src/redis-cli shutdown

8. ##### redis的持久化

   - RDBfan方式

     RedisDataBase(RDB)就是在指定时间间隔内将内存中的数据集快照写入磁盘,数据恢复时将快照文件直接再读取到内存.

     RDB保存了在某个时间点的数据集,存储在一个二进制文件中,只有一个文件,默认是dump.rdb.RDB技术非常适合做备份,可以保存最近一个小时,一天,一个月的全部数据,保存数据是在单独的进程中写文件,不影响Redis正常使用,RDB回复数据比AOF快

   - AOF方式

     Append-onlFile(AOF),Redis每次接收到一条改变数据的命令时,它将把该命令写到一个AOF文件中(只记录写操作,不记录读操作),当redis重启时,它通过执行AOF文件中的所有命令来回复数据

10.1. ##### 

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

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020082417403883.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0MDY5ODM5,size_16,color_FFFFFF,t_70#pic_center)


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

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200824174054925.png#pic_center)


       ./src/redis-cli -a 111111 --cluster reshard 192.168.5.62:8002

       #你想要从原集群移动多少节点至新节点

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200824174107915.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0MDY5ODM5,size_16,color_FFFFFF,t_70#pic_center)

       #接收节点的id是多少

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020082417412112.png#pic_center)


       #从哪个源节点移出

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200824174128961.png#pic_center)


       输入done即添加新节点完成

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200824174137854.png#pic_center)


       新增加的节点已经分配到哈希槽了

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200824174146761.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0MDY5ODM5,size_16,color_FFFFFF,t_70#pic_center)

1. ##### Redis持久化机制

   Redis是一个支持持久化的内存数据库,通过持久化机制把内存中的数据同步到硬盘文件来保证数据持久化.当Redis重启后通过把硬盘文件重新加载到内存,就能达到回复数据的目的

   **实现**:单独创建fork()一个子进程,将当前父进程的数据库数据复制到子进程的内存中,然后由子进程写入到临时文件中.持久化的过程结束了,再用这个临时文件代替上次的快照文件,然后子进程退出,内存释放

   **RDB:**是redis默认的持久化方式,按照一定的时间周期策略把内存中的数据以快照的形式保存到硬盘的二进制文件.即snampshot快照存储,对应产生的数据文件为dump.rdb,通过配置文件张的save参数来定义快照的周期(快照可以是其所表示数据的一个副本,也可以是数据的复制品)

   **AOF:**redis会将每一个收到的命令通过write函数追加到文件最后,类似于mysql的binlog,当redis重启会通过重新执行文件中保存的写命令来在内存中重建整个数据的内容.

   当两种方式同时开启时,数据恢复Redis会优先选择AOF回复

2. ##### 缓存雪崩,缓存穿透,缓存预热,缓存更新,缓存降级等问题

   - 缓存雪崩

     原本应该访问缓存的请求大量请求数据库,对数据库cpu和内存造成巨大压力.

     解决办法:大多数系统设计者考虑用加锁或者队列的方式保证不会有大量的线程对数据库一次性进行读写,还可以分散设置缓存过期时间

   - 缓存穿透

     布隆过滤器:将所有可能的数据哈希到一个足够大的bitMap中,

   - 缓存预热

     - 直接写个缓存刷新页面,上线时手工操作

   - 缓存更新

   - 缓存降级

     当访问量剧增,服务出现问题(如相应时间慢或不响应)或非核心服务影响到核心流程的性能时,仍然需要保证服务还是可用的,即时是有损服务.系统可以根据一些关键数据进行自动降级,也可以配置开关实现人工降级

     降级的最终目的是保证核心服务可用,即使是有损的.而且有些服务是无法降级的(比如加入购物车,结算)

3. ##### 单线程的redis为什么这么快

   - 纯内存操作
   - 单线程操作,避免频繁的上下文切换
   - 采用了非阻塞I/O多路复用机制

4. ##### redis事务

   redis事务功能是通过multi,exec,discard,watch四个原语实现的

   redis回家一个事务中的所有命令序列化然后顺序执行

   redis不支持回滚,在事务失败时不进行回滚,而是继续执行余下的命令

   如果一个事务中的命令出现错误,那么所有的命令都不会执行

   如果一个事务中出现运行错误,那么正确的命令会被执行
