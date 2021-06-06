package test;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

public class ExchangerTest1 {
    /**
     * 一般用于线程之前数据交换
     */
        static class Producer extends Thread {
            private Exchanger<Integer> exchanger;
            private static int data = 0;
            Producer(String name, Exchanger<Integer> exchanger) {
                super("Producer-" + name);
                this.exchanger = exchanger;
            }

            @Override
            public void run() {
                for (int i=1; i<5; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        data = i;
                        System.out.println(getName()+" 交换前:" + data);
                        //等待另一个线程到达此交换点（除非当前线程被中断），然后将给定的对象传送给该线程，并接收该线程的对象
                        data = exchanger.exchange(data);
                        System.out.println(getName()+" 交换后:" + data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        static class Consumer extends Thread {
            private Exchanger<Integer> exchanger;
            private static int data = 0;
            Consumer(String name, Exchanger<Integer> exchanger) {
                super("Consumer-" + name);
                this.exchanger = exchanger;
            }

            @Override
            public void run() {
                while (true) {
                    data = 0;
                    System.out.println(getName()+" 交换前:" + data);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        data = exchanger.exchange(data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(getName()+" 交换后:" + data);
                }
            }
        }

        public static void main(String[] args) throws InterruptedException {
            Exchanger<Integer> exchanger = new Exchanger<Integer>();
            new Producer("", exchanger).start();
            new Consumer("", exchanger).start();
            TimeUnit.SECONDS.sleep(7);
            System.exit(-1);
        }
}
