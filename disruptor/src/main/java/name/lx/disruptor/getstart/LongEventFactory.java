package name.lx.disruptor.getstart;

import com.lmax.disruptor.EventFactory;

/**
 * Created in 2018/6/4 11:07
 *
 */
public class LongEventFactory implements EventFactory<LongEvent> {
	@Override
	public LongEvent newInstance() {
		return new LongEvent();
	}
}
