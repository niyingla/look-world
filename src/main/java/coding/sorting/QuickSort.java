package coding.sorting;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class QuickSort implements IImutableSorter{
    @Override
    public List<Integer> sort(List<Integer> A) {
        return this.quickSort(A);
    }

    private List<Integer> quickSort(List<Integer> A) {

        if(A.size() <= 1) {
            return A;
        }
        // |---left---| x | ---right ---||
        var x = A.get(0);
        //小于部分
        var left = A.stream().filter(a -> a < x)
                    .collect(toList());
        //等于部分
        var mid = A.stream().filter(a -> a.equals(x))
                .collect(toList());
        //大于部分
        var right = A.stream().filter(a -> a > x)
                .collect(toList());

        //继续拆分 得到两部分结果集汇总
        left = quickSort(left);
        right = quickSort(right);
        //合并
        left.addAll(mid);
        left.addAll(right);
        return left;
    }


}
