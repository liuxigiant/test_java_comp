package name.lx.disruptor.getstart;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * Created in 2018/6/4 11:15
 *
 */
public class LongEventProducerWithTranslator {
	private RingBuffer<LongEvent> ringBuffer;

	public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	//Translator 针对生产者事件发送的封装 （申请sequence、设置事件数据、提交）
	private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR_ONE_ARG =
			new EventTranslatorOneArg<LongEvent, ByteBuffer>() {
				@Override
				public void translateTo(LongEvent event, long sequence, ByteBuffer arg0) {
					event.setValue(arg0.getLong(0));
					System.out.println(String.format("Event producer-translator sequence [%s] in thread [%s]",
							sequence, Thread.currentThread().getName()));
				}
			};

	public void onData(ByteBuffer bf){
		ringBuffer.publishEvent(TRANSLATOR_ONE_ARG, bf);
	}
}
