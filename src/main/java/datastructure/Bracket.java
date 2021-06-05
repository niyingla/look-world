package datastructure;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Bracket {


    /**
     * 判断string 中  { 和 } 形成的对数 数量是否匹配
     * @param str
     * @return
     */

    public boolean isMatch(String str){
        var stack = new Stack<Character>();

        for(char c : str.toCharArray()) {
            //遇到左括号 放入栈
            if(c == '{') {
                stack.push(c);
                continue;
            }

            //遇到右括号 弹栈 比较
            if(c == '}') {
                if(stack.size() == 0) {
                    return false;
                }
                var item = stack.pop();
                if(item != '{') {
                    return false;
                }
            }
        }
        return stack.size() == 0;

    }

    @Test
    public void test(){
        var cases = new String[]{
               "{1}{2}{3}",
                "{{1+2}{{3+4}}}",
                "1*{2+3*{4+5}}",
                "",
                "{",
                "}}{{",
                "{{{}",
        };

        var values = new Boolean[] {
                true,
                true,
                true,
                true,
                false,
                false,
                false
        };

        for(int i = 0; i < cases.length; i++) {
            var c = cases[i];
            assertEquals(values[i], isMatch(c));
        }




    }
}
