package com.shiyafen.lock.demo1.utils;

import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 *
 */
public class JedisPoolRedisLock {

    private static int lockSuccess = 1;

    /**
     * @param lockKey      在Redis中创建的key值
     * @param notLockTimie 尝试获取锁超时时间
     * @return 返回lock成功值
     */
    public String getLock(String lockKey, int notLockTimie, int timeOut) {
        //获取Redis连接
        Jedis jedis = RedisUtil.getJedis();
        // 计算我们尝试获取锁超时时间
        Long endTime = System.currentTimeMillis() + notLockTimie;
        //  当前系统时间小于endTime说明获取锁没有超时 继续循环 否则情况下推出循环
        while (System.currentTimeMillis() < endTime) {
            System.out.println(System.currentTimeMillis()+"尝试获取锁=="+endTime);
            String lockValue = UUID.randomUUID().toString();
            // 当多个不同的jvm同时创建一个相同的rediskey 只要谁能够创建成功谁就能够获取锁
            if (jedis.setnx(lockKey, lockValue) == lockSuccess) {
                //TODO 根据业务校验是否需要加 加上有效期 自动删除
                jedis.expire(lockKey, timeOut / 1000);
                return lockValue;
                // 退出循环
            }
            // 否则情况下 继续循环
        }
        /*
        1645779244084尝试获取锁==1645779249084
        1645779244208尝试获取锁==1645779249084
        1645779244208尝试获取锁==1645779249084
        1645779244208尝试获取锁==1645779249084
        1645779244209尝试获取锁==1645779249084
        1645779244209尝试获取锁==1645779249084
        1645779244210尝试获取锁==1645779249084
        1645779244210尝试获取锁==1645779249084
        1645779244210尝试获取锁==1645779249084
        1645779244211尝试获取锁==1645779249084
                        ……
        1645779246915尝试获取锁==1645779249084
        main，获取锁成功:lockValue:d6f124cb-1bdc-42bf-b9a5-bcc7d40c9533

        Process finished with exit code 0

        * */
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 释放锁
     *
     * @return
     */
    public boolean unLock(String locKey, String lockValue) {
        //获取Redis连接
        Jedis jedis = RedisUtil.getJedis();
        try {
            // 判断获取锁的时候保证自己删除自己
            if (lockValue.equals(jedis.get(locKey))) {
                return jedis.del(locKey) > 0 ? true : false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }
    /**
     *A JVM 获取锁
     *A JVM 执行行业务
     *A JVM 释放锁
     *
     */
}
