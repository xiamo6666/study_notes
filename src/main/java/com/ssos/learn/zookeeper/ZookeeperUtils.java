package com.ssos.learn.zookeeper;


import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @ClassName: ZookeeperUtils
 * @Description: 事件监控
 * @Author: xwl
 * @Date: 2021/5/18 16:44
 * @Vsersion: 1.0
 */

public class ZookeeperUtils implements AsyncCallback.DataCallback, AsyncCallback.StatCallback, Watcher {
    private String path;
    private ZooKeeper zooKeeper;

    public ZookeeperUtils(String path, ZooKeeper zookeeper) {
        this.path = path;
        this.zooKeeper = zookeeper;

    }


    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        System.out.println(new String(bytes));
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                break;
            case NodeCreated:
                System.out.println("create");
                zooKeeper.getData(path, this, this, null);
                break;
            case NodeDeleted:
                System.out.println("delete");
                zooKeeper.getData(path, this, this, null);
                break;
            case NodeDataChanged:
                System.out.println("changed");
                zooKeeper.getData(path, this, this, null);
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
                System.out.println("test");
        }

    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {

    }
}
