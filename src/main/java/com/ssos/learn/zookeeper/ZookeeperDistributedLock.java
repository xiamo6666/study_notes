package com.ssos.learn.zookeeper;

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

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getPathName() {
        return pathName;
    }

    public void tryLock() {
        try {
            threadName = Thread.currentThread().getName();
            //判断锁状态（要求锁可重入）
            byte[] data = zk.getData("/", false, new Stat());
            if (new String(data).equals(threadName)) {
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
        } catch (InterruptedException e) {
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
}
