package com.lhj.sso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

//@SpringBootApplication + @Bean 配置类
@SpringBootApplication
public class RedisConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public JedisCluster getJedisCluster() {
        // 1.通过RedisProperties获取Host和Port
        String nodes = redisProperties.getNodes();
        // 2.切割nodes
        String[] split = nodes.split(",");
        // 3.创建Set集合对象(泛型是HostAndPort)
        Set<HostAndPort> hostAndPortSet = new HashSet<HostAndPort>();
        // 4.遍历切割后的数组
        for(String hostPort : split) {
            // 5.再次切割(分隔符是":")
            String[] str = hostPort.split(":");
            // 6.创建HostAndPort对象，并且把切割好的数组传入
            HostAndPort hostAndPort = new HostAndPort(str[0].trim(), Integer.parseInt(str[1].trim()));
            // 7.把hostAndPort对象放入Set集合中
            hostAndPortSet.add(hostAndPort);
        }
        // 9.创建JedisCluster对象
        JedisCluster jedisCluster = new JedisCluster(hostAndPortSet, redisProperties.getCommandTimeout(), redisProperties.getMaxAttmpts());
        // 10.返回jedisCluster对象
        return jedisCluster;
    }

}
