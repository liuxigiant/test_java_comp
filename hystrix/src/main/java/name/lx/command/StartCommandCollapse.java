package name.lx.command;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created in 2018/4/8 19:33
 *
 */
public class StartCommandCollapse extends HystrixCollapser<List<String>, String, Integer> {

	private final Integer key;

	public StartCommandCollapse(Integer key) {
		this.key = key;
	}

	@Override
	public Integer getRequestArgument() {
		return key;
	}

	@Override
	protected HystrixCommand<List<String>> createCommand(final Collection<CollapsedRequest<String, Integer>> requests) {
		return new BatchCommand(requests);
	}

	@Override
	protected void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, Integer>> requests) {
		int count = 0;
		for (CollapsedRequest<String, Integer> request : requests) {
			request.setResponse(batchResponse.get(count++));
		}
	}

	private static final class BatchCommand extends HystrixCommand<List<String>> {
		private final Collection<CollapsedRequest<String, Integer>> requests;

		private BatchCommand(Collection<CollapsedRequest<String, Integer>> requests) {
			super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
					.andCommandKey(HystrixCommandKey.Factory.asKey("GetValueForKey")));
			this.requests = requests;
		}

		@Override
		protected List<String> run() {
			ArrayList<String> response = new ArrayList<String>();
			for (CollapsedRequest<String, Integer> request : requests) {
				// artificial response for each argument received in the batch
				response.add("ValueForKey: " + request.getArgument());
			}
			return response;
		}
	}

	public static void main(String[] args) throws Exception {

		try(HystrixRequestContext context = HystrixRequestContext.initializeContext()){
			Future<String> c1 = new StartCommandCollapse(1).queue();
			Future<String> c2 = new StartCommandCollapse(2).queue();
			Future<String> c3 = new StartCommandCollapse(3).queue();
			Future<String> c4 = new StartCommandCollapse(4).queue();
			System.out.println("c1 result -> " + c1.get());
			System.out.println("c2 result -> " + c2.get());
			System.out.println("c3 result -> " + c3.get());
			System.out.println("c4 result -> " + c4.get());

			// assert that the batch command 'GetValueForKey' was in fact
			// executed and that it executed only once
			System.out.println("command size -> " + HystrixRequestLog.getCurrentRequest().getExecutedCommands().size());  //1
			HystrixCommand<?> command = HystrixRequestLog.getCurrentRequest().getExecutedCommands().toArray(new HystrixCommand<?>[1])[0];
			// assert the command is the one we're expecting
			assertEquals("GetValueForKey", command.getCommandKey().name());
			// confirm that it was a COLLAPSED command execution
			assertTrue(command.getExecutionEvents().contains(HystrixEventType.COLLAPSED));
			// and that it was successful
			assertTrue(command.getExecutionEvents().contains(HystrixEventType.SUCCESS));
		}
	}
}