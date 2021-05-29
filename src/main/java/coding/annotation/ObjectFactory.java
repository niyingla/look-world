package coding.annotation;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;

public class ObjectFactory {

    public static <T> T newInstance(Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Annotation[] annotations = clazz.getAnnotations();
        LinkedList<IAspect> aspects = new LinkedList<IAspect>();

        for(var annotation : annotations) {
            if(annotation instanceof  Aspect) {
                Class type = ((Aspect) annotation).type();
                IAspect aspect = (IAspect)(type.getConstructor().newInstance());
                aspects.push(aspect);
            }
        }

        //JDK动态代理 基于接口 创建实现类
        T inst = clazz.getConstructor().newInstance();
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                clazz.getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        aspects.forEach(aspect -> aspect.before());
                        Object result = method.invoke(inst);
                        aspects.forEach(aspect -> aspect.after());
                        return result;
                    }
                }
        );
    }

    @Test
    public void test() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InterruptedException {
        //这里只能用接口 接
        IOrder order = ObjectFactory.newInstance(Order.class);
        order.pay();
        order.show();
    }

}
