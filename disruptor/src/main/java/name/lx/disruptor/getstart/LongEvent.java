package name.lx.disruptor.getstart;

import com.alibaba.fastjson.JSON;

/**
 * Created in 2018/6/4 11:06
 *
 */
public class LongEvent {
	private Long value;

	public void setValue(Long value) {
		this.value = value;
	}

	public Long getValue() {
		return value;
	}

	public void clear(){
		value = null;
	}
}
