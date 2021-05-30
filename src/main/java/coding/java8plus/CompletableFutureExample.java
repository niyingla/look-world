package coding.java8plus;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureExample {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        CompletableFuture future = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            //Thread.sleep(1000);
            // future.complete("Hello");

            future.completeOnTimeout("Hello", 1000, TimeUnit.MILLISECONDS);
            return null;
        });
        // sync. method blocking...
        var value = future.get();
        System.out.println(value);

    }

    @Test
    public void test2() throws ExecutionException, InterruptedException {
        CompletableFuture successFuture = CompletableFuture.completedFuture("Hello");
        CompletableFuture failFuture = CompletableFuture.failedFuture(new InterruptedException());

        System.out.println(successFuture.get());

        if(failFuture.isCompletedExceptionally()) {
            System.out.println("error"); // print
        }
        System.out.println(failFuture.get()); // Exception
    }

    @Test
    public void ss(){
        int i = 1;
        CompletableFuture
                //异步执行
                .supplyAsync(() -> i + 7)
                //然后执行
                .thenApply(ui->ui*2)
                //然后执行
                .thenApply(ui->ui*3)
                //完成时执行 r 结果 e 一场
                .whenComplete((r,e)->System.out.println(r));
    }

}
