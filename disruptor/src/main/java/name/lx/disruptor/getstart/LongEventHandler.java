package name.lx.disruptor.getstart;


import com.alibaba.fastjson.JSON;
import com.lmax.disruptor.EventHandler;

/**
 * Created in 2018/6/4 11:08
 *
 */
public class LongEventHandler implements EventHandler<LongEvent> {
	@Override
	public void onEvent(LongEvent event, long sequence, boolean endOfBatch){
		System.out.println(String.format("Event [%s] consumer sequence [%s] in thread [%s]. EndOfBatch ? [%s]",
				JSON.toJSONString(event), sequence, Thread.currentThread().getName(), endOfBatch));
	}
}
