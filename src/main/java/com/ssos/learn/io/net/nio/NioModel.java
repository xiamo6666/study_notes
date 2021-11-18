package com.ssos.learn.io.net.nio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName: NioModel
 * @Description: nio模型
 * @Author: xwl
 * @Date: 2021/4/23 12:40
 * @Vsersion: 1.0
 */

public class NioModel {


    /**
     * nio 之同步非堵塞 多路复用
     */
    @Test
    public void testNioSelector() throws Exception {
        final ServerSocketChannel serverSocketChannel =
                ServerSocketChannel.open().bind(new InetSocketAddress(9000));
        serverSocketChannel.configureBlocking(false);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<Selector> selector = new AtomicReference<>();
        new Thread(() -> {
            try {
                selector.set(Selector.open());
                System.out.println(Thread.currentThread().getName());
                countDownLatch.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        countDownLatch.await();
        serverSocketChannel.register(selector.get(), SelectionKey.OP_ACCEPT);
        System.out.println("1234");

        while (true) {
            System.out.println(selector.get().select());
            Iterator<SelectionKey> iterator = selector.get().selectedKeys().iterator();
            while (iterator.hasNext()) {
                final SelectionKey next = iterator.next();
                iterator.remove();
                if (next.isAcceptable()) {
                    System.out.println("accept event");
                    ServerSocketChannel channel = (ServerSocketChannel) next.channel();
                    final SocketChannel accept = channel.accept();
                    accept.configureBlocking(false);
                    final ByteBuffer allocate = ByteBuffer.allocate(1024);
                    accept.register(selector.get(), SelectionKey.OP_READ, allocate);
                    System.out.println(accept.getRemoteAddress());
                }
                if (next.isReadable()) {
                    System.out.println("read event");
                    SocketChannel channel = (SocketChannel) next.channel();
                    next.cancel();
//                    channel.register(selector, SelectionKey.OP_READ);
//                    ByteBuffer attachment = (ByteBuffer) next.attachment();
//                    SocketChannel channel = (SocketChannel) next.channel();
//                    attachment.clear();
//                    channel.read(attachment);

//                    System.out.println(new String(attachment.array()));
//                    new Thread(() -> {
//                        System.out.println("read thread");
//                    }).start();
                }
            }
        }

    }


    /**
     * nio 之同步非堵塞
     */
    @Test
    public void testNio() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel
                .open()
                .bind(new InetSocketAddress(9000));
        serverSocketChannel.configureBlocking(false);
        Set<SocketChannel> set = new HashSet<>();
        for (; ; ) {
            SocketChannel accept = serverSocketChannel.accept();
            if (accept != null) {
                accept.configureBlocking(false);
                set.add(accept); //添加到连接集合
                System.out.println("有新的连接");
            }
            set.forEach(socketChannel -> {
                try {
                    ByteBuffer allocate = ByteBuffer.allocate(200);
                    int read = socketChannel.read(allocate);
                    if (read == 0) {
                        return;
                    }
                    if (read == -1) {
                        System.out.println("断开连接");
                        socketChannel.close();
                        set.remove(socketChannel);
                    }
                    if (read > 0) {
                        System.out.println(new String(allocate.array()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


        }
    }

    @Test
    public void testBuffer() {
        System.out.println(0 % 19);
    }

}
