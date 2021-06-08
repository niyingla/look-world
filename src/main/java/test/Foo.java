package test;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Foo {
    static class DbConnection {
        DbConnection() {
        }
    }


    /**
     * 强制一致 （之前其他代码 也不会重排）
     */
    static volatile DbConnection ref;

    /**
     * happens before (前面带来的变化在后面可以观察到) 约束 java 9之后
     * 保证在他之前的指令在他之前完成就行
     * 保证在他之后的指令在他之后完成就行
     * ref1.getAcquire() 获取内容 判断为空
     * ref1.set() 设置内容
     *
     */
    static AtomicReference<DbConnection> ref1 = new AtomicReference();

    public static DbConnection getDb() {
        // weak atomics : acquire, release
        var localRef = ref;
        if(localRef == null) {
            synchronized (Foo.class) {
                System.out.println("Synchronized block pass thread:" + Thread.currentThread().getId());
                localRef = ref;
                if (localRef == null) {
                    localRef = ref = new DbConnection();
                }
            }
        }
        return localRef;
    }



    public static void main(String[] argv) {
        for(var i = 0; i < 200; i++) {
            new Thread(() -> {
                getDb();
            }).start();
        }
    }


}
