package name.lx.rxjava2;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created in 2018/5/16 17:20
 *
 */
public class TestParallel {
	public static void main(String[] args) {
		ExecutorService obPool = Executors.newFixedThreadPool(3, new ThreadFactoryBuilder().setNameFormat("obPool-%d").build());
		ExecutorService subPool = Executors.newFixedThreadPool(3, new ThreadFactoryBuilder().setNameFormat("subPool-%d").build());
		Observable.fromArray(1, 2, 3, 4, 5).flatMap(s -> Observable.just(s).subscribeOn(Schedulers.from(obPool))).subscribeOn(Schedulers.from(obPool)).observeOn(Schedulers.from(subPool)).subscribe(s ->{
			System.out.println(String.format("%s in [%s]", s, Thread.currentThread().getName()));
			Random random = new Random();
			netWorkCost(random.nextInt(5000));
		});
	}

	private static void netWorkCost(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
}
