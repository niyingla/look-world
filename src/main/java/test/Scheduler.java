package test;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {

    /**
     * 是否lock free 判断是否当前线程故障，会影响释放。（或者保证每次都有线程在进步）
     * 例子 自旋cas 就是lock-free
     * cas 写入状态 然后执行 再cas 写回去 就是有锁
     *
     * obstruction free
     * 线程隔离，线程不一定进步，最终可以进步
     * 举例 嵌套cas 第一个cas只更新版本号 检查是否有执行权 第二个真是cas 修改值和版本号
     *
     * wait free
     * 保证所有线程同时进步
     * 举例 copyOnWrite的读线程
     *
     * 放置一个数据的队列 元素必须消费下一个才能加入 (match操作)
     * （原理上这两个队列 两边插入不同状态的元素进行匹配 才能remove）
     * 支持dualQueue （双FIFO(先进先出)排队）LinkedTransferQueue
     * 支持dualQueue dualStack 双FIFO(先进先出)排队） SynchronousQueue cas实现竞争
     */
    SynchronousQueue<Runnable> tasks = new SynchronousQueue<>(false);
    //区分方法 来消费 transfer方法 和SynchronousQueue类似
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
