package name.lx.disruptor.example.handler;

import com.alibaba.fastjson.JSON;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import name.lx.disruptor.example.vo.Product;
import name.lx.disruptor.example.vo.ProductEvent;
import name.lx.disruptor.example.vo.ProductPriceEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
* Created in 2018/6/8 10:11
*
*/
public class SyncProduct {

	private static final int VENDER_THREAD_POOL_SIZE = 3;
	private static final int PRODUCT_THREAD_POOL_SIZE = 8;


	public static void main(String[] args) throws Exception{
		SyncProduct syncProduct = new SyncProduct();
		syncProduct.doSync();
	}

   public void doSync() {
	   try{
		   List<String> venderList = getVenderList();
		   List<Product> productList = new ArrayList<>();

		   //-------------product init & handle-----------------
		   ThreadFactory productThreadFactory = generateThreadFactory("productPool-");
		   Disruptor<ProductPriceEvent> productDisruptor =
				   new Disruptor<>(ProductPriceEvent::new, 16, productThreadFactory,
						   ProducerType.MULTI, new BlockingWaitStrategy());
		   productDisruptor.handleEventsWithWorkerPool(getProductWorkHandler())
				   			.then(((event, sequence, endOfBatch) -> {
				   				if (event.getProduct().getPrice() == null){
				   					return;
								}
								productList.add(event.getProduct());
								System.out.println(String.format("add product [%s] in [%s]", event.getProduct().getName(), Thread.currentThread().getName()));
				   			}));

		   productDisruptor.setDefaultExceptionHandler(getProductExceptionHandler());
		   productDisruptor.start();


		   //-------------vender init & handle-----------------
		   ThreadFactory venderThreadFactory = generateThreadFactory("venderPool-");
		   Disruptor<ProductEvent> venderDisruptor =
				   new Disruptor<>(ProductEvent::new, 4, venderThreadFactory,
						   			ProducerType.SINGLE, new BlockingWaitStrategy());
		   venderDisruptor.handleEventsWithWorkerPool(getVenderWorkHandler(productDisruptor));

		   venderDisruptor.setDefaultExceptionHandler(getVenderExceptionHandler());
		   venderDisruptor.start();

		   for(String v :  venderList){
			   venderDisruptor.publishEvent(((event, sequence) -> {
				   event.setVenderId(v);
				   System.out.println(String.format("publish vender [%s] in [%s]", v, Thread.currentThread().getName()));
			   }));
		   }

		   //wating...getMinimumGatingSequence
		   while ((venderDisruptor.getRingBuffer().getMinimumGatingSequence() + 1) < venderList.size()){
			   Thread.sleep(100);
			   System.out.println(String.format("wait vender [%s]--[%s]------------", venderDisruptor.getRingBuffer().getMinimumGatingSequence() + 1, venderList.size()));
		   }

		   System.out.println("get product for vender done");
		   System.out.println(String.format("venderDisruptor  [%s]--[%s]------------", venderDisruptor.getRingBuffer().getMinimumGatingSequence() + 1, venderList.size()));


		   while (productDisruptor.getRingBuffer().getMinimumGatingSequence() < productDisruptor.getRingBuffer().getCursor()){
			   Thread.sleep(100);
			   System.out.println(String.format("wait product [%s]--[%s]------------", productDisruptor.getRingBuffer().getMinimumGatingSequence(), productDisruptor.getRingBuffer().getCursor()));
		   }

		   System.out.println("get price for product done");
		   System.out.println(String.format("productDisruptor  [%s]--[%s]------------", productDisruptor.getRingBuffer().getMinimumGatingSequence(),  productDisruptor.getRingBuffer().getCursor()));

		   System.out.println("===================" + Thread.currentThread().getName());

		   System.out.println(JSON.toJSON(productList));

		   venderDisruptor.shutdown();
		   productDisruptor.shutdown();
	   } catch (Exception e){
		   System.out.println("Exception main " + Thread.currentThread().getName());
		   System.out.println("Exception main " + e.getMessage());
		   throw new RuntimeException(e);
	   }
   }

	private ExceptionHandler<ProductEvent> getVenderExceptionHandler() {
		return new ExceptionHandler<ProductEvent>() {
			@Override
			public void handleEventException(Throwable ex, long sequence, ProductEvent event) {
				System.out.println(String.format("get product for vender error, vender is %s", event.getVenderId()));
			}

			@Override
			public void handleOnStartException(Throwable ex) {
				System.out.println("disruptor start exception : " + ex.getMessage());
			}

			@Override
			public void handleOnShutdownException(Throwable ex) {
				System.out.println("disruptor shout down exception : " + ex.getMessage());
			}
		};
	}

