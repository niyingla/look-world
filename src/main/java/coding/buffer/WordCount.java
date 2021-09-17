package coding.buffer;

import org.junit.Test;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class WordCount {


    final ForkJoinPool pool = ForkJoinPool.commonPool();

    /**
     * 计算当前字符串 出现次数
     * @param str
     * @return
     */
    private static HashMap<String, Integer> countByString(String str){
        HashMap map = new HashMap<String, Integer>();
        //包装字符串 到 StringTokenizer
        StringTokenizer tokenizer = new StringTokenizer(str);
        //是否有下一个字符串
        while(tokenizer.hasMoreTokens()) {
            //获取下一个字符串
            String word = tokenizer.nextToken();
            //计算当前字符
            incKey(word, map, 1);
        }
        return map;
    }

    /**
     * key 计数 出现次数
     * @param key
     * @param map
     * @param n
     */
    private static void incKey(String key, HashMap<String, Integer> map, Integer n) {
        if(map.containsKey(key)){
            map.put(key, map.get(key) + n);
        } else {
            map.put(key, n);
        }
    }


    class CountTask implements Callable<HashMap<String, Integer>> {
        private final long start;
        private final long end;
        private final String fileName;

        public CountTask(String fileName, long start, long end) {
            this.start = start;
            this.end = end;
            this.fileName = fileName;

        }

        @Override
        public HashMap<String, Integer> call() throws Exception {
            // r	以只读的方式打开文本，也就意味着不能用write来操作文件
            // rw	读操作和写操作都是允许的
            // rws	每当进行写操作，同步的刷新到磁盘，刷新内容和元数据
            // rwd	每当进行写操作，同步的刷新到磁盘，刷新内容
            //创建一个文件岁间读取渠道对象
            FileChannel channel = new RandomAccessFile(this.fileName, "rw").getChannel();

            // [start, end] -> Memory
            // Device -> Kernel Space -> UserSpace(buffer) -> Thread
            //文件通道的可读可写要建立在文件流本身可读写的基础之上
            //把文件影射为内存映像文件
            MappedByteBuffer mbuf = channel.map(FileChannel.MapMode.READ_ONLY, this.start, this.end - this.start);
            //解码成并换成string
            String str = StandardCharsets.US_ASCII.decode(mbuf).toString();
            return countByString(str);
        }
    }


    public void run(String fileName, long chunkSize) throws ExecutionException, InterruptedException {
        File file = new File(fileName);
        long fileSize = file.length();

        long position = 0;

        long startTime = System.currentTimeMillis();
        //创建等待任务队列数组
        ArrayList<Future<HashMap<String, Integer>>> tasks = new ArrayList<Future<HashMap<String, Integer>>>();
        while(position < fileSize) {
            //获取当前次读取末尾位
            Long next = Math.min(position + chunkSize, fileSize);
            //读取区间数据
            CountTask task = new CountTask(fileName, position, next);
            //重置下一次位置 为当前末尾位
            position = next;
            //提交单次读取任务
            Future<HashMap<String, Integer>> future = pool.submit(task);
            //加入线程任务队列
            tasks.add(future);
        }
        System.out.format("split to %d tasks\n", tasks.size());

        HashMap totalMap = new HashMap<String, Integer>();
        //循环获取所有任务结果
        for(Future<HashMap<String, Integer>> future: tasks) {
            //获取结果等待
            HashMap<String, Integer> map = future.get();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                //叠加
                incKey(entry.getKey(), totalMap, entry.getValue());
            }
        }

        System.out.println("time:" + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("total:" + totalMap.size());

        System.out.println(totalMap.get("ababb"));
    }

    @Test
    public void count() throws ExecutionException, InterruptedException {
        var counter = new WordCount();
        System.out.println("processors:" + Runtime.getRuntime().availableProcessors());
        // 参数 文件名 单次检索量
        counter.run("word", 1024*1024*20);
    }

    @Test
    public void compare_with_single() throws IOException {
        // 缓冲读取流
        BufferedInputStream in = new BufferedInputStream(new FileInputStream("word"));
        // 读取字节数字
        byte[] buf = new byte[4*1024];
        Integer len = 0;
        // 字符串对应的次数
        HashMap total = new HashMap<String, Integer>();
        Long startTime = System.currentTimeMillis();
        // 读取一个字节数组
        while((len = in.read(buf)) != -1) {
            //复制结果到新数组
            byte[] bytes = Arrays.copyOfRange(buf, 0, len);
            String str = new String(bytes);
            //计数map
            HashMap<String, Integer> hashMap = countByString(str);
            //循环档当前计数结果
            for(Map.Entry<String, Integer> entry : hashMap.entrySet()) {
                String key = entry.getKey();
                //累加到之前的map
                incKey(key, total, entry.getValue());
            }
        }
        // 阿姆达定律
        // 120s -> 16core -> 120/16 = ? 
        //  P    NP

        System.out.println("time:" + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println(total.get("ababb"));
        System.out.println(total.size());
    }
}
