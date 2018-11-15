package name.lx.rxjava2;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

/**
 * Created in 2018/5/22 10:29
 *
 */
public class TestConsumer {

	public static void main(String[] args) throws Exception{
//		ExecutorService obPool = Executors.newFixedThreadPool(3, new ThreadFactoryBuilder().setNameFormat("obPool-%d").build());
//		ExecutorService subPool = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setNameFormat("subPool-%d").build());
//		Observable.fromArray(1, 2, 3, 4, 5)
//				.flatMap(s -> Observable.just(s).subscribeOn(Schedulers.from(obPool)).flatMap(ss -> {
//					Random random = new Random();
//					netWorkCost(random.nextInt(100));
//					List<String> list = new ArrayList<>();
//					for (int i = 0; i < 5000000 * ss; i++){
//						list.add(ss + "-" + i);
//					}
//					System.out.println(ss + "---" + Thread.currentThread().getName());
//					return Observable.fromArray(list.toArray(new String[list.size()]));
//
//				}))
//				.observeOn(Schedulers.from(subPool))
//				.subscribe(s ->{
//					System.out.println(String.format("%s in [%s]", s, Thread.currentThread().getName()));
//					Random random = new Random();
//					netWorkCost(random.nextInt(5000));
//					System.out.println(String.format("%s out [%s]", s, Thread.currentThread().getName()));
//				}, error -> {
//					System.out.println("error->" + error.getMessage());
//				});
//		Thread.sleep(1000 * 60);
//		subPool.shutdown();
//		obPool.shutdown();

		Observable.interval(1, TimeUnit.MILLISECONDS)
				.observeOn(Schedulers.newThread())
				.subscribe(
						i -> {
							System.out.println(i);
							try {
								Thread.sleep(100);
							} catch (Exception e) { }
						},
						System.out::println);

		System.in.read();
	}

	private static void netWorkCost(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
}
