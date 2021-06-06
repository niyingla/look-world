package test;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
public class ProducerCustomerModel {
    final static int MAX = 10;
    //生产队列
    LinkedList<Integer> queue = new LinkedList<>();

    ReentrantLock lock = new ReentrantLock();
    //生产约束
    Condition full = lock.newCondition();
    //消费约束
    Condition emtpy = lock.newCondition();


    /**
     * 生产
     * @return
     * @throws InterruptedException
     */
    int readData() throws InterruptedException {
        Thread.sleep((long)Math.random()*1000);
        return (int)Math.floor(Math.random());
    }


    /**
     * 生产者方法
     * @throws InterruptedException
     */
    // Producer
    public void readDb() throws InterruptedException {
        lock.lock();
        //队列中数据是否满了
        if (queue.size() == MAX) {
            //生产等待
            full.await();
            return;
        }
        //生产数据
        var data = readData(); // 1s
        if(queue.size() == 1) {
            //有数据就直接激活消费等待队列 开启生产
            emtpy.signalAll();
        }
        //加入队列
        queue.add(data);
        lock.unlock();
    }

    /**
     * 消费 防止并发计算size等 所以加锁
     * @throws InterruptedException
     */
    // Comsumer
    public void calculate() throws InterruptedException {

        lock.lock();
        //没数据 消费队列等待
        if (queue.size() == 0) {
            emtpy.await();
            return;
        }
        //队列取一个
        var data = queue.remove();
        System.out.println("queue-size:" + queue.size());
        //小于最大队列数 唤醒生产
        if(queue.size() == MAX - 1) {
            full.signalAll();
        }

        data *= 100;
        lock.unlock();
    }


    public static void main(String[] argv) {
        var p = new ProducerCustomerModel();
        //100 个线程去生产
        for(int i = 0; i < 100; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        p.readDb();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        //一个线程消费
        new Thread(() -> {
            while(true) {
                try {
                    p.calculate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
