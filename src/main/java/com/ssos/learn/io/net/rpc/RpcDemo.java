package com.ssos.learn.io.net.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;

/**
 * @ClassName: RpcDemo
 * @Description: dto
 * @Author: xwl
 * @Date: 2021/11/18 14:25
 * @Vsersion: 1.0
 */

public class RpcDemo {


    public static void main(String[] args) {

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ByteBuf byteBuf = Unpooled.copiedBuffer("wocao".getBytes());

//        Class[] classes = {RpcInterface.class};
//        final RpcInterface object = (RpcInterface) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), classes, new InvocationHandler() {
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                System.out.println(this.getClass());
//                System.out.println("invoke methodTeest");
//                return "test";
//            }
//        });
//        System.out.println(object.methodTest());

    }
}
