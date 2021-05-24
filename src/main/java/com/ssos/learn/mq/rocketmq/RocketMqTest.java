package com.ssos.learn.mq.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: RocketMqTest
 * @Description: 基本使用demo
 * @Author: xwl
 * @Date: 2021/5/21 10:51
 * @Vsersion: 1.0
 */

public class RocketMqTest {


    /**
     *
     */
    @Test
    public void producer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("DefaultCluster");
        // Specify name server addresses.
        producer.setNamesrvAddr("10.10.11.20:9876");
        //Launch the instance.
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(10);

        int messageCount = 100;
        final CountDownLatch countDownLatch = new CountDownLatch(messageCount);
        for (int i = 0; i < messageCount; i++) {
            try {
                Message msg = new Message("TopicTest",
                        "*",
                        "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
                producer.send(msg);

            } catch (Exception e) {
                e.printStackTrace();

            }
            countDownLatch.await();
            producer.shutdown();
        }
    }

        /**
         * mq 消息接收
         *
         * @throws Exception
         */
        @Test
        public void consume() throws Exception{
            CountDownLatch countDownLatch = new CountDownLatch(1);
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("DefaultCluster");

            // Specify name server addresses.
            consumer.setNamesrvAddr("10.10.11.20:9876");
            // Subscribe one more more topics to consume.
            consumer.subscribe("TopicTest", "*");
            consumer.registerMessageListener((MessageListenerConcurrently) (megs, context) -> {
                megs.forEach(p -> System.out.println(new String(p.getBody())));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

            });
            //Launch the consumer instance.
            consumer.start();
            countDownLatch.await();
            System.out.printf("Consumer Started.%n");
        }

}
