package name.lx.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import java.io.InputStreamReader;

/**
 * Created in 2018/4/8 13:59
 *
 */
public class StartCommandRequestCache extends HystrixCommand<String> {

	//多个command公用Setter
	private static final Setter setter =
			Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("stCommandGroup"))
					//可不指定，默认为 getClass().getSimpleName()
					//Factory中自定义了一个Map，使用ConcurrentHashMap作为容器缓存HystrixCommandKey实例
					.andCommandKey(HystrixCommandKey.Factory.asKey("stCommand")
					);

	private String name;

	public StartCommandRequestCache(String name) {
		super(setter);
		this.name = name;
	}

	@Override
	protected String run() throws Exception {
//		Thread.sleep(1000);
		return "Hello " + name;
	}

	/**
	 * 请求缓存需要覆写该方法
	 *
	 * You enable request caching by implementing the getCacheKey() method
	 *
	 */
	@Override
	protected String getCacheKey() {
		return name;
	}

	public static void main(String[] args) throws Exception{

		System.out.println("------------------------------------------------------------");
		//JDK 7 try-with-resource HystrixRequestContext实现了Closeable 接口，会自动关闭
		try(
			HystrixRequestContext context1 = HystrixRequestContext.initializeContext()
		){
			StartCommandRequestCache c11 = new StartCommandRequestCache("a");
			System.out.println("c11 result -> " + c11.execute());
			System.out.println("c11 is cached -> " + c11.isResponseFromCache()); //false : 首次执行

			StartCommandRequestCache c12 = new StartCommandRequestCache("a");
			System.out.println("c12 result -> " + c12.execute());
			System.out.println("c12 is cached -> " + c12.isResponseFromCache()); // true : 命中缓存
		}

		System.out.println("------------------------------------------------------------");
		try(
			HystrixRequestContext context2 = HystrixRequestContext.initializeContext()
		){
			StartCommandRequestCache c21 = new StartCommandRequestCache("a");
			System.out.println("c21 result -> " + c21.execute());
			System.out.println("c21 is cached -> " + c21.isResponseFromCache()); //false : 新的request context
		}


		new InputStreamReader(System.in).read();
	}
}
