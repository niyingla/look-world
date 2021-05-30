package coding.sorting;

/**
 * 冒泡排序
 */
public class BubbleSort implements IMutableSorter {

    @Override
    public void sort(int[] A) {
        for(int i = A.length - 1; i >=0; i--) {
            // 找到0-i间的最大元素放到A[i]
            bubble(A, 0, i+1);
        }
    }

    /**
     * 比较发小 交换位置
     * @param A
     * @param i
     * @param j
     */
    private void bubble(int[] A, int i, int j) {
        for(int k = 0; k < j - 1; k++) {
            if(A[k] > A[k+1]) {
                Helper.swap(A, k, k+1);
            }
        }
    }


}
