package datastructure;

import java.util.Arrays;

public class EQueen {

    //皇后数量
    int[] queens = new int[8];

    public boolean solve(int col) {

        //到达第九列 直接return 0-7
        if(col == 8) {
            return true;
        }

        // i : row 每列尝试放入皇后
        for(int i = 0; i < 8; i++) {
            //本次是否可以放置
            boolean flag = true;

            //设置当前位置（行） 为当前列 皇后
            queens[col] = i;

            //判断之前是否满足 j 当前列
            for(int j = 0; j < col; j++) {
                //行差 （queens[j] 为历史j列的 皇后行数）
                var rowDiff = Math.abs(queens[j] - i);
                //列差
                var colDiff = col - j;
                //不是同行并且不是斜对角
                if(queens[j] == i || rowDiff == colDiff) {
                    flag = false;
                    break;
                }
            }
            // flag = false 不尝试了 为true 继续尝试下一列
            if(flag && solve(col + 1)) {
                return true;
            }

        }
        return false;
    }

    void print(){

        for(int i = 0; i < 8; i++) {

            for(int j = 0; j < 8; j++) {
                if(queens[i] == j) {
                    System.out.print(" Q ");
                } else {
                    System.out.print(" . ");
                }

            }
            System.out.print("\n");
        }
    }

    public static void main(String[] argv) {
        var equeen = new EQueen();
        equeen.solve(0);
        equeen.print();

    }

}
