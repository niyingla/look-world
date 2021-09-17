package datastructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Expr {

    static Queue<String> prefixTransform(String expr) {
        //数字在前 运算符号在后
        var queue = new Queue<String>();
        var highPriorityStack = new Stack<String>();

        //通过运算符号切割
        var prts = expr.replaceAll("[+-/*]+", "#$0#").split("#");

        for(var prt : prts) {
            //是数字 进入队列
            if(prt.matches("\\d+")) {
                queue.enqueue(prt);
            }
            else if(prt.equals("+") || prt.equals("-")) {
                //运算符 队列中存在元素
                while(highPriorityStack.size() > 0) {
                    //从前取出元素进行
                    var op = highPriorityStack.pop();
                    if(op.equals("*") || op.equals("/")) {
                        //当前元素加入到末尾
                        queue.enqueue(op);
                    } else {
                        //直到当前元素不是优先计算符 就插入
                        highPriorityStack.push(op);
                        break;
                    }
                }
                highPriorityStack.push(prt);
            } else if(prt.equals("*") || prt.equals("/")) {
                //运算符 放到这个栈
                highPriorityStack.push(prt);
            }
        }
        while(highPriorityStack.size() > 0) {
            queue.enqueue(highPriorityStack.pop());
        }
        System.out.println(queue.toString());
        return queue;
    }

    /**
     * 转换表达式
     * @param expr
     * @return
     */
    public static Integer eval(String expr) {
        //转换符号 拿到数据队列
        var queue = prefixTransform(expr);
        //当前运行数据栈
        var stack = new Stack<Integer>();
        while(queue.size() > 0) {
            //去一个元素(最后取运算元素)
            var item = queue.dequeue();
            //判断是符号还是数字
            if (item.matches("\\+|-|\\*|/")) {
                //从前取数字 运算
                switch (item) {
                    case "+":
                        stack.push(stack.pop() + stack.pop());
                        break;
                    case "-":
                        stack.push(stack.pop() - stack.pop());
                        break;
                    case "*":
                        stack.push(stack.pop() * stack.pop());
                        break;
                    case "/":
                        stack.push(stack.pop() / stack.pop());
                        break;
                }

            } else {
                //转成数子 放到数组
                stack.push(Integer.parseInt(item));
            }
        }

        return stack.pop();
    }

    public static void main(String[] argv) throws IOException {

        var scanner = new Scanner(System.in);
        while(true) {
            System.out.print("enter>");
            var expr = scanner.nextLine();
            var value = eval(expr);
            System.out.println(value);
        }
    }

}
