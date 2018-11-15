package name.lx.rxjava1;


import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.io.InputStreamReader;

/**
 * Created in 2018/4/16 18:25
 *
 */
public class TestAsync {
	public static void main(String[] args) throws Exception{
		Observable
				.create(subscribe ->{
							System.out.println("send event -> 1" + String.format(" ThreadName-%s", Thread.currentThread().getName()));
							subscribe.onNext("1");
							try{
								Thread.sleep(3000);
							}catch (Exception e){
							}
							System.out.println("send event -> 2" + String.format(" ThreadName-%s", Thread.currentThread().getName()));
							subscribe.onNext("2");
						})
				//事件产生在异步线程中执行，不会阻塞当前线程执行
				.subscribeOn(Schedulers.newThread())
				.subscribe(s -> System.out.println("consume event -> "+ s + String.format(" ThreadName-%s", Thread.currentThread().getName())));



		System.out.println("end -----------------" + String.format(" ThreadName-%s", Thread.currentThread().getName()));

		System.in.read();
	}
}
