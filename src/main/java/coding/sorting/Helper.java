package coding.sorting;

public class Helper {
    /**
     * 数据换位
     * @param A
     * @param i
     * @param j
     */
    static void swap(int[] A, int i, int j){
        int temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }

}
