package name.lx.rxjava2;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created in 2018/4/17 16:46
 *
 */
public class TestStart {
	public static void main(String[] args) throws Exception{
		ObservableOnSubscribe<String> onSubscribe
				= 	new ObservableOnSubscribe<String>() {
						@Override
						public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
							System.out.println("prepare 1");
							observableEmitter.onNext("1");

							System.out.println("prepare 2");
							observableEmitter.onNext("2");

							System.out.println("prepare 3");
							observableEmitter.onNext("3");
						}
					};

		//被观察者
		Observable<String> observable = Observable.create(onSubscribe);


		//观察者
		Observer<String> observer = new Observer<String>() {

			private Disposable disposable;
			@Override
			public void onSubscribe(Disposable disposable) {
				this.disposable = disposable;
			}

			@Override
			public void onNext(String s) {
				System.out.println(s);

				if (s.equals("2")){
					disposable.dispose();
				}
			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onComplete() {

			}
		};

		//订阅
		observable.subscribe(observer);

		System.in.read();
	}

}
