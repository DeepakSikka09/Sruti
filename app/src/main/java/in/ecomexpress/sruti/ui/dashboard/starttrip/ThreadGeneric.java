package in.ecomexpress.sruti.ui.dashboard.starttrip;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by 63091 on 19-07-2019.
 */

public class ThreadGeneric {
    public static <T> T runThread(T t) {//throws ExecutionException, InterruptedException
        try {
            Callable<T> callable = new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return t;
                }
            };
            ExecutorService es = Executors.newFixedThreadPool(3);
            Future<T> tt = es.submit(callable);
            es.shutdown();
            return tt.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   public static void  executeCall(Runnable callable){
       try {
         new Thread(callable).start();
       } catch (Exception e) {
           e.printStackTrace();
       }

   }
}
