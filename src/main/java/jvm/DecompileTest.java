package jvm;

/**
 * 反编译前 类信息
 */
public class DecompileTest {
    public static String s = "@Hello";
    public String add(int a, int b){
        int d = (a + b) * 100;
        return d + s;
    }

    public static void main(String[] argv) {
    }
}
