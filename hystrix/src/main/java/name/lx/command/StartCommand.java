package name.lx.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.io.InputStreamReader;
import java.util.concurrent.Future;

/**
 * Created in 2018/4/5 17:54
 *
 */
public class StartCommand extends HystrixCommand<String> {

	//多个command公用Setter
	private static final Setter setter =
			Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("stCommandGroup"))
					//可不指定，默认为 getClass().getSimpleName()
					//Factory中自定义了一个Map，使用ConcurrentHashMap作为容器缓存HystrixCommandKey实例
					.andCommandKey(HystrixCommandKey.Factory.asKey("stCommand")
					);

	private String name;

	public StartCommand(String name) {
		super(setter);
		this.name = name;
	}

	@Override
	protected String run() throws Exception {
//		Thread.sleep(1000);
		return "Hello " + name;
	}

	public static void main(String[] args) throws Exception{

		//=======================async======================
		StartCommand async = new StartCommand("async");
		Future<String> asyncFuture = async.queue();
		while(true){
			if (asyncFuture.isDone() && !asyncFuture.isCancelled()){
				String asyncResult = asyncFuture.get();
				System.out.println("async result --> " + asyncResult);
				break;
			}
		}

		//========================sync======================
		StartCommand sync = new StartCommand("sync");
		String syncResult = sync.execute();  // execute() 其实是伪同步，等效于 queue().get()
		System.out.println("sync result --> " + syncResult);


		//=======================Observable=================
		Observable<String> observable = new StartCommand("observable").observe();  // toObservable -- 一个command只能被执行一次
		observable.subscribe(new Action1<String>() {
			@Override
			public void call(String s) {
				System.out.println("observable result --> " + s);
			}
		});
		//JDK8
		Observable<String> observable1 = new StartCommand("observable1").observe();
		observable1.subscribe(s -> System.out.println("observable result --> " + s));

		Observable<String> observable2 = new StartCommand("observable2").observe();
		observable2.subscribe(new Observer<String>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onNext(String s) {
				System.out.println("observable result --> " + s);
			}
		});
		//JDK8
		Observable<String> observable3 = new StartCommand("observable3").observe();
		observable3.subscribe(
				s -> System.out.println("observable result --> " + s),
				Throwable::printStackTrace //e -> e.printStackTrace()
		);









		new InputStreamReader(System.in).read();
	}
}
