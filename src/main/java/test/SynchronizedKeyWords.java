package test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedKeyWords {

    public static void main(String[] argv) {
        var lock = new ReentrantLock();

        var t = new Thread(() -> {
            try {
                lock.tryLock(1000, TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("finished.");
            }
        });
        t.start();
        //interrupt（）方法 其作用是中断此线程（此线程不一定是当前线程，
        // 而是指调用该方法的Thread实例所代表的线程），但实际上只是给线程设置一个中断标志，线程仍会继续运行
        //interrupted（）方法 作用是测试当前线程是否被中断（检查中断标志），
        // 返回一个boolean并清除中断状态，第二次再调用时中断状态已经被清除，将返回一个false。
        //isInterrupted（）方法 作用是只测试此线程是否被中断 ，不清除中断状态。
        //调用打断方法 不会停止程序 一般用isInterrupted() 判断终止递归或者循环
        t.interrupt();


    }
}
