package name.lx.rxjava2;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import java.util.List;

/**
 * Created in 2018/4/17 17:23
 *
 */
public class TestBuffer {
	public static void main(String[] args) {
		Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
				.buffer(3, 2)
				.subscribe(new Consumer<List<Integer>>() {
					@Override
					public void accept(List<Integer> integers) throws Exception {
						System.out.println(integers);
					}
				});


	}
}
