package name.lx.disruptor.getstart.main;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import name.lx.disruptor.getstart.LongEvent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Created in 2018/6/14 16:09
 *
 */
public class ConsumerDependencyMain {
	public static void main(String[] args) {
		ThreadFactory productThreadFactory = generateThreadFactory("pool-");
		Disruptor<LongEvent> disruptor =
				new Disruptor<>(LongEvent::new, 4, productThreadFactory,
						ProducerType.SINGLE, new BlockingWaitStrategy());

		EventHandler<LongEvent> c1 = (event, sequence, endOfBatch) -> System.out.println(String.format("c1 handle sequence %s in %s", sequence, Thread.currentThread().getName()));
		EventHandler<LongEvent> c2 = (event, sequence, endOfBatch) -> System.out.println(String.format("c2 handle sequence %s in %s", sequence, Thread.currentThread().getName()));
		EventHandler<LongEvent> c3 = (event, sequence, endOfBatch) -> System.out.println(String.format("c3 handle sequence %s in %s", sequence, Thread.currentThread().getName()));
		disruptor.handleEventsWith(c1, c2).then(c3);

		disruptor.start();

		IntStream.range(0, 20).forEach( i ->
			disruptor.publishEvent((event, sequence) -> {
				event.setValue(sequence);
				System.out.println(String.format("p1 publish sequence %s in %s", sequence, Thread.currentThread().getName()));
			})
		);

		disruptor.shutdown();
	}

	private static ThreadFactory generateThreadFactory(String threadNamePrefix) {
		return new ThreadFactory() {
			private volatile AtomicInteger counter = new AtomicInteger(0);
			public Thread newThread(Runnable r) {
				return  new Thread(r,  threadNamePrefix + counter.getAndIncrement());
			}
		};
	}
}
