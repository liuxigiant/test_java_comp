package name.lx.rxjava1;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created in 2018/5/22 10:29
 *
 */
public class TestConsumer {

	public static void main(String[] args) throws Exception{
		ExecutorService obPool = Executors.newFixedThreadPool(3, new ThreadFactoryBuilder().setNameFormat("obPool-%d").build());
		ExecutorService subPool = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setNameFormat("subPool-%d").build());
//		Observable.just(1, 2, 3, 4, 5)
//				.flatMap(s -> Observable.just(s).subscribeOn(Schedulers.from(obPool)).flatMap(ss -> {
//					Random random = new Random();
//					netWorkCost(random.nextInt(1000 * ss));
//					List<String> list = new ArrayList<>();
//					for (int i = 0; i < 5000000 * ss; i++){
//						list.add(ss + "-" + i);
//					}
//					System.out.println(ss + "---" + Thread.currentThread().getName());
//					return Observable.just(list.toArray(new String[list.size()]));
//
//				}))
		Observable.create(sub -> {

		})
				.observeOn(Schedulers.from(subPool))
				.subscribe(sb ->{
					System.out.println(String.format("%s in [%s]", sb, Thread.currentThread().getName()));
					Random random = new Random();
					netWorkCost(random.nextInt(5000));
					System.out.println(String.format("%s out [%s]", sb, Thread.currentThread().getName()));
				});
		Thread.sleep(1000 * 60);
		subPool.shutdown();
		obPool.shutdown();
	}

	private static void netWorkCost(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
}
