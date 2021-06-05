package datastructure;


import java.util.ArrayList;

public class TreePrinter {

    <T> int heightOf(BSTree.BSTNode<T> node) {
        if(node == null) {
            return 0;
        }
        return Math.max(
                heightOf(node.left),
                heightOf(node.right)
        ) + 1;
    }


    public <T> void print(BSTree.BSTNode<T> root) {
        //找出结构高度
        int h = heightOf(root);
        //2的高度次幂
        int W = 2 * (int) Math.pow(2, h);
        //每行数据
        var lines = new StringBuilder[h*2];
        for(int i = 0; i < h*2; i++) {
            lines[i] = new StringBuilder(String.format("%" + W + "s", ""));
        }

        printNode(lines, W, root, 0, 0);
        for(var line : lines) {
            System.out.println(line.toString());
        }

    }

    /**
     * 答应
     * @param lines 打印数据行
     * @param W 行数 * 2
     * @param node 节点
     * @param h 行
     * @param base 基础位置
     * @param <T>
     */
    private <T> void printNode(StringBuilder[] lines, int W,  BSTree.BSTNode<T> node, int h, int base) {
        //数量
        var nums = Math.pow(2, h);
        //位置 总数/数量*2
        var pos = base + (int)(W / (nums *  2));
        //获取数据字符串
        var str = node.data.toString();
        //循环插入数据
        for(int i = 0; i < str.length(); i++) {
            //插入数据
            lines[h * 2].setCharAt(pos + i, str.charAt(i));
        }
        //打印左指向
        if(node.left != null) {
            lines[h*2+1].setCharAt(pos-1, '/');
            printNode(lines, W, node.left, h+1, base);
        }
        //打印左指向
        if(node.right != null) {
            lines[h*2 + 1].setCharAt(pos + str.length() + 1, '\\');
            printNode(lines, W, node.right, h+1, pos);
        }
    }
}
