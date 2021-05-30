package coding.sorting;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class MergeSort implements IMutableSorter {
    AtomicInteger i = new AtomicInteger();
    @Override
    public void sort(int[] A) {
        mergeSort(A, 0, A.length);
    }

    private void mergeSort(int[] A, int l, int r) {

        // stack overflow
        // 是否还可以拆分
        if(r - l <= 1) {
            return;
        }

        //区中值 小数 向下取整
        int mid = (l+r+1)/2;
        //递归 差分任务
        mergeSort(A, l, mid);
        mergeSort(A, mid, r);

        merge(A, l, mid, r);

    }

    /**
     * 递归最底层的2个元素合并 然后 4 -》8 最后合并所有 所以每一次合并都有意义
     * @param A
     * @param l
     * @param mid
     * @param r
     */

    private void merge(int[] A, int l, int mid, int r) {
        //复制出两条等长数组
        int[] B = Arrays.copyOfRange(A, l, mid+1);
        int[] C = Arrays.copyOfRange(A, mid, r+1);
        System.out.println(B.length);
        //末尾放置极值
        B[B.length-1] = C[C.length - 1] = Integer.MAX_VALUE;

        int i = 0, j = 0;

        for(int k = l; k < r; k++) {
            //合并数组
            if(B[i] < C[j]) {
                A[k] = B[i++];
            } else {
                A[k] = C[j++];
            }
        }

    }

}
