package coding.sorting;

public class SelectionSort implements IMutableSorter {

    @Override
    public void sort(int[] A) {
        //从后往前找最大的数据放到最后
        for(int i = A.length - 1; i >= 0; i--) {
            // 0 - A[i]
            int j = maxIndex(A, 0, i+1);
            //交换
            Helper.swap(A, i, j);
        }
    }

    /**
     * 返回i-（j-1）内最大值所在元素的序号
     * @param A
     * @param i
     * @param j
     * @return
     */
    static private int maxIndex(int[] A, int i, int j) {
        int max = Integer.MIN_VALUE;
        int maxIndex = j-1;
        for(int k = j-1; k >= i; k--) {
            if(max < A[k]) {
                max = A[k];
                maxIndex = k;
            }
        }
        return maxIndex;
    }

}
