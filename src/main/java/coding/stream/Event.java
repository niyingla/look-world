package coding.stream;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Event<T>{

    T data;
    public Event(T data){
        this.data = data;
    }

    static class EventData {
        Integer id;
        String msg;
        public EventData(Integer id, String msg) {
            this.id = id;
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "EventData{" +
                    "id=" + id +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }

    static class Transforms {
        static EventData transform(Integer id) {
            switch(id) {
                case 0:
                    return new EventData(id, "Start");
                case 1:
                    return new EventData(id, "Running");
                case 2:
                    return new EventData(id, "Done");
                case 3:
                    return new EventData(id, "Fail");
                default:
                    return new EventData(id, "Error");
            }
        }
    }

    /**
     * 提供一个函数式接口 使之可以在lambda内被匿名引用
     * @param <A>
     * @param <B>
     */
    @FunctionalInterface
    interface FN<A, B> {
        B apply(A a);
    }


    /**
     * 定义替换方法 内容是 调用前面的函数式接口的实际载体 （FN 接口）(Transforms.transform 实际载体)
     * @param f
     * @param <B>
     * @return
     */
    <B> Event<?> map(FN<T, B> f) {
        return new Event<>(f.apply(this.data));
    }

    public static void main(String[] args) {
        Stream<Event<Integer>> s = Stream.of(
                new Event<>(1),
                new Event<>(2),
                new Event<>(0),
                new Event<>(10)
        );

        s.map(event -> event.map(Transforms::transform))
                .forEach(e ->
                    System.out.println(e.data)
                );

    }
}