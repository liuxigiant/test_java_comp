package name.lx.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Created in 2018/3/30 11:10
 *
 */
public class TestFallBackCommand extends HystrixCommand<String> {

	private final String name;
	public TestFallBackCommand(String name) {
		super(Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HelloWorldGroup"))
                /* 配置依赖超时时间,500毫秒*/
//				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(500)));

//				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("pool"))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(5).withMaximumSize(5).withMaxQueueSize(5))
//				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(50000))
		);
		this.name = name;
	}

	@Override
	protected String getFallback() {
		System.out.println("fallback --> exeucute Falled");
		return "exeucute Falled";
	}

	@Override
	protected String run() throws Exception {
//		throw new HystrixBadRequestException("bad argument");

		System.out.println("in---");
		//sleep 1 秒,调用会超时
//		TimeUnit.MILLISECONDS.sleep(1000);
		TimeUnit.MILLISECONDS.sleep(1000);
		System.out.println("sleep end -- ");
		String resp = "Hello " + name +" thread:" + Thread.currentThread().getName();
		System.out.println("resp --> " + resp);
		return resp;
	}

	public static void main(String[] args) throws Exception{
		TestFallBackCommand command = new TestFallBackCommand("test-Fallback");
		String result = command.execute();
		System.out.println("result --> " + result);

		command.circuitBreaker.isOpen();

		new InputStreamReader(System.in).read();
	}
}
