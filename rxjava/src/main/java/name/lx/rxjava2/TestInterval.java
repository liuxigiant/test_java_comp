package name.lx.rxjava2;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created in 2018/4/17 17:28
 *
 */
public class TestInterval {
	public static void main(String[] args) throws Exception {
		Observable.interval(3,2, TimeUnit.SECONDS)
				.subscribe(new Consumer<Long>() {
					@Override
					public void accept(Long aLong) throws Exception {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						System.out.println("interval :" + aLong + " at " + format.format(new Date())  + "\n");
					}
				});


		System.in.read();
	}
}
