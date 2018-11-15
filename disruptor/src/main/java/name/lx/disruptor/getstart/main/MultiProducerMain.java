package name.lx.disruptor.getstart.main;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import name.lx.disruptor.getstart.LongEvent;
import name.lx.disruptor.getstart.LongEventFactory;

import java.util.stream.IntStream;

/**
 * Created in 2018/6/13 18:17
 *
 */
public class MultiProducerMain {
	public static void main(String[] args) throws Exception{
		LongEventFactory factory = new LongEventFactory();

		int bufferSize = 512;

		Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize,
				new ThreadFactoryBuilder().setNameFormat("cons-%d").build(),
				ProducerType.MULTI, new BlockingWaitStrategy());

		disruptor.handleEventsWithWorkerPool(getWorkerPool());

		disruptor.start();

		IntStream.range(0, 300).forEach(i ->
			new Thread( () -> {
				while (true){
					disruptor.publishEvent((event, sequence) -> {
//						System.out.println("in set event " + sequence + " in " + Thread.currentThread().getName());
//						try {
//
//							Thread.sleep(10000);
//						} catch (Exception e) {
//
//						}
						event.setValue(0L);
//						System.out.println("out set event " + sequence + " in " + Thread.currentThread().getName());
					});
				}

			}
			).start()
		);
//
//		Thread.sleep(1000);
//
//		disruptor.publishEvent((event, sequence) -> {
//			System.out.println("in set event " + sequence + " in " + Thread.currentThread().getName());
//			event.setValue(1L);
//			System.out.println("out set event " + sequence + " in " + Thread.currentThread().getName());
//		});

		while (true){
			System.out.println(disruptor.getCursor() + "--" + disruptor.getRingBuffer().getMinimumGatingSequence());
			Thread.sleep(100);
		}

	}

	private static WorkHandler<LongEvent>[] getWorkerPool() {
		int n = 3;
		WorkHandler[] arr = new WorkHandler[n];
		IntStream.range(0, n).forEach(i -> {
			arr[i] = new WorkHandler<LongEvent>() {
				@Override
				public void onEvent(LongEvent event) throws Exception {
					System.out.println(event.getValue());
					Thread.sleep(200);
				}
			};
		});
		return arr;
	}
}
