package name.lx.disruptor.getstart.main;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import name.lx.disruptor.getstart.LongEvent;

import java.nio.ByteBuffer;

/**
 * Created in 2018/6/4 15:42
 */
public class ClearEventMain {

	public static void main(String[] args) throws Exception{
		Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, 1024,
													new ThreadFactoryBuilder().setNameFormat("cons-%d").build());

		disruptor.handleEventsWith(((event, sequence, endOfBatch) ->
				System.out.println(String.format("Event [%s] consumer sequence [%s] in thread [%s]. EndOfBatch ? [%s]",
						JSON.toJSONString(event), sequence, Thread.currentThread().getName(), endOfBatch))
		)).then(((event, sequence, endOfBatch) ->{
			System.out.println(String.format("Clear Event [%s] consumer sequence [%s] in thread [%s]. EndOfBatch ? [%s]",
					JSON.toJSONString(event), sequence, Thread.currentThread().getName(), endOfBatch));
			event.clear();
			Thread.sleep(1000000);
		}));

		// Start the Disruptor, starts all threads running
		disruptor.start();

		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
		ByteBuffer bb = ByteBuffer.allocate(8);
		for (long l = 0; true; l++)
		{
			bb.putLong(0, l);

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
