package name.lx.disruptor.getstart.main;

import com.lmax.disruptor.dsl.Disruptor;
import name.lx.disruptor.getstart.LongEvent;
import sun.rmi.runtime.Log;

import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

/**
 * Created in 2018/6/8 15:49
 *
 */
public class SequenceMain {

	public static void main( String[] args) throws Exception{
		Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(() -> new LongEvent(), 8, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r);
			}
		});

		disruptor.handleEventsWith(((event, sequence, endOfBatch) -> {
			System.out.println(event);
		}));

		disruptor.start();

		IntStream.range(0, 10).forEach(i -> disruptor.publishEvent((event, sequence) -> event.setValue(Long.valueOf(i))));

//		Thread.sleep(5000);

		System.out.println(disruptor.getRingBuffer().getCursor());

	}
}
