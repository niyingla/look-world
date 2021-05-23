package coding.collection;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class HashSetvsTreeSet  {

    @Test
    public void test_order(){
        var hashSet = new HashSet<Integer>();
        hashSet.add(3);
        hashSet.add(7);
        hashSet.add(2);
        hashSet.add(81);


        System.out.println(hashSet.stream().map(x -> x.toString()).collect(Collectors.joining(",")));


        var treeSet = new TreeSet<Integer>(){
            {
                add(3);
                add(7);
                add(2);
                add(81);
            }
        };



        System.out.println(treeSet.stream().map(x -> x.toString()).collect(Collectors.joining(",")));
    }


    @Test
    public void test_benchmark(){

        Random random = new Random();
        LinkedList<String> words = new LinkedList<>();
        for(int i = 0; i < 1000000; i++) {

            String word = random.ints(97, 123)
                    .limit(12)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            words.add(word);
        }


        HashSet<String> hashSet = new HashSet<String>();
        TreeSet<String> treeSet = new TreeSet<String>();

        Long start = System.currentTimeMillis();
        for(String w : words) {
            hashSet.add(w);
        }
        for(String w : words) {
            hashSet.contains(w);
        }
        System.out.println("hashSet time:" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for(var w : words) {
            treeSet.add(w);
        }
        for(var w : words) {
            treeSet.contains(w);
        }
        System.out.println("treeSet time:" + (System.currentTimeMillis() - start));
    }


}
