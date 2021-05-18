package com.ssos.learn.io.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName: NettyDemo
 * @Description: dto
 * @Author: xwl
 * @Date: 2021/4/26 10:32
 * @Vsersion: 1.0
 */

public class NettyDemo {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(2), new NioEventLoopGroup(8))
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(ctx.pipeline());
                                ByteBuf byteBuf = (ByteBuf) msg;
//                                System.out.println(((InetSocketAddress) ctx.channel().localAddress()).getPort());
                                String message = byteBuf.toString(StandardCharsets.UTF_8);
                                System.out.println(message);
                            }
                        });
                    }
                });

        try {
            serverBootstrap.bind(9000).sync();
//            System.out.println("wocao");
//            serverBootstrap.bind(9001).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testM() {
        System.out.println(100 % 5);
    }
}
