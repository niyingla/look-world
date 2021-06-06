package test;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class DelayQueueTest {

    static class DelayedItem<T> implements Delayed {

        //数据
        T value;
        //生效时间
        long time = 0;

        public DelayedItem(T v, long delay){
            this.value = v;
            this.time = delay + System.currentTimeMillis();
        }

        /**
         * 获取生效时间 0 开始生效
         * @param unit
         * @return
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return time - System.currentTimeMillis();
        }

        /**
         * 比较那个先执行
         * @param o
         * @return
         */
        @Override
        public int compareTo(Delayed o) {
            return (int) (this.time - ((DelayedItem)o).time);
        }

        @Override
        public String toString() {
            return "DelayedInteger{" +
                    "value=" + value +
                    ", time=" + time+
                    '}';
        }
    }

    static DelayQueue<DelayedItem<Integer>> queue = new DelayQueue<>();

    public static void main(String[] argv) {


        new Thread(() -> {
            for(int i = 0; i < 1000; i++){
                queue.offer(new DelayedItem<Integer>(i, i*1000));
            }
        }).start();

        new Thread(() -> {
            while(true) {
                try {
                    var item = queue.take();
                    System.out.println(item.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
