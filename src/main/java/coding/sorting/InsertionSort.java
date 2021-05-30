package coding.sorting;

public class InsertionSort implements IMutableSorter {
    @Override
    public void sort(int[] A){
        for(int i = 1; i < A.length; i++) {
            // 将A[i] 插入在卡片0，到卡片i之间
            // j代表抓到的牌，先放到最右侧，不断交换到对应的位置

            int c = A[i];
            int j = i;

            //换位
            for(; j > 0 && A[j-1] > c;j--) {
                A[j] = A[j-1];
            }
            A[j] = c;
        }
    }

    public static void main(String[] args) {
        InsertionSort insertionSort = new InsertionSort();

        int[] a = {222, 22, 324};

        insertionSort.sort(a);

        System.out.println(a);

    }

}
