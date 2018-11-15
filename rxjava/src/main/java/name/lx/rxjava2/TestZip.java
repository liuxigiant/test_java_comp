package name.lx.rxjava2;

import io.reactivex.Observable;

/**
 * Created in 2018/4/17 17:03
 *
 */
public class TestZip {
	public static void main(String[] args) {
		Observable<String> stringObservable1
				= Observable.create(observableEmitter -> {
								System.out.println("stringObservable1 prepare A");
								observableEmitter.onNext("A");

								System.out.println("stringObservable1 prepare B");
								observableEmitter.onNext("B");

								System.out.println("stringObservable1 prepare C");
								observableEmitter.onNext("C");

							});

		Observable<String> stringObservable2
				= Observable.create(observableEmitter -> {
								System.out.println("stringObservable2 prepare 1");
								observableEmitter.onNext("1");

								System.out.println("stringObservable2 prepare 2");
								observableEmitter.onNext("2");

								System.out.println("stringObservable2 prepare 3");
								observableEmitter.onNext("3");

								System.out.println("stringObservable2 prepare 4");
								observableEmitter.onNext("4");
							});

		//将连个事件做匹配，类似与sql中的left join，事件数量少的放左边
		Observable<String> zipObservable
				= Observable.zip(stringObservable1, stringObservable2, (s1, s2) -> s1 + s2);


		zipObservable.subscribe(s -> System.out.println("receive " + s));

	}
}
