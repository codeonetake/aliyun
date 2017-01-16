package com.aliyun.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**   
 * Redis操作接口  
 */
public class RedisPool {
    private static JedisPool pool = null;
    
    /**
     * 构建redis连接池
     * 
     * @param ip
     * @param port
     * @return JedisPool
     */
    public static JedisPool getPool() {
        if (pool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
            //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
            config.setMaxTotal(500);
            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMaxIdle(5);
            //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWaitMillis(60000);
            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setTestOnBorrow(true);
            pool = new JedisPool(config, "59.110.54.171", 6297);
        }
        return pool;
    }
    
    /**
     * 返还到连接池
     * 
     * @param pool 
     * @param redis
     */
    public static void returnResource(JedisPool pool, Jedis redis) {
        if (redis != null) {
            pool.returnResource(redis);
        }
    }
    
    /**
     * 获取数据
     * 
     * @param key
     * @return
     */
    public static String get(String key){
        String value = null;
        
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = getPool();
            jedis = pool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            //释放redis对象
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(pool, jedis);
        }
        
        return value;
    }
    
    public static void set(String key,String value){
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = getPool();
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            //释放redis对象
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(pool, jedis);
        }
    }
    
    public static void set(String key,String value,int time){
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = getPool();
            jedis = pool.getResource();
            jedis.set(key, value);
            jedis.expire(key, time);
        } catch (Exception e) {
            //释放redis对象
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(pool, jedis);
        }
    }
    
    public static boolean isExist(String key){
        JedisPool pool = null;
        Jedis jedis = null;
        boolean exist = false;
        try {
            pool = getPool();
            jedis = pool.getResource();
            exist = jedis.exists(key);
        } catch (Exception e) {
            //释放redis对象
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(pool, jedis);
        }
        return exist;
    }
    
    public static void main(String[] args) {
    		//appid=wx58def8103bfad533
    		//appSecret=545eff181fea837a37032abee5ba1bd3
    		//set("appid", "wx58def8103bfad533");
    		//set("appSecret", "545eff181fea837a37032abee5ba1bd3");
    		set("menuJson", "{\"button\":[{\"type\":\"view\",\"name\":\"博客首页\",\"key\":\"blogIndex\",\"url\":\"http://codeawl.com\"},{\"name\": \"文章列表\", \"sub_button\": [{\"type\": \"view\", \"name\": \"最近文章\", \"key\": \"recentArticle\", \"url\": \"http://codeawl.com\"},{\"type\": \"view\", \"name\": \"文章列表\", \"key\": \"articleList\", \"url\": \"http://codeawl.com\"},{\"type\": \"view\", \"name\": \"热门文章\", \"key\": \"hotArticle\", \"url\": \"http://codeawl.com\"}]}]}");
	}
}