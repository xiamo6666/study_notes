package com.ssos.learn.concurrent.dcl;

import java.util.HashMap;

/**
 * @ClassName: DCLDemo
 * @Description: dto
 * @Author: xwl
 * @Date: 2021/11/4 17:38
 * @Vsersion: 1.0
 */

public class DCLDemo {
    private Node head;
    private Node last;
    private Integer prevKey;

    static class Node {
        Node(Integer key, Integer value) {
            this.value = value;
            this.key = key;
        }

        Integer key;
        Integer value;
        Node prevNode;
        Node nextNode;
    }

    private int capacity;

    private HashMap<Integer, Node> map = new HashMap();


    public DCLDemo(int capacity) {

        if (capacity >= 1 && capacity <= 3000) {
            this.capacity = capacity;
        } else {
            this.capacity = 3000;
        }

    }

    public int get(int key) {
        final Node node = map.get(key);
        if (node == null) {
            return -1;
        }
        if (last == node) {
            return node.value;
        }
        if (head == node) {
            head = node.nextNode;
            head.prevNode = null;
        }

        final Node lastNode = this.last;

        final Node oldPrevNode = node.prevNode;
        oldPrevNode.nextNode = node.nextNode;
        node.nextNode.prevNode = oldPrevNode;

        lastNode.nextNode = node;
        node.prevNode = lastNode;

        this.last = node;
        this.prevKey = key;
        return node.value;

    }

    public void put(int key, int value) {

        Node node = new Node(key, value);
        if (prevKey != null && prevKey == key && map.containsKey(key)) {
            map.get(key).value = value;
            return;
        }
        if (head == null) {
            head = node;
            last = node;
        } else {
            Node prevNode = map.get(prevKey);
            prevNode.nextNode = node;
            node.prevNode = prevNode;
            this.last = node;
        }
        if (map.size() >= capacity) {

            //移除头结点
            Node headNode = this.head;
            map.remove(headNode.key);
            this.head = headNode.nextNode;


        }
        this.prevKey = key;
        map.put(key, node);

    }

    public static void main(String[] args) {

        final DCLDemo lRUCache = new DCLDemo(3);
        System.out.println(lRUCache.get(2));
        lRUCache.put(2, 6);
        System.out.println(lRUCache.get(1));
        lRUCache.put(1, 5);
        lRUCache.put(2, 2);
        lRUCache.put(3, 2);
        System.out.println(lRUCache.get(1));
        lRUCache.put(4, 2);
        lRUCache.put(5, 2);
        System.out.println(lRUCache.get(1));
        lRUCache.put(6, 2);
        System.out.println(lRUCache.get(1));
        System.out.println(lRUCache.get(2));


    }

}
