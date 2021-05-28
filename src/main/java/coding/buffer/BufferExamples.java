package coding.buffer;

import coding.collection.RandomStringGenerator;
import org.junit.Test;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BufferExamples {

    @Test
    public void gen() throws IOException {
        Random r = new Random();
        String fileName = "word";

        //指定缓冲区大小 （最好不调整）
        Integer bufferSize = 4*1024;
        //创建缓存输出流
        BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(fileName), bufferSize);

        Long start = System.currentTimeMillis();

        for(int i = 0; i < 1000000000; i++) {
            for (int j = 0; j < 5; j++) {
                fout.write(97 + r.nextInt(5));
            }
            fout.write(' ');
        }
        fout.close();
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void read_test() throws IOException {
        String fileName = "word";
        FileInputStream in = new FileInputStream(fileName);

        var start = System.currentTimeMillis();

        int b;
//        var sb = new StringBuilder();
        while((b = in.read()) != -1) {
        }

        var end = System.currentTimeMillis();
        System.out.println((end - start) + "ms");
        in.close();
    }


    @Test
    public void read_test_withBuffer() throws IOException {
        String fileName = "word";
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));

        Long start = System.currentTimeMillis();

        int b;
//        var sb = new StringBuilder();
        byte[] bytes = new byte[1024*8];
        //读取一个字节数组的数据
        while((b = in.read(bytes)) != -1) {
        }

        var end = System.currentTimeMillis();
        System.out.println((end - start) + "ms");
        in.close();
    }

    /**
     * 速度上和上面差不多
     * @throws IOException
     */
    @Test
    public void read_test_nio() throws IOException {
        // New I/O
        var fileName = "word";

        FileChannel channel = new FileInputStream(fileName).getChannel();
        //非配内存区
        ByteBuffer buff = ByteBuffer.allocate(1024*8);
        Long start = System.currentTimeMillis();

        while(channel.read(buff) != -1) {
            buff.flip();
            // 读取数据
            //System.out.println(new String(buff.array()));
            buff.clear();
        }

        System.out.format("%dms\n", System.currentTimeMillis() - start);


    }

    @Test
    public void test_chinese(){
        String raw = "长坂桥头杀气生，横枪立马眼圆睁。一声好似轰雷震，独退曹家百万兵。";
        Charset charset = StandardCharsets.UTF_8;
        //内存复制到bytes
        byte[] bytes = charset.encode(raw).array();
        //创建指定长度字节数组
        byte[] bytes2 = Arrays.copyOfRange(bytes, 0, 11);
        //创建指定长度ByteBuffer byte是字节 一个UTF-8文字可能包含多个字节
        ByteBuffer bbuf = ByteBuffer.allocate(12);
        //创建指定长度CharBuffer 字符数字
        CharBuffer cbuf = CharBuffer.allocate(12);
        //把内容放进 ByteBuffer
        bbuf.put(bytes2);
        //指针复位
        bbuf.flip();
        //解码到CharBuffer
        charset.newDecoder().decode(bbuf, cbuf, true);
        //指针复位
        cbuf.flip();
        char[] tmp = new char[cbuf.length()];
        if(cbuf.hasRemaining()){
            //取出数据到char[]
            cbuf.get(tmp);
            System.out.println("here:" + new String(tmp));
        }
        //打印未读取数据大小
        System.out.format("limit-pos=%d\n", bbuf.limit() - bbuf.position());
        //拷贝未读取数据 末尾 - 最大
        byte[] copyOfRange = Arrays.copyOfRange(bbuf.array(), bbuf.position(), bbuf.limit());
        //一般有三个指针 读取位 末尾位 最大位
        System.out.println(copyOfRange);

    }

    /**
     * 异步channel
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test_async_read() throws IOException, ExecutionException, InterruptedException {
        String fileName = "word";
        AsynchronousFileChannel channel =
                AsynchronousFileChannel.open(Path.of(fileName), StandardOpenOption.READ);

        ByteBuffer buf = ByteBuffer.allocate(1024*8);
        Future<Integer> operation = channel.read(buf, 0);
        Integer numReads = operation.get();
        buf.flip();
        String chars = new String(buf.array());
        buf.clear();
    }
}
