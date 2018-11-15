package name.lx.disruptor.getstart;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * Created in 2018/6/4 11:11
 *
 */
public class LongEventProducer {
	private RingBuffer<LongEvent> ringBuffer;

	public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void onData(ByteBuffer bf){
		long sequence = ringBuffer.next();
		try{
			LongEvent event = ringBuffer.get(sequence);
			event.setValue(bf.getLong(0));
			System.out.println(String.format("Event producer sequence [%s] in thread [%s]",
								 sequence, Thread.currentThread().getName()));
		}finally {
			//申请sequence后，需要在finally里提交  -- 防止阻塞其他生产者提交数据
			ringBuffer.publish(sequence);
		}

	}

}
