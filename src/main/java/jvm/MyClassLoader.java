package jvm;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 手写类加载器
 */
public class MyClassLoader {

    /**
     * 生成class 字节码
     * @return
     */
    private static byte[] genClass(){
        //创建池子 用于保存运行时数据
        ClassPool pool = ClassPool.getDefault();
        //是否已经存在
        CtClass aClass = pool.getOrNull("greetings.Go");
        //已经存在并且冻结了
        if (aClass != null && aClass.isFrozen()) {
            //解冻
            aClass.defrost();
        }
        //产生一个类
        CtClass ctClass = pool.makeClass("greetings.Go");
        //为类产生方法 返回值类型 方法名 参数类型（这里没有 就是空数组）  类对象
        CtMethod method = new CtMethod(CtClass.voidType, "greetings", new CtClass[]{}, ctClass);
        //设置为public 方法
        method.setModifiers(Modifier.PUBLIC);
        try {
            //设置方法内容
            method.setBody("{ System.out.println(\"Hi, greetings!\"); }");
            //添加到类
            ctClass.addMethod(method);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        try {
            //转成二进制
            return ctClass.toBytecode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 二进制类加载器
     */
    class BinLoader extends ClassLoader {
        /**
         * 重写加载类方法
         * @param name
         * @return
         * @throws ClassNotFoundException
         */
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name == "greetings.Go") {
                //生成自己的二进制
                var bytes = genClass();
                //调用父类方法 根据二进制文件 定义类 字节开始和结束位
                return defineClass("greetings.Go", bytes, 0, bytes.length);
            }
            //其他类 默认继续双亲委派
            return super.findClass(name);
        }
    }

    /**
     * 远程加载类
     */
    class NetLoader extends ClassLoader {
        byte[] bytes;
        public NetLoader() throws IOException {
            //发起远程链接
            connect();
        }
        private void connect() throws IOException {
            try(var socket = new Socket("localhost", 8000)){
                //读取内容
                bytes = socket.getInputStream().readAllBytes();
            }
        }

        /**
         * 获取加载的class
         * @param name
         * @return
         * @throws ClassNotFoundException
         */
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name == "greetings.Go") {
                return defineClass("greetings.Go", bytes, 0, bytes.length);
            }
            return super.findClass(name);
        }
    }


    /**
     * 远程加载类服务端 用于返回客户端 加载类的请求的byte[]
     * @throws IOException
     */
    @Test
    public void server() throws IOException {
        var serverSocket = new ServerSocket(8000);
        //调用原来的生成方法
        var bytes = genClass();
        while(true) {
            //等待远程链接
            try(var clientSocket = serverSocket.accept()) {
                System.out.println("receve request...");
                //输出数据
                var out = clientSocket.getOutputStream();
                out.write(bytes);
                out.flush();
            }
        }
    }


    /**
     * 测试自定义类加载器生成对象
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    @Test
    public void test_gen() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //创建自定义类加载器
        BinLoader myloader = new BinLoader();
        //加载我们指定的内容字节 换成的Class
        Class goclazz= myloader.loadClass("greetings.Go");
        //实例化
        Object go = goclazz.getConstructor().newInstance();
        //反射调用方法
        go.getClass().getMethod("greetings").invoke(go);
    }

    /**
     * 测试远程加载
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IOException
     */
    @Test
    public void test_net() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        //创建远程加载类
        NetLoader myloader = new NetLoader();
        //调用远程加载方法
        Class goclazz= myloader.loadClass("greetings.Go");
        //实例化
        Object go = goclazz.getConstructor().newInstance();
        //加载类
        go.getClass().getMethod("greetings").invoke(go);
    }


}
