package coding.annotation;


import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Aspect(types = TimeUsageAspect.class)
public class Order implements IOrder {

    int state = 0;
    @Override
    public void pay() throws InterruptedException {
        Thread.sleep(50);
        this.state = 1;
    }

    @Override
    public void show() {
        System.out.println("order status:" + this.state);
    }


    @Test
    public void test_proxy() throws InterruptedException {

        var order = new Order();
        var proxy = (IOrder)Proxy.newProxyInstance(
                Order.class.getClassLoader(),
                new Class[]{IOrder.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("before invoke method:" + method);
                        return method.invoke(order);
                    }
                }
        );
        proxy.pay();

    }

}
