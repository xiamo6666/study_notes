package com.ssos.learn.io.nio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashSet;
import java.util.Set;

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
    public void testNioSelector() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open().bind(new InetSocketAddress(9000));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        for (; ; ) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            selectionKeys.forEach(p -> {
                if (p.isAcceptable()) {
                    try {
                        ServerSocketChannel socketChannel = ((ServerSocketChannel) p.channel());
                        SocketChannel accept = socketChannel.accept();
                        if (accept != null) {
                            accept.configureBlocking(false);
                            accept.register(selector, SelectionKey.OP_READ);
                            System.out.println("客户端连接成功");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    /**
     * nio 之同步非堵塞
     */
    @Test
    public void testNio() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open().bind(new InetSocketAddress(9000));
        serverSocketChannel.configureBlocking(false);
        Set<SocketChannel> set = new HashSet<>();
        byte[] bytes = new byte[200];
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

}
