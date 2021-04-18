package com.ssos.learn.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @ClassName: ForkJoinPoolTest
 * @Description: 测试ForkJoinPool
 * @Author: xwl
 * @Date: 2021/4/18 10:53
 * @Vsersion: 1.0
 */

public class ForkJoinPoolTest {
    private static volatile boolean flag = true;

    static class TestForkJoin extends RecursiveTask<Integer> {
        private int start;
        private int end;
        private final int interval = 5;

        public TestForkJoin(int start, int end) {
            this.start = start;
            this.end = end;
        }


        @Override
        protected Integer compute() {
            if ((end - start) > 5) {
                TestForkJoin testForkJoin = new TestForkJoin(start, ((end + start) / 2));
                TestForkJoin testForkJoin1 = new TestForkJoin(((end + start) / 2) + 1, end);
                testForkJoin.fork();
                testForkJoin1.fork();
                return testForkJoin.join() + testForkJoin1.join();
            } else {
                int sum = 0;
                for (int i = start; i <= end; i++) {
                    sum = sum + i;
                }
                return sum;
            }
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Integer> submit = forkJoinPool.submit(new TestForkJoin(1, 100));
        System.out.println(submit.get());
    }
}
