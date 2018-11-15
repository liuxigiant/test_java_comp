package name.lx.command;

/**
 * Created in 2018/4/2 19:49
 *
 */
public class TestCommand {
	public static void main(String[] args) throws Exception{
		TestFallBackCommand c1 = new TestFallBackCommand("c1");
		c1.queue();
		TestFallBackCommand c2 = new TestFallBackCommand("c2");
		c2.queue();
		TestFallBackCommand c3 = new TestFallBackCommand("c3");
		c3.queue();
		TestFallBackCommand c4 = new TestFallBackCommand("c4");
		c4.queue();
		TestFallBackCommand c5 = new TestFallBackCommand("c5");
		c5.queue();

		System.out.println("--------------------------------------");

		TestFallBackCommand c6 = new TestFallBackCommand("c6");
		c6.queue();

		System.out.println("**************************************");

		while(true){
			Thread.sleep(10000);
		}
	}
}