	private ExceptionHandler<ProductPriceEvent> getProductExceptionHandler() {
		return new ExceptionHandler<ProductPriceEvent>() {
			@Override
			public void handleEventException(Throwable ex, long sequence, ProductPriceEvent event) {
				System.out.println(String.format("get product price error, vender is %s, product is %s", event.getVenderId(), event.getProduct().getName()));
			}

			@Override
			public void handleOnStartException(Throwable ex) {
				System.out.println("disruptor start exception : " + ex.getMessage());
			}

			@Override
			public void handleOnShutdownException(Throwable ex) {
				System.out.println("disruptor shout down exception : " + ex.getMessage());
			}
		};
	}

	private ThreadFactory generateThreadFactory(String threadNamePrefix) {
		return new ThreadFactory() {
			private volatile AtomicInteger counter = new AtomicInteger(0);
			public Thread newThread(Runnable r) {
				return  new Thread(r,  threadNamePrefix + counter.getAndIncrement());
			}
		};
	}

	private WorkHandler<ProductEvent>[] getVenderWorkHandler(Disruptor<ProductPriceEvent> productDisruptor) {
		WorkHandler<ProductEvent>[] handlerArr = new WorkHandler[VENDER_THREAD_POOL_SIZE];
		IntStream.range(0, VENDER_THREAD_POOL_SIZE)
					.forEach(i -> handlerArr[i] = event -> handleVender(productDisruptor, event));
		return handlerArr;
	}

	private void handleVender(Disruptor<ProductPriceEvent> productDisruptor, ProductEvent event) {
		List<Product> productListForVender = getProductListByVender(event.getVenderId());
		productListForVender.forEach(p -> {
			productDisruptor.publishEvent(((event1, sequence1) -> {
				event1.setProduct(p);
				System.out.println(String.format("publish product [%s] in [%s]", p.getName(), Thread.currentThread().getName()));
			}));
		});
		System.out.println(String.format("handle vender [%s] in [%s]", event.getVenderId(), Thread.currentThread().getName()));
	}

	private WorkHandler<ProductPriceEvent>[] getProductWorkHandler() {
		WorkHandler<ProductPriceEvent>[] handlerArr = new WorkHandler[PRODUCT_THREAD_POOL_SIZE];
		IntStream.range(0, PRODUCT_THREAD_POOL_SIZE)
					.forEach(i -> handlerArr[i] = event -> handleProduct(event));
		return handlerArr;
	}

	private void handleProduct(ProductPriceEvent event) {
		Product p = event.getProduct();
		getProductPrice(p);
		System.out.println(String.format("handle product [%s] in [%s]", p.getName(), Thread.currentThread().getName()));
	}


	private List<String> getVenderList() {
	   List<String> venderList = new ArrayList<>();
	   for (int i = 1; i < 15; i++){
		   venderList.add("v" + i);
	   }
	   return venderList;
   }

   private void getProductPrice(Product p) {
	   Random random = new Random();

//	   if (p.getVender().equals("v2")){
//		   throw new RuntimeException("error price");
//	   }

	   p.setPrice((Long.parseLong(p.getVender().substring(1)) * Long.parseLong(p.getName().replace(p.getVender(), "").substring(3))));
//		p.setPrice((long)(random.nextInt(5000)));



	   netWorkCost(random.nextInt(1000));

   }

   private List<Product> getProductListByVender(String vender) {
	   System.out.println(String.format("call %s in [%s] ", vender, Thread.currentThread().getName()));
	   List<Product> venderProductList = new ArrayList<>();
	   Random random = new Random();
//		netWorkCost(random.nextInt(5000));
//		netWorkCost(2000);

	   for (int i = 1; i < 13; i++){
		   Product p = new Product();
		   p.setVender(vender);
		   p.setName(vender + "--" + "p" + i);
		   venderProductList.add(p);
	   }

		if (vender.equals("v2") ){
			throw new RuntimeException("error product");
		}
	   return venderProductList;
   }

   private void netWorkCost(long time) {
	   try {
		   Thread.sleep(time);
	   } catch (Exception e){
		   throw new RuntimeException(e);
	   }
   }


}
