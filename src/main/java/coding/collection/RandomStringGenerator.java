package coding.collection;

import java.util.*;

public class RandomStringGenerator<T> implements Iterable<T> {

    private final List<T> list;


    private void swap(int[] a, int i, int i1) {
    }

    public RandomStringGenerator(List<T> list) {
        this.list = list;


    }

    /**&
     * 实现迭代方法
     * @return
     */
    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {
            /**
             * 是否包含下一个值
             * @return
             */
            @Override
            public boolean hasNext() {
                return true;
            }

            /**
             * 返回下一个值
             * @return
             */
            @Override
            public T next() {
                return list.get((int) (list.size() * Math.random()));
            }
        };
    }


    public static void main(String[] argv) {
        var list = Arrays.asList("List", "Tree", "Array");
        var gen = new RandomStringGenerator<String>(list);

        for(var s: gen) {
            System.out.println(s);
        }

//        var it = gen.iterator();
//        for(int i = 0; i < 100; i++) {
//            System.out.println(it.next());
//        }




    }

}
