package com.ssos.learn.zookeeper;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: ZookeeperDistributedLock
 * @Description: 基于zookeeper实现的可重入分布式锁
 * @Author: xwl
 * @Date: 2021/5/19 16:35
 * @Vsersion: 1.0
 */
@Slf4j
public class ZookeeperDistributedLock implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {
    private ZooKeeper zk;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private String pathName;
    /**
     * 分布式系统中唯一标示
     * server+threadId+随机数
     */
    private String threadName;


    public String getThreadName() {
        return threadName;
    }


    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getPathName() {
        return pathName;
    }

    /**
     * @param ip   zk ip
     * @param path 需要锁住的path
     */
    public ZookeeperDistributedLock(String ip, String path) {
        this.zk = getConnection(ip, path);
    }

    public void tryLock() {
        try {
            threadName = Thread.currentThread().getName() + "-" + RandomUtils.nextInt(10000, 19999);
            //判断锁状态（要求锁可重入）
            byte[] data = zk.getData("/", false, new Stat());
            if (data != null && new String(data).equals(threadName)) {
                return;
            }
            zk.create("/tryLock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "abc");
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            zk.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * //watch
     *
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.EventType type = watchedEvent.getType();
        switch (type) {
            case None:
                System.out.println("None");
                break;
            case NodeCreated:
                System.out.println("NodeCreated");
                break;
            case NodeDeleted:
                System.out.println("NodeDeleted");
                zk.getChildren("/", false, this, "123");
                break;
            case NodeDataChanged:
                System.out.println("NodeDataChanged");
                break;
            case NodeChildrenChanged:
                System.out.println("NodeChildrenChanged");
                break;
            case DataWatchRemoved:
                System.out.println("DataWatchRemoved");
                break;
            case ChildWatchRemoved:
                System.out.println("ChildWatchRemoved");
                break;
            case PersistentWatchRemoved:
                System.out.println("PersistentWatchRemoved");
                break;
            default:
                System.out.println("error");
        }
    }


    /**
     * Children2Callback
     *
     * @param rc
     * @param path
     * @param ctx
     * @param children
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        if (children == null) {
            return;
        }
        Collections.sort(children);
        int i = children.indexOf(pathName.substring(1));
        if (i == 0) {
            try {
                zk.setData("/", threadName.getBytes(), stat.getVersion());
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        } else {
            zk.exists("/" + children.get(i - 1), this, this, "123");
        }
    }

    /**
     * //StringCallback
     * 节点创建事件
     *
     * @param rc
     * @param path
     * @param ctx
     * @param name
     */
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        //StringCallback
        setPathName(name);
        if (name != null) {
            zk.getChildren("/", false, this, "123");
        }

    }

    /**
     * //StatCallback
     *
     * @param rc
     * @param path
     * @param ctx
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat == null) {
            zk.getChildren("/", false, this, "123");
        }
    }


    public ZooKeeper getConnection(String ip, String path) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(ip + path, 2000, new Watcher() {
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
                            log.debug("连接完成");
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
                            log.debug("断开事件");
                            break;
                        default:
                            log.debug("zk");
                    }
                    Event.EventType type = watchedEvent.getType();
                    switch (type) {
                        case None:
                            break;
                        case NodeCreated:
                            log.debug("create");
                            break;
                        case NodeDeleted:
                            log.debug("delete");
                            break;
                        case NodeDataChanged:
                            log.debug("node data changed");
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
                            log.debug("zk");
                    }

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            assert zooKeeper != null;
            if (zooKeeper.exists("/", false) == null) {
                zooKeeper.create("/", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.debug("初始化根节点");
            }
            countDownLatch.await();
        } catch (Exception e) {
            log.debug("根节点初始化完成");
        }
        return zooKeeper;
    }

}
