package name.lx.disruptor.getstart.main;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import name.lx.disruptor.example.vo.ProductPriceEvent;
import name.lx.disruptor.getstart.LongEvent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

/**
 * Created in 2018/6/11 17:06
 *
 */
public class ExceptionHandlerMain {

	public static void main(String[] args) throws Exception{
		ThreadFactory productThreadFactory = generateThreadFactory("productPool-");
		Disruptor<LongEvent> productDisruptor =
				new Disruptor<>(LongEvent::new, 4, productThreadFactory,
						ProducerType.MULTI, new BlockingWaitStrategy());

		productDisruptor.handleEventsWith((event, sequence, endOfBatch) -> {
			System.out.println(String.format("handle %s-%s-%s", event.getValue(), sequence, Thread.currentThread().getName()));

			Thread.sleep(5000);

//			if (event.getValue() == 2){
//				throw new RuntimeException("handle error " + event.getValue());
//			}
		});

//		productDisruptor.setDefaultExceptionHandler(getExceptionHandler());

		productDisruptor.start();

		LongStream.range(1, 10).forEach(i -> {
			productDisruptor.publishEvent((event, sequence) -> {
				event.setValue(i);
				System.out.println(String.format("publish %s-%s-%s", event.getValue(), sequence, Thread.currentThread().getName()));

				try{
//					Thread.sleep(2000);
				}catch (Exception e){

				}

			});

		});

		while (true){
			System.out.println("cursor -- " + productDisruptor.getCursor());
			System.out.println("min consumer sequence -- " + productDisruptor.getRingBuffer().getMinimumGatingSequence());
			Thread.sleep(5000);
		}

		//在时间都被处理完成后，停止所有时间处理器
		//需要所有事件publish完成之后，再调用这个方法
		//不会停止线程池，也不会等待线程执行结束
//		productDisruptor.shutdown();
	}

	private static ExceptionHandler<LongEvent> getExceptionHandler() {
		return new ExceptionHandler<LongEvent>() {
			@Override
			public void handleEventException(Throwable ex, long sequence, LongEvent event) {
				System.out.println(String.format("exception %s-%s-%s", event.getValue(), sequence, Thread.currentThread().getName()));
			}

			@Override
			public void handleOnStartException(Throwable ex) {
				System.out.println("handleOnStartException...");
			}

			@Override
			public void handleOnShutdownException(Throwable ex) {
				System.out.println("handleOnShutdownException...");
			}
		};
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
