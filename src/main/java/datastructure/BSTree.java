package datastructure;

import org.junit.Test;

/**
 * 红黑树的大小顺序
 * 右 中 左 依次递加
 * @param <T>
 */

public class BSTree<T extends Comparable<T>> {

    //根节点
    BSTNode<T> root = null;

    //树节点
    static class BSTNode<T> {
        //左侧元素
        BSTNode<T> left = null;
        //右侧元素
        BSTNode<T> right = null;
        //中间元素（数据本身）
        T data;

        public BSTNode(T data) {
            this.data = data;
        }
    }

    private void add(BSTNode<T> node, BSTNode<T> element) {

        //小于 在左侧
        if(element.data.compareTo(node.data) <= 0) {
            //不存在左下级
            if(node.left == null) {
                //直接指向
                node.left = element;
                return;
            }
            //继续递归往下添加
            add(node.left, element);
        } else {
            //大于在右侧

            //不存在右下级
            if(node.right == null) {
                node.right = element;
                return;
            }
            //继续递归往下添加
            add(node.right, element);
        }

    }

    //添加
    public void add(T element) {
        var node = new BSTNode<>(element);
        if(root == null) {
            root = node;
            return;
        }

        add(root, node);

    }

    /**
     * 根节点 位置 在哪就是什么序
     *
     * 先序 根节点->左子树->右子树
     * @param node
     * @param <T>
     */
    <T> void preOrder(BSTNode<T> node) {

        if(node == null) {
            return;
        }

        System.out.println(node.data);
        preOrder(node.left);
        preOrder(node.right);

    }

    /**
     * 后序 左子树->右子树->根节点
     * @param node
     * @param <T>
     */
    <T> void postOrder(BSTNode<T> node){
        if(node == null) {
            return;
        }

        postOrder(node.left);
        postOrder(node.right);
        System.out.println(node.data);

    }

    /**
     * 中序 左子树->根节点->右子树
     * 因为满足 左中右 小到大顺序 所以打印是 从小到大 满足二茬搜索树
     * @param node
     * @param <T>
     */
    <T> void inOrder(BSTNode<T> node){
        if(node == null) {
            return;
        }

        inOrder(node.left);
        System.out.println(node.data);
        inOrder(node.right);

    }

    /**
     * 广度先搜索 从上往下 先 平级再 下级
     * （深度优先 从上往下 先 下级再 平级）
     * @param node
     * @param <T>
     */
    // Breadth First Search
    public static <T> void bfs(BSTNode<T> node){
        var queue = new Queue<BSTNode<T>>();
        //进入队列
        queue.enqueue(node);
        //只要有子元素 就会往队列填充
        while(queue.size() > 0) {
            //出队
            var item = queue.dequeue();
            //打印出队元素
            System.out.println(item.data);
            //子元素入队
            if(item.left != null)
                queue.enqueue(item.left);
            if(item.right != null)
                queue.enqueue(item.right);
        }
    }

    /**
     * 左右翻转
     * @param node
     * @param <T>
     */
    public static <T> void reverse(BSTNode<T> node) {
        if(node == null) {
            return;
        }

        var t = node.left;
        node.left = node.right;
        node.right = t;

        reverse(node.left);
        reverse(node.right);

    }

    @Test
    public void test(){
        System.out.println("abcdefghijklmn".hashCode());
        var o = new Object();
        o.hashCode();
        var tree = new BSTree<Integer>();

        tree.add(10);
        tree.add(7);
        tree.add(5);
        tree.add(8);
        tree.add(15);
        tree.add(30);
        tree.add(21);

        var printer = new TreePrinter();
        printer.print(tree.root);


        bfs(tree.root);
        //preOrder(tree.root);
        //postOrder(tree.root);
//        inOrder(tree.root);
    }

    @Test
    public void test_reverse(){
        var tree = new BSTree<Integer>();

        tree.add(10);
        tree.add(7);
        tree.add(5);
        tree.add(8);
        tree.add(15);
        tree.add(30);
        tree.add(21);

        var printer = new TreePrinter();
        printer.print(tree.root);

        BSTree.reverse(tree.root);
        printer.print(tree.root);
    }

}

