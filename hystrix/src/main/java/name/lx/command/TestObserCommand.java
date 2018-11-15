package name.lx.command;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created in 2018/4/5 17:34
 *
 */
public class TestObserCommand extends HystrixObservableCommand<String> {

	private final String name;

	public TestObserCommand(String name) {
		super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
		this.name = name;
	}

	@Override
	protected Observable<String> construct() {
		return Observable.create(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> observer) {
				try {
					if (!observer.isUnsubscribed()) {
						// a real example would do work like a network call here
						observer.onNext("Hello");
						observer.onNext(name + "!");
						observer.onCompleted();
					}
				} catch (Exception e) {
					observer.onError(e);
				}
			}
		} ).subscribeOn(Schedulers.io());
	}


	public static void main(String[] args) {
		TestObserCommand command = new TestObserCommand("LX");
		command.observe().subscribe(s -> {
			System.out.println(s);
		});
	}
}
