package com.ssos.learn.mq.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
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
        CountDownLatch countDownLatch = new CountDownLatch(100);
        int messageCount = 100;
        for (int i = 0; i < messageCount; i++) {
            try {
                Message msg = new Message("TopicTest",
                        "*",
                        "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
                //同步消息发送
//                producer.send(msg);

                //异步消息发送
                producer.send(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.println(Thread.currentThread().getName() + sendResult.getSendStatus());
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                    }
                });

                //单向消息，无需反馈
//                producer.sendOneway(msg);

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        countDownLatch.await();

    }

    /**
     * mq 消息接收
     *
     * @throws Exception
     */
    @Test
    public void consume() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("DefaultCluster");

        // Specify name server addresses.
        consumer.setNamesrvAddr("10.10.11.20:9876");
        // Subscribe one more more topics to consume.
        consumer.subscribe("TopicTest", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (megs, context) -> {
            megs.forEach(p -> System.out.println(new String(p.getBody())));
            //默认情况下只会被一个consumer进行消费
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        //Launch the consumer instance.
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.start();
        countDownLatch.await();
        System.out.printf("Consumer Started.%n");

    }

    @Test
    public void consume1() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("DefaultCluster");

        // Specify name server addresses.
        consumer.setNamesrvAddr("10.10.11.20:9876");
        // Subscribe one more more topics to consume.
        consumer.subscribe("TopicTest", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (megs, context) -> {
            megs.forEach(p -> System.out.println(new String(p.getBody())));
            //默认情况下只会被一个consumer进行消费
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        //Launch the consumer instance.
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.start();
        countDownLatch.await();
        System.out.printf("Consumer Started.%n");

    }

}
