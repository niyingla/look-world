package datastructure;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;


public class List<T> {
    static class Node<T> {
        Node<T> next = null;
        T data;
        public Node(T data){
            this.data = data;
        }
    }
    Node<T> head = null;

    /**
     * 头部插入下一个元素
     * @param data
     */
    // O(1) 插入难度 头部插入永远 1
    public void insert(T data) {
        var node = new Node<>(data);
        node.next = head;
        head = node;

    }

    /**
     * 根据条件内容标胶
     * @param predicate
     * @return
     */
    // O(n) 算最大元素 所以难度是 n
    public Node<T> find(Predicate<T> predicate) {
        var p = head;
        while(p != null) {
            //比较值
            if(predicate.test(p.data)) {
                return p;
            }
            //往下遍历
            p = p.next;
        }
        return null;

    }

    /**
     * 循环数据 计数 返回大小
     * @return
     */
    public Integer size(){
        var p = head;
        Integer c = 0;
        while(p != null) { p = p.next; c++; }
        return c;
    }

    /**
     * 删除元素
     * @param node
     */
    // O(n)
    public void remove(Node<T> node){
        if(head == null) {
            return;
        }

        if(head == node) {
            head = head.next;
            return;
        }

        //前一个元素
        var slow = head;
        //下一个元素
        var fast = head.next;

        //下一个不等于删除的 并且下一个不等于空 就继续循环
        while(fast != node && fast != null) {
            //替换往下走
            slow = fast;
            fast = fast.next;
        }
        //fast 存在表示招到了
        if(fast != null) {
            //替换 fast（下一个元素） 前一个指针指向后一个 当前元素放空（fast）
            slow.next = fast.next;
//            fast.data = null;
        }

    }


    /**
     * 换向
     */
    public void reverse(){
        // prev | current | next
        Node<T> prev = null;
        var current = head;
        Node<T> next;
        //从首个元素开始 往下执行
        while(current != null) {
            //存下下一个数素
            next = current.next;
            //前一个元素指向下一个
            current.next = prev;
            //------元素往下走一个--------
            //当前元素放到前一个
            prev = current;
            //替换下一个到当前
            current = next;
        }
        //放置首个元素 prev 就是反转后数组的最后一个元素（最后一个current）
        head = prev;

    }

    private Node<T> _reverse2(Node<T> head) {
        //不等于null 继续换
        if(head == null || head.next == null) {
            return head;
        }

        // 直到最后一个才开始停止递归    执行往下走换序 执行结果就是 从最后一个开始执行递归 指针翻转
        var rest = _reverse2(head.next);
        //前后换序 head的下一个的下一个换成当前（指针翻转） head.next 为current 然后前后换
        head.next.next = head;
        //断开 pre和head的指向 本来的 下一个的链接 （断开之前 head的下一个已经传入递归）
        head.next = null;
        return rest;
    }

    public void reverse2() {
        head = _reverse2(head);
    }

    /**
     * 是否存在环
     * @return
     */
    public boolean hasLoop1(){
        var set = new HashSet<>();

        var p = head;
        while(p != null) {
            //集合中已经存在 表示有环
            if(set.contains(p)) {
                return true;
            }
            //加入集合
            set.add(p);
            p = p.next;
        }
        return false;
    }

    /**
     * 是否有环
     * @return
     */
    public boolean hasLoop2(){
        if(head == null || head.next == null) {
            return false;
        }
        //慢指针每次跑一个
        var slow = head;
        //快指针 相差两个指针每次跑两个
        var fast = head.next.next;
        while(fast != null && fast.next != null) {
            //追上了 就返回 有环
            if(fast == slow) {return true;}
            slow = slow.next;
            fast = fast.next.next;
        }
        return false;
    }


    @Test
    public void test_insert(){
        var list = new List<Integer>();
        list.insert(1);
        list.insert(2);
        list.insert(3);

        var p = list.head;
        for(Integer i = 3; p != null ; i--) {
            //比较当前数据和目标
            assertEquals(i, p.data);
            //换下一个
            p = p.next;
        }
    }

    @Test
    public void test_find_remove(){
        var list = new List<String>();

        list.insert("C++");
        list.insert("Java");
        list.insert("C");
        list.insert("C#");
        list.insert("python");

        var node = list.find(x -> x == "Java");
        assertEquals("Java", node.data);

        var node2 = list.find(x -> x == "ruby");
        assertEquals(null, node2);

        list.remove(node);
        assertEquals(Integer.valueOf(4), list.size());
        assertEquals(null, list.find(x -> x == "Java"));
    }

    @Test
    public void test_reverse() {
        var list = new List<Integer>();

        list.insert(1);
        list.insert(2);
        list.insert(3);
        list.reverse();

        var p = list.head;
        for (Integer i = 1; p != null; i++) {
            assertEquals(i, p.data);
            p = p.next;
        }
    }

    /**
     * 递归
     */
    @Test
    public void test_reverse2() {
        var list = new List<Integer>();

        list.insert(1);
        list.insert(2);
        list.insert(3);
        list.insert(4);
        list.insert(5);
        list.reverse2();

        var p = list.head;
        for (Integer i = 1; p != null; i++) {
            assertEquals(i, p.data);
            p = p.next;
        }
    }

    @Test
    public void test_loop(){
        var list = new List<Integer>();
        list.insert(3);
        list.insert(2);
        list.insert(1);
        list.insert(0);
        var node = list.find(x -> x == 3);
        node.next = list.head;

        assertEquals(true, list.hasLoop1());
        assertEquals(true, list.hasLoop2());

    }

}
