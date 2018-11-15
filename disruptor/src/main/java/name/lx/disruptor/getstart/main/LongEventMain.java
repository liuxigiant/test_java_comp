package name.lx.disruptor.getstart.main;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import name.lx.disruptor.getstart.LongEvent;
import name.lx.disruptor.getstart.LongEventFactory;
import name.lx.disruptor.getstart.LongEventProducer;
import name.lx.disruptor.getstart.LongEventProducerWithTranslator;

import java.nio.ByteBuffer;

/**
 * Created in 2018/6/4 11:21
 *
 */
public class LongEventMain {
	public static void main(String[] args) throws Exception{
		// Executor that will be used to construct new threads for consumers
//		Executor executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("prod-%d").build());

		// The factory for the event
		LongEventFactory factory = new LongEventFactory();

		// Specify the size of the ring buffer, must be power of 2.
		int bufferSize = 8;

		// Construct the Disruptor
		Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize,
															new ThreadFactoryBuilder().setNameFormat("cons-%d").build(),
															ProducerType.MULTI, new BlockingWaitStrategy());

		// Connect the handler
//		disruptor.handleEventsWith(new LongEventHandler());

		//JDK8 Lambda Functional Interface
		disruptor.handleEventsWith(((event, sequence, endOfBatch) ->{
			System.out.println(String.format("Event [%s] consumer sequence [%s] in thread [%s]. EndOfBatch ? [%s]",
					JSON.toJSONString(event), sequence, Thread.currentThread().getName(), endOfBatch));
			Thread.sleep(100000);
		}));

		// Start the Disruptor, starts all threads running
		disruptor.start();

		// Get the ring buffer from the Disruptor to be used for publishing.
		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

		LongEventProducer producer = new LongEventProducer(ringBuffer);
		LongEventProducerWithTranslator producerWithTranslator = new LongEventProducerWithTranslator(ringBuffer);
		ByteBuffer bb = ByteBuffer.allocate(8);
		for (long l = 0; true; l++)
		{
			bb.putLong(0, l);
			producer.onData(bb);
			producerWithTranslator.onData(bb);

			//JDK8 Lambda Functional Interface
			ringBuffer.publishEvent(((event, sequence, arg0) -> {
				event.setValue(arg0.getLong(0));
				System.out.println(String.format("Event producer-lambda sequence [%s] in thread [%s]",
						sequence, Thread.currentThread().getName()));
			}), bb);

			Thread.sleep(1000);
		}
	}
}
