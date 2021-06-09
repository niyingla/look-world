package concurrent;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeStack<T> {
    /**
     * 公平锁 等待队列按序进行
     * 非公平锁 加入时尝试获取锁 （更高效）
     * 并行 多线程完全同时执行
     * 并发 多线程交替执行
     * @param <T>
     */

    static class Node<T> {

        Node<T> next;
        T value;

        public Node(T value){
            this.value = value;
            this.next = null;
        }
    }


    /**
     * 头元素必须线程安全对象
     * 因为要从头部cas写入添加和删除对象
     */
    AtomicStampedReference<Node<T>> head;
    public LockFreeStack(){
        //初始化头部对象
        var headNode = new Node<T>(null);
        //初始化头部线程安全
        head = new AtomicStampedReference<>(headNode, 0);
    }

    /**
     * 放入新的头部节点
     * @param v
     */
    public void push(T v){
        var newNode = new Node<T>(v);
        //一直尝试写入
        while(true) {
            int stamp = head.getStamp();
            Node<T> ref = head.getReference();
            //之前头部对象设置为当前添加的下一个
            newNode.next = ref;
            //尝试cas写入 替换头部对象为当前并让版本号加一
            if(head.compareAndSet(ref, newNode, stamp, stamp+1)) {
                return;
            }
        }

    }

    /**
     * 删除头部节点
     * @return
     */
    public T pop(){
        while (true) {
            int stamp = head.getStamp();
            //获取头节点元素
            Node<T> ref = head.getReference();
            //没有下一个元素 空数组（默认就有头节点 所以不判断head）
            if(ref.next == null) {
                return null;
            }
            //这是头节点下一个元素（要头节点删除）
            var next = ref.next;
            //把头节换成next
            head.compareAndSet(ref, next, stamp, stamp+1);
            return ref.value;
        }
    }




    @Test
    public void testSingle(){

        var stack = new LockFreeStack<Integer>();

        for(int i = 0; i < 100; i++) {
            stack.push(i);
        }

        Integer j = null;
        Integer i = 99;
        while((j = stack.pop()) != null) {
            assertEquals(j+"", i-- +"");
        }
    }

    @Test
    public void testMultiThreads() throws InterruptedException {
        var stack = new LockFreeStack<Integer>();

        for(int i = 0; i < 16; i++) {
            var t = new Thread(() -> {

                for(int j = 0; j < 100; j++ ){
                    stack.push(j);
                }
//
            });
            t.start();
            t.join();
        }
        Integer c = 0;
        while(stack.pop() != null) {
            c ++;
        }
        assertEquals(c+"", "1600");
    }

}
