package test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class WaitAwait {
    /**
     *
     * @param argv
     * @throws InterruptedException
     */
    public static void main(String[] argv) throws InterruptedException {

        var lock = new ReentrantLock();
        Condition waitCond = lock.newCondition();
        Object obj = new Object();


        var t1 = new Thread(() -> {
            System.out.println("before-wait...1");
            try {
                lock.lock();
                //此处注释第一种方式等待
//                synchronized (obj) {
//                    obj.wait();
//                }
                waitCond.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
            System.out.println("after-wait...1");
        });

        var t2 = new Thread(() -> {
            System.out.println("before-wait...2");
            try {
                lock.lock();
//                synchronized (obj) {
//                    obj.wait();
//                }
                waitCond.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
            System.out.println("after-wait...2");
        });


        t1.start();
        t2.start();


        Thread.sleep(3);
        lock.lock();
        waitCond.signalAll();
        lock.unlock();
//        synchronized(obj){
//            obj.notifyAll();
//        }

    }
}
