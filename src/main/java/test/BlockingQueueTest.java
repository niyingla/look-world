package test;

import java.util.concurrent.*;

public class BlockingQueueTest {


    public static void main(String[] argv) {
        //阻塞队列三组接口

        //抛异常 add / remove
        //非阻塞 offer / poll
        //阻塞 puut / take

        BlockingQueue<Integer> queue;
        //queue = new ArrayBlockingQueue<Integer>(10);
        //queue = new LinkedBlockingQueue<Integer>();
        //双向队列 提供first、last元素操作方法
        //queue = new LinkedBlockingDeque<>();
        //内部是有序的树型堆（有点像二叉树） 头小 尾大 增加数据 从头开始挨个比较下沉 删除数据 从尾开始挨个比较上升
        //queue = new PriorityBlockingQueue<>();
        //
        //queue = new LinkedTransferQueue<Integer>();
        queue = new SynchronousQueue<>();
        //内部 PriorityQueue 每次取数据 更新最近的一条过期时间
        //queue = new DelayQueue<>();

        // Producer
        for(int i = 0; i < 100; i++) {
            new Thread(() -> {
                try {
                    queue.put((int) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // Consumer
        for(int i = 0; i < 10; i++) {
            new Thread(() -> {

                while(true) {
                    Integer x = null;
                    try {
                        x = queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Receive:" + x);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }).start();
        }

    }
}
