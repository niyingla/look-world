package test;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {

    //放置一个数据的队列 元素必须消费下一个才能加入
    SynchronousQueue<Runnable> tasks = new SynchronousQueue<>(false);
    //区分方法 来消费
    //LinkedTransferQueue<Runnable> tasks = new LinkedTransferQueue<>();

    static AtomicInteger idCount = new AtomicInteger(0);

    public Scheduler(int workers) {
        //循环创建目前个数 线程
        for(int i = 0; i < workers; i++) {
            //创建线程 并开始执行Worker.run
            new Thread(new Worker()).start();
        }
    }

    class Worker implements Runnable {

        int id;
        public Worker(){
            this.id = idCount.getAndIncrement();
        }

        @Override
        public void run() {
            //循环执行队列中任务
            while(true) {
                Runnable runnable = null;
                try {
                    //队列中 取任务执行
                    runnable = tasks.take();
                    runnable.run();
                    System.out.format("work done by id=%d\n", id);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 提交任务方法
     * @param r
     * @throws InterruptedException
     */
    public void submit(Runnable r) throws InterruptedException {
        //版本1
        //tasks.offer(r)
        // DualQueue
        // LinkedBlockingQueue
        // transfer方法表示生产者传递数据给消费者进行消费
//        while(!tasks.tryTransfer(r)) {
//            Thread.onSpinWait();
//            new Thread(new Worker()).start();
//        }

        //版本2
        if(!tasks.offer(r)){
            //等待少量cpu周期
            Thread.onSpinWait();
            //创建新的线程
            new Thread(new Worker()).start();
        }
    }

    public static void main(String[] argv) throws InterruptedException {
        //创建线程池
        var scheduler = new Scheduler(10);

        //循环1000个任务
        for(int i = 0; i < 1000; i++) {
            var localI = i;
            Thread.sleep(1);
            //提交任务到线程池
            scheduler.submit(() -> {
                try{
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

    }

}
