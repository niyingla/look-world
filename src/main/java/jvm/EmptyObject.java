package jvm;


import org.junit.Test;

import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class EmptyObject {

    // 注入Instrumentation流程（jdk本身的对象 不可以new）
    // 1 把InstrumentationAgent 编译成class 文件
    // 2 打成jar 放入.MF 指定注入的对象
    // 3 运行本方法 需要指定 InstrumentationAgent jar地址 --javaagent:/home/*/InstrumentationAgent.jar
    //-XX:-UseCompressedClassPointers
    //-XX:-UseCompressedOops
    public static void main(String[] argv){
        //16
        System.out.println(InstrumentationAgent.getObjectSize(new Object()));
        //16
        System.out.println(InstrumentationAgent.getObjectSize(new int[0]));
        //24
        System.out.println(InstrumentationAgent.getObjectSize(""));
        //16
        Integer y = 1;
        System.out.println(InstrumentationAgent.getObjectSize(y));
        //16
        int x = 1;
        System.out.println(InstrumentationAgent.getObjectSize(x));
        //空对象 至少16字节 一定是8的倍数 会有对齐位
    }

}


