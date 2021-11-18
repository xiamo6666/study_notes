package com.ssos.learn.io.net.nio;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

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
    public void testNettyConnect() throws Exception {
        final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(1);
        final NioSocketChannel nioSocketChannel = new NioSocketChannel();
        nioEventLoopGroup.register(nioSocketChannel);

        final ChannelFuture connect =
                nioSocketChannel.connect(new InetSocketAddress("127.0.0.1", 9000));
        nioSocketChannel.writeAndFlush(Unpooled.copiedBuffer("wocao".getBytes())).sync();
        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = (ByteBuf) msg;
                final CharSequence charSequence = byteBuf
                        .getCharSequence(0,
                                byteBuf.readableBytes(),
                                CharsetUtil.UTF_8
                        );
                System.out.println(charSequence);
            }
        });
        connect.channel().closeFuture().sync();
    }

    @Test
    public void testNettyConnectBootStrap() throws Exception {
        final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(1);
        ChannelFuture channelFuture = new Bootstrap()
                .group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf byteBuf = (ByteBuf) msg;
                        System.out.println((byteBuf.getCharSequence(0, byteBuf.readableBytes(), CharsetUtil.UTF_8)));
                    }
                })
                .connect(new InetSocketAddress("127.0.0.1",
                        9000)
                );
        channelFuture.sync().channel().closeFuture().sync();
    }

    @Test
    public void testNettyServer() throws Exception {
        final NioServerSocketChannel nioServerSocketChannel = new NioServerSocketChannel();
        final NioEventLoopGroup eventExecutors = new NioEventLoopGroup(1);
        eventExecutors.register(nioServerSocketChannel);
        final ChannelFuture channelFuture = nioServerSocketChannel.bind(new InetSocketAddress(9000));
        channelFuture.channel().pipeline().addLast(new ChannelInboundHandlerAdapter() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


                NioSocketChannel client = (NioSocketChannel) msg;
                client.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                        System.out.println(msg);
                    }
//                    @Override
//                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                        ByteBuf byteBuf = (ByteBuf) msg;
//                        System.out.println(byteBuf.getCharSequence(0, byteBuf.readableBytes(), CharsetUtil.UTF_8));
//                    }
                });
                eventExecutors.register(client);

                System.out.println(msg.getClass());
            }
        });
        channelFuture.channel().closeFuture().sync();
    }
}
