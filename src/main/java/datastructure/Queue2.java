package datastructure;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Queue2<T> {

    /**
     * 两个栈 实现队列
     */
    //进队栈
    Stack<T> s1 = new Stack<>();
    //出队栈
    Stack<T> s2 = new Stack<>();

    public void enqueue(T e) {
        s1.push(e);
    }

    public T dequeue() {
        //出队列有数据 返回出队列的
        if(s2.size() > 0) {
            return s2.pop();
        }

        //没有就从进队列放一次 （会造成顺序倒过来）取顶部 放到底部
        while(s1.size() > 0) {
            s2.push(s1.pop());
        }
        return s2.pop();
    }

    @Test
    public void test(){
        var queue = new Queue2<>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.enqueue(4);

        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
        assertEquals(4, queue.dequeue());

    }
}
