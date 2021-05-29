package coding.proxy;

import basic.monad.Try;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.stream.Collectors;

public interface Aspect {
    void before();
    void after();

    static <T> T getProxy(Class<T> cls, String ... aspects) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //切面类 数组
        var aspectInsts = Arrays.stream(aspects)
                //循环切面类对象
                .map(name -> Try.ofFailable(() -> {

            var clazz = Class.forName(name);
            //循环切 实例化
            return (Aspect)clazz.getConstructor().newInstance();
        }))
                .filter(aspect -> aspect.isSuccess())
                .collect(Collectors.toList());

        //实例化代理类
        var inst = cls.getConstructor().newInstance();
        //创建切面代理对象
        return (T) Proxy.newProxyInstance(
                cls.getClassLoader(),
                cls.getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        for(var aspect : aspectInsts) {
                            aspect.get().before();
                        }
                        var result = method.invoke(inst);
                        for(var aspect : aspectInsts) {
                            aspect.get().after();
                        }
                        return result;
                    }
                }
        );
    }
}
