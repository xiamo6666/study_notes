package com.ssos.learn.io.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import org.junit.jupiter.api.Test;

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
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new HttpServerCodec(),
                                new HttpObjectAggregator(65535),
                                new WebSocketServerProtocolHandler("/"),
                                new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println("2");
                                        System.out.println(msg);
                                        ctx.channel().attr(AttributeKey.valueOf("123")).set("");
                                        ctx.fireChannelRead(msg);
                                    }
                                },

                                new SimpleChannelInboundHandler<Object>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
                                        System.out.println("1");
                                        channelHandlerContext.fireChannelRead("456");
                                    }

                                    @Override
                                    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                                        super.handlerRemoved(ctx);
                                    }
                                });

                    }
                });

        try {
            serverBootstrap.bind(9000).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testM() {
        System.out.println(100 % 5);
 
    }
}
