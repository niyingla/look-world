package test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {
    CyclicBarrier barrier;
    //计数次数
    int page = 0;

    public CyclicBarrierTest(){
        //创建栅栏 达到目标后执行的方法
        barrier = new CyclicBarrier(2, () -> {
            System.out.println("sync...");
            page ++;
        });
    }

    /**
     * 准备产品
     */
    void prepareProducts() {
        while(page < 1000) {
            try {
                System.out.println("fetch product...");
                int x = barrier.await();
                if(x == 2) {

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 准备订单
     */
    void prepareDeliveryOrders()  {
        while (page < 1000) {
            try {
                System.out.println("fetch delivery order...");
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

    }

    void run() {
        new Thread(this::prepareProducts).start();
        new Thread(this::prepareDeliveryOrders).start();
    }

    public static void main(String[] argv) {
        var test = new CyclicBarrierTest();
        test.run();
    }
}
