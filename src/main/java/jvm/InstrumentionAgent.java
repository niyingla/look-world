package jvm;

import java.lang.instrument.Instrumentation;

class InstrumentationAgent {
    /**
     * java 本身操作对象不能创建 只能注入
     */
    private static volatile Instrumentation globalInstrumentation;

    /**
     * 系统内置流程 会在main执行之前注入 目标对象
     *
     * Javaagent是java命令的一个参数。参数 javaagent 可以用于指定一个 jar 包，并且对该 java 包有2个要求：
     *
     * 这个 jar 包的 MANIFEST.MF 文件必须指定 Premain-Class 项。
     * Premain-Class 指定的那个类必须实现 premain() 方法。
     *
     * @param agentArgs
     * @param inst
     */
    public static void premain(final String agentArgs, final Instrumentation inst)  {
        globalInstrumentation = inst;
    }

    /**
     * 获取类 大小
     * @param object
     * @return
     */
    public static long getObjectSize(final Object object) {
        if (globalInstrumentation == null) {
            throw new IllegalStateException("Agent not initialized.");
        }
        return globalInstrumentation.getObjectSize(object);
    }
}