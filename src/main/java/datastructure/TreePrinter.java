package datastructure;


import java.util.ArrayList;

public class TreePrinter {

    /**
     * 计算树高度（从最底层的0开始每层加一）
     * @param node
     * @param <T>
     * @return
     */
    <T> int heightOf(BSTree.BSTNode<T> node) {
        if(node == null) {
            return 0;
        }
        //递归调用 高的继续 +1 计算
        return Math.max(
                heightOf(node.left),
                heightOf(node.right)
        ) + 1;
    }


    public <T> void print(BSTree.BSTNode<T> root) {
        //找出结构高度
        int h = heightOf(root);
        //曾展示宽度
        int W = (int) Math.pow(2, h + 1);
        //每行数据
        var lines = new StringBuilder[h * 2];
        for(int i = 0; i < h*2; i++) {
            //创建打印数据行
            lines[i] = new StringBuilder(String.format("%" + W + "s", ""));
        }
        //答应节点数据
        printNode(lines, W, root, 0, 0);
        for(var line : lines) {
            System.out.println(line.toString());
        }

    }

    /**
     * 打印当前节点及下属节点数据
     * @param lines 打印数据行
     * @param W 当前层次数 最大容纳元素数 * 2
     * @param node 节点
     * @param h 行
     * @param base 基础位置
     * @param <T>
     */
    private <T> void printNode(StringBuilder[] lines, int W,  BSTree.BSTNode<T> node, int h, int base) {
        //当前行数量
        var nums = Math.pow(2, h);
        //位置 = 总字数/数量*2
        var pos = base + (int) (W / (nums * 2));
        //获取数据字符串
        var str = node.data.toString();
        //循环插入数据
        for (int i = 0; i < str.length(); i++) {
            //插入数据
            lines[h * 2].setCharAt(pos + i, str.charAt(i));
        }
        //打印左指向
        if (node.left != null) {
            lines[h * 2 + 1].setCharAt(pos - 1, '/');
            //打印当前节点及下属节点数据
            printNode(lines, W, node.left, h + 1, base);
        }
        //打印左指向
        if (node.right != null) {
            lines[h * 2 + 1].setCharAt(pos + str.length() + 1, '\\');
            //打印当前节点及下属节点数据
            printNode(lines, W, node.right, h + 1, pos);
        }
    }
}
