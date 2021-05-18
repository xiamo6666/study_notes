package com.ssos.learn.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * @ClassName: ZookeeperDemo
 * @Description: dto
 * @Author: xwl
 * @Date: 2021/5/12 15:17
 * @Vsersion: 1.0
 */

public class ZookeeperDemo {
    public static void main(String[] args) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        //在zookeeper的连接中，没有连接池的概念，一次连接就是一个session（生命周期）
        ZooKeeper zooKeeper = new ZooKeeper("10.10.11.104:2181", 200, new Watcher() {
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
                        System.out.println("wocao");
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
                        System.out.println("wocao");
                }

            }

        });
        countDownLatch.await();
        ZookeeperUtils zookeeperUtils = new ZookeeperUtils("/aaa", zooKeeper);
        zooKeeper.getData("/aaa", zookeeperUtils, zookeeperUtils, null);
        while (true){

        }

    }
}
