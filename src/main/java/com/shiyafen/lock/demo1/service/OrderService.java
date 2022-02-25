package com.shiyafen.lock.demo1.service;

import com.shiyafen.lock.demo1.utils.JedisPoolRedisLock;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class OrderService {


    private static final String LOCKKEY = "test_lock";

    public static void service() {
        // 1.获取锁
        JedisPoolRedisLock mayiktRedisLock = new JedisPoolRedisLock();
        String lockValue = mayiktRedisLock.getLock(LOCKKEY, 5000, 5000);
        if (StringUtils.isEmpty(lockValue)) {
            System.out.println(Thread.currentThread().getName() + "，获取锁失败了");
            return;
        }
        // 执行我们的业务逻辑
        System.out.println(Thread.currentThread().getName() + "，获取锁成功:lockValue:" + lockValue);
//
//        // 3.释放锁
        mayiktRedisLock.unLock(LOCKKEY, lockValue);
    }

    public static void main(String[] args) {
        service();
    }


    /***
     *
     * 尝试获取锁为什么次数限制？
     * 如果我们业务逻辑5s 内没有执行完毕呢？
     *
     * 分场景：
     * 1.锁的超时时间根据业务场景来预估
     * 2.可以自己延迟锁的时间
     * 3.在提交事务的时候检查锁是否已经超时 如果已经超时则回滚（手动回滚）否则提交。
     *
     * 仅限于单机版本
     */
}
