package com.imooc.spring.reactive.loader;

import java.util.concurrent.*;

/**
 * 并行数据加载器
 *
 * @author 小马哥
 * @since 2018/6/20
 */
public class ParallelDataLoaderV2Poll extends DataLoaderV2 {

    protected void doLoad() {  // 并行计算

        ExecutorService executorService = Executors.newFixedThreadPool(3); // 创建线程池
        CompletionService completionService = new ExecutorCompletionService(executorService);

        completionService.submit(new Callable() {
            @Override
            public Object call() throws Exception {
                return loadConfigurations();
            }
        });

        completionService.submit(new Callable() {
            @Override
            public Object call() throws Exception {
                return loadUsers();
            }
        });

        completionService.submit(new Callable() {
            @Override
            public Object call() throws Exception {
                return loadOrders();
            }
        });


        int count = 0;
        while (count < 3) { // 等待三个任务完成
            Future future = completionService.poll();
            if (future != null) {
                try {
                    System.out.println("线程:"+future.get()+" 任务执行结束:");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                count++;
            }
        }

        executorService.shutdown();

        //https://cloud.tencent.com/developer/article/1444259


    }  // 总耗时 max(1s, 2s, 3s)  >= 3s

    public static void main(String[] args) {
        new ParallelDataLoaderV2Poll().load();
    }

}
