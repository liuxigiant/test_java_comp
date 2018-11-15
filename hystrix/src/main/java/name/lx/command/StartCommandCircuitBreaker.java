package name.lx.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

import java.io.InputStreamReader;

/**
 * Created in 2018/4/8 21:34
 *
 */
public class StartCommandCircuitBreaker extends HystrixCommand<String> {

	private String req;

	public StartCommandCircuitBreaker(String req) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("stCommandGroup"))
					.andCommandKey(HystrixCommandKey.Factory.asKey("stCommand"))
					.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerRequestVolumeThreshold(1)));
		this.req = req;
	}

	@Override
	protected String run() throws Exception {
		System.out.println("in -> " + req);
		Thread.sleep(1100);
		return "success -> " + req;
	}

	@Override
	protected String getFallback() {
		return "fail -> " + req;
	}

	public static void main(String[] args) throws Exception {
		StartCommandCircuitBreaker c1 = new StartCommandCircuitBreaker("c1");
		System.out.println(c1.execute());
		System.out.println("c1 is open ? -> " + c1.circuitBreaker.isOpen());
		StartCommandCircuitBreaker c2 = new StartCommandCircuitBreaker("c2");
		System.out.println("B : c2 is open ? -> " + c2.circuitBreaker.isOpen());
		System.out.println(c2.execute());
		System.out.println("A : c2 is open ? -> " + c2.circuitBreaker.isOpen());
		StartCommandCircuitBreaker c3 = new StartCommandCircuitBreaker("c3");
		System.out.println("c3 is open ? -> " + c3.circuitBreaker.isOpen());

		new Thread(() -> {
			StartCommandCircuitBreaker c4 = new StartCommandCircuitBreaker("c3");
			System.out.println("c4 -> " + c4.circuitBreaker.isOpen());
		}).start();

		new InputStreamReader(System.in).read();
	}
}
