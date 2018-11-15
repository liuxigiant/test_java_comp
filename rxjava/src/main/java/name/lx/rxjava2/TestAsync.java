package name.lx.rxjava2;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.springframework.scheduling.annotation.Schedules;

import java.util.ArrayList;
import java.util.List;

/**
 * Created in 2018/5/10 14:39
 *
 */
public class TestAsync {

	public static void main(String[] args) throws Exception{
		List<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		//(String[])list.toArray()
		Observable<String> observable = Observable.fromArray(list.toArray(new String[list.size()]));
		observable.map(s -> {
			System.out.println("sb -> " + Thread.currentThread().getName());
				return Integer.parseInt(s);
		}).subscribeOn(Schedulers.io())
//				.observeOn()
				.subscribe(integer -> {
					System.out.println("ob -> " + Thread.currentThread().getName());
			System.out.println("--->" + integer);
		});

		System.out.println("main -> " + Thread.currentThread().getName());

		System.out.println("----------------------");


		System.in.read();
	}
}
