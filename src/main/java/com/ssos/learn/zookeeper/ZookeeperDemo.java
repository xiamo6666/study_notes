package com.ssos.learn.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * @ClassName: ZookeeperDemo
 * @Description: dto
 * @Author: xwl
 * @Date: 2021/5/12 15:17
 * @Vsersion: 1.0
 */

public class ZookeeperDemo {

    @Test
    public void watcherTest() throws Exception {
        ZooKeeper connection = getConnection();
        ZookeeperUtils zookeeperUtils = new ZookeeperUtils("/aaa", connection);
        connection.getData("/aaa", zookeeperUtils, zookeeperUtils, null);
//        zooKeeper.create("/deviceNumber","".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        semaphore.acquire();
    }

    public ZooKeeper getConnection() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper("10.10.11.104:2181/lock", 2000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    Event.KeeperState state = watchedEvent.getState();
                    switch (state) {
                        case Unknown:
                            break;
                        case Disconnected:
                            break;
                        case NoSyncConnected:
                            break;
                        case SyncConnected:
                            countDownLatch.countDown();
                            System.out.println("连接完成");
                            break;
                        case AuthFailed:
                            break;
                        case ConnectedReadOnly:
                            break;
                        case SaslAuthenticated:
                            break;
                        case Expired:
                            break;
                        case Closed:
                            break;
                        default:
                            System.out.println("zk");
                    }
                    Event.EventType type = watchedEvent.getType();
                    switch (type) {
                        case None:
                            break;
                        case NodeCreated:
                            System.out.println("create");
                            break;
                        case NodeDeleted:
                            System.out.println("delete");
                            break;
                        case NodeDataChanged:
                            System.out.println("node data changed");
                            break;
                        case NodeChildrenChanged:
                            break;
                        case DataWatchRemoved:
                            break;
                        case ChildWatchRemoved:
                            break;
                        case PersistentWatchRemoved:
                            break;
                        default:
                            System.out.println("zk");
                    }

                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }


    /**
     * 分布式锁demo
     * 抢锁，释放
     */
    @Test
    public void lock() throws Exception {

        ZookeeperDistributedLock zookeeperDistributedLock = new ZookeeperDistributedLock();
        ZookeeperDistributedLock zookeeperDistributedLock1 = new ZookeeperDistributedLock();
        zookeeperDistributedLock.setZk(getConnection());
        zookeeperDistributedLock1.setZk(getConnection());
        zookeeperDistributedLock.tryLock();
        System.out.println(zookeeperDistributedLock.getThreadName() + ":获取到分布式锁");
        zookeeperDistributedLock1.tryLock();
        System.out.println("可重入锁");
        System.out.println(zookeeperDistributedLock.getThreadName() + ":自动释放分布式锁");
        zookeeperDistributedLock.release();

    }


    /**
     * 获取子节点
     *
     * @throws InterruptedException
     */
    @Test
    public void testChildren() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        getConnection().getChildren("/lock", false, new AsyncCallback.Children2Callback() {
            @Override
            public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
                System.out.println(children);
                countDownLatch.countDown();
            }
        }, "123");
        countDownLatch.await();
    }
}
