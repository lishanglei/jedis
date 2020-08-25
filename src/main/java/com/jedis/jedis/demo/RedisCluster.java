package com.jedis.jedis.demo;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lishanglei
 * @version v1.0.0
 * @date 2020/8/24
 * @Description Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2020/8/24              lishanglei      v1.0.0           Created
 */
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
