package com.atdu.netty.BasicC2;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
@Slf4j
public class TestJdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("执行任务");
                Thread.sleep(1000);
                return 50;
            }
        });
        log.info("等待结果");
        log.info("结果是 {}",future.get());

    }
}
