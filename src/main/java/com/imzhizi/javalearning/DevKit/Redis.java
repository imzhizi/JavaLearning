package com.imzhizi.javalearning.DevKit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import lombok.SneakyThrows;
import org.aspectj.weaver.ast.Or;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDate;

/**
 * created by zhizi
 * on 4/22/20 20:56
 */
public class Redis {

    @Data
    static class Order implements Serializable {
        private long id;
        private double money;
        private LocalDate date;
        private Long uid;

        public Order() {
        }

        public Order(int id, double money, LocalDate date) {
            this.id = id;
            this.money = money;
            this.date = date;
        }
    }

    private Jedis jedis;

    @Before
    public void before() throws IOException {
        jedis = new Jedis("localhost", 6379);
    }

    /**
     * 通过FastJson实现序列化和反序列化
     * [为Redis配置自定义fastJson序列化工具类 - Bingo许的个人空间 - OSCHINA](https://my.oschina.net/u/872813/blog/3052565 )
     * [Spring Boot Redis 序列化方案的选择 - 掘金](https://juejin.im/post/5d5e10d2e51d4561b416d487#heading-4 )
     */
    @Test
    public void init() {
        jedis.flushDB();
        Order o1 = new Order(1, 2.5, LocalDate.of(2015, 4, 17));
        Order o2 = new Order(2, 50, LocalDate.of(2015, 4, 18));
        Order o3 = new Order(3, 99.8, LocalDate.of(2015, 4, 19));

        jedis.set("order:1".getBytes(), JSON.toJSONBytes(o1, SerializerFeature.WriteClassName));
        jedis.set("order:2".getBytes(), JSON.toJSONBytes(o2, SerializerFeature.WriteClassName));
        jedis.set("order:3".getBytes(), JSON.toJSONBytes(o3, SerializerFeature.WriteClassName));
    }

    @Test
    public void 查询() {
        System.out.println(getById(1).getDate());
        System.out.println(getById(2).getDate());
        System.out.println(getById(3).getDate());
    }

    public Order getById(long oid) {
        return JSONObject.parseObject(jedis.get(("order:" + oid).getBytes()), Order.class);
    }
}
