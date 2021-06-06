package test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class PhaserTest {

    Phaser phaser = new Phaser();
    ExecutorService executorService = Executors.newCachedThreadPool();

    class Worker implements Runnable{
        @Override
        public void run(){
            // 4 --- sync --- 4 --- sync --..
            //注册一次
            phaser.register();

            while(true) {
                try {
                    Thread.sleep(500);
                    System.out.println("I'm working:@" + phaser.getPhase());
                    //合并方法到达并且等待其他进入
                    phaser.arriveAndAwaitAdvance();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     *                              phaser的方法解释
     * arrive 到达 在屏幕上等待其他合作方 到达线程 +1
     * awaitAdvance 等待进步 在屏幕上等待其他线程，数量够了就进入同步点
     * register 注册 声明自己是一个合作方 将parties +1
     * deregister 注销 注销自己 将parties -1
     * @throws InterruptedException
     */

    public void run() throws InterruptedException {
        // main thread 注册本身
        phaser.register();
        executorService.execute(new Worker());
        executorService.execute(new Worker());
        executorService.execute(new Worker());
        executorService.execute(new Worker());
        while(true) {
            //等待其他注册的线程到达
            phaser.arriveAndAwaitAdvance();
            //同步放行数
            System.out.println("Sync...." + phaser.getPhase());
        }
    }

    public static void main(String[] argv) throws InterruptedException {
        var test = new PhaserTest();
        test.run();
    }

}
