package com.ssos.learn.concurrent.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: TestDisruptor
 * @Description:
 * @Author: xwl
 * @Date: 2021/4/18 11:57
 * @Vsersion: 1.0
 */

public class TestDisruptor {
    public static void main(String[] args) {

        fun2();
    }

    /**
     * 普通方式
     */

    static public void fun1() {
        LongEventFactory longEventFactory = new LongEventFactory();
        Disruptor<LongEventFactory.LongEvent> disruptor = new Disruptor<>(longEventFactory, 8,
                Executors.defaultThreadFactory());
        disruptor.handleEventsWith(new LongEventHandler());
        disruptor.start();
        RingBuffer<LongEventFactory.LongEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent((longEvent, age, l) -> longEvent.set(age));
    }

    /**
     * lambda 表达式的方式
     */
    static public void fun2() {
        Disruptor<LongEventFactory.LongEvent> disruptor = new Disruptor<>(LongEventFactory.LongEvent::new,
                8, Executors.defaultThreadFactory());
        disruptor.handleEventsWith((longEvent, var2, var3) -> {
            System.out.println(longEvent.value);
            System.out.println(Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(10);
        });
        disruptor.start();
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                disruptor.publishEvent((longEvent, l, age) -> {
                    longEvent.set(age);
                }, 100);
            }).start();
        }

    }

}
