package com.imooc.spring.reactive.loader;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * 并行数据加载器
 *
 * @author 小马哥
 * @since 2018/6/20
 */
public class ParallelDataLoaderV2 extends DataLoaderV2 {

    protected void doLoad() {  // 并行计算

        long result1 = 0, result2 = 0, result3 = 0;

        ExecutorService executorService = Executors.newFixedThreadPool(3); // 创建线程池
        CompletionService completionService = new ExecutorCompletionService(executorService);
        completionService.submit(super::loadConfigurations, null);      //  耗时 >= 1s
        completionService.submit(super::loadUsers, null);               //  耗时 >= 2s
        completionService.submit(super::loadOrders, null);              //  耗时 >= 3s

//        int count = 0;
//        while (count < 3) { // 等待三个任务完成
//            if (completionService.poll() != null) {
//                count++;
//            }
//        }

        executorService.shutdown();

        //https://cloud.tencent.com/developer/article/1444259

        for (int i = 0; i < 3; i++) {
            try {
                System.out.println("线程:"+completionService.take().get()+" 任务执行结束:");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


    }  // 总耗时 max(1s, 2s, 3s)  >= 3s

    public static void main(String[] args) {
        new ParallelDataLoaderV2().load();
    }

}
