package com.ssos.learn.concurrent.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * @ClassName: LongEventHandler
 * @Description: 消息的处理程序
 * @Author: xwl
 * @Date: 2021/4/18 12:11
 * @Vsersion: 1.0
 */

public class LongEventHandler implements EventHandler<LongEventFactory.LongEvent> {

    public static int count;
    @Override
    public void onEvent(LongEventFactory.LongEvent longEvent, long l, boolean b) throws Exception {
        count++;
        System.out.println(Thread.currentThread().getName()+":event="+longEvent.value+":count="+count);
    }
}
