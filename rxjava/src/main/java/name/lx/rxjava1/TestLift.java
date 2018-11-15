package name.lx.rxjava1;

import rx.Observable;
import rx.Subscriber;

/**
 * Created in 2018/4/16 19:21
 *
 */
public class TestLift {
	public static void main(String[] args) {
		Observable.OnSubscribe<String> oldOnSubscribe =
							new Observable.OnSubscribe<String>() {
								@Override
								public void call(Subscriber<? super String> subscriber) {
									subscriber.onNext("1");
									subscriber.onNext("2");
								}
							};
		Observable<String> oldObservable = Observable.create(oldOnSubscribe);

		//lift 将事件进行转换  ---  integerObservable是创建的新 Observable
		//integerObservable中包含的 OnSubscribe为OnSubscribeLift（OnSubscribe的实现类）
		//OnSubscribeLift 中包含持有oldObservable中的oldOnSubscribe
		Observable<Integer> integerObservable =
				oldObservable
						.lift(new Observable.Operator<Integer, String>() {
							@Override
							public Subscriber<? super String> call(Subscriber<? super Integer> subscriber) {
								//这里返回的其实就是一个代理
								Subscriber<String> proxySubscriber =
								 new Subscriber<String>() {
									@Override
									public void onCompleted() {
										subscriber.onCompleted();
									}

									@Override
									public void onError(Throwable e) {
										subscriber.onError(e);
									}

									@Override
									public void onNext(String s) {
										subscriber.onNext(Integer.parseInt(s));
									}
								};
								return proxySubscriber;
							}
						});

		//订阅后触发事件
		//调用链是  integerObservable.OnSubscribeLift.oldOnSubscribe.call(proxySubscriber)
		//这个调用链就很明显了  将老的事件分发到被代理的Subscriber上
		//即 proxySubscriber.onNext("1")  -->  subscriber.onNext(Integer.parseInt(s))  老的事件转换成新的事件
		integerObservable.subscribe(System.out::println);

	}
}
