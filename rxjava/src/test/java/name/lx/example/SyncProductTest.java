package name.lx.example;

import name.lx.example.handler.SyncProduct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created in 2018/5/18 10:39
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class SyncProductTest {

	@Autowired
	private SyncProduct syncProduct;

	@Test
	public void testSync() throws Exception{
		System.out.println("================================start");
		syncProduct.doSync();

//		Thread.sleep(2000);
//
//		System.out.println("================================second");
//		syncProduct.doSync();

		System.out.println("================================end");

		System.in.read();
	}


	@Test
	public void testSync2() throws Exception{
		System.out.println("================================start");
		syncProduct.doSync2();

		System.out.println("================================end");

		System.in.read();
	}


	@Test
	public void testCountDownLatch() throws Exception{
		System.out.println("-----------1");
		CountDownLatch latch = new CountDownLatch(1);

		System.out.println("-----------2");
		latch.await(3, TimeUnit.SECONDS);

		System.out.println("-----------3");
		System.in.read();
	}
}
