package com.ssos.learn.io.net.nio.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: EventLoopGroup
 * @Description: dto
 * @Author: xwl
 * @Date: 2021/11/17 17:09
 * @Vsersion: 1.0
 */

public class EventLoopGroup {
    private EventLoop[] eventLoop;
    private ServerSocketChannel socketChannel;
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private int size;

    public EventLoopGroup(int size) {
        this.size = size;
        this.eventLoop = new EventLoop[size];
        for (int i = 0; i < size; i++) {
            eventLoop[i] = new EventLoop();
            new Thread(eventLoop[i]).start();

        }
    }


    public static void main(String[] args) throws Exception {
        final EventLoopGroup eventLoopGroup = new EventLoopGroup(5);
        eventLoopGroup.bind(new InetSocketAddress(9000));
    }

    private void bind(InetSocketAddress inetSocketAddress) {
        try {
            this.socketChannel =
                    ServerSocketChannel
                            .open()
                            .bind(inetSocketAddress);
            this.socketChannel.configureBlocking(false);
            EventLoop eventLoop = getSelect();
            eventLoop.linkedBlockingQueue.put(this.socketChannel);
            //唤醒select.select()堵塞
            eventLoop.selector.wakeup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 轮询选择一个selector
     *
     * @return
     */
    private EventLoop getSelect() {
        return eventLoop[atomicInteger.getAndIncrement() % size];
    }

    class EventLoop implements Runnable {
        private Selector selector;
        private final LinkedBlockingQueue<Channel> linkedBlockingQueue = new LinkedBlockingQueue<Channel>();

        EventLoop() {
            try {
                this.selector = Selector.open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            for (; ; ) {
                try {
                    while (selector.select() > 0) {
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();
                            if (key.isAcceptable()) {
                                System.out.println(Thread.currentThread().getName() + "accept event");
                                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                                SocketChannel socketChannel = serverSocketChannel.accept();
                                EventLoop eventLoop = getSelect();
                                try {
                                    eventLoop.linkedBlockingQueue.put(socketChannel);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                eventLoop.selector.wakeup();
                            }
                            if (key.isReadable()) {
                                System.out.println(Thread.currentThread().getName() + "read event");
                                SocketChannel client = (SocketChannel) key.channel();
                                ByteBuffer buffer = (ByteBuffer) key.attachment();
                                buffer.clear();
                                int read = 0;
                                while (true) {
                                    read = client.read(buffer);
                                    if (read > 0) {
                                        buffer.flip();
                                        byte[] bytes = new byte[buffer.limit()];
                                        buffer.get(bytes);
                                        System.out.println(new String(bytes));
                                    } else if (read == 0) {

                                        break;
                                    } else {
                                        client.close();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //处理逻辑

                if (!linkedBlockingQueue.isEmpty()) {
                    try {
                        Channel channel = linkedBlockingQueue.take();
                        if (channel instanceof ServerSocketChannel) {
                            ((ServerSocketChannel) channel).register(selector, SelectionKey.OP_ACCEPT);
                            System.out.println("服务器启动成功");
                        }
                        if (channel instanceof SocketChannel) {
                            SocketChannel socketChannel = (SocketChannel) channel;
                            socketChannel.configureBlocking(false);
                            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2048);
                            socketChannel.register(selector, SelectionKey.OP_READ, byteBuffer);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }


}

