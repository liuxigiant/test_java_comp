package name.lx.example.handler;

 import com.alibaba.fastjson.JSON;
 import com.google.common.util.concurrent.ThreadFactoryBuilder;
 import io.reactivex.Observable;
 import io.reactivex.schedulers.Schedulers;
 import name.lx.example.vo.Product;
 import org.springframework.stereotype.Service;

 import javax.annotation.PostConstruct;
 import javax.annotation.PreDestroy;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Random;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.atomic.AtomicInteger;

 /**
 * Created in 2018/5/15 14:41
 *
 */
@Service
public class SyncProduct {

//	public static void main(String[] args) throws Exception{
//		SyncProduct syncProduct = new SyncProduct();
//		syncProduct.doSync();
//
//
////		System.in.read();
//	}

	private ExecutorService venderPool;
	private ExecutorService productPool;
	private ExecutorService subPool;

	@PostConstruct
	public void init(){
		System.out.println("init------------------------");
		venderPool = Executors.newFixedThreadPool(3, new ThreadFactoryBuilder().setNameFormat("venderPool-%d").build());
		productPool = Executors.newFixedThreadPool(8, new ThreadFactoryBuilder().setNameFormat("productPool-%d").build());
		subPool = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setNameFormat("subPool-%d").build());
	}

	@PreDestroy
	public void clean(){
		System.out.println("clean------------------------");
		productPool.shutdown();
		venderPool.shutdown();
		subPool.shutdown();
	}

	public void doSync2() {
		try{
			List<String> venderList = getVenderList();
			List<Product> productList = new ArrayList<>();
			Observable.fromArray(venderList.toArray(new String[venderList.size()]))
					.map(s ->{
							System.out.println(String.format("emit %s in [%s] ", s, Thread.currentThread().getName()));
							netWorkCost(2000);
							return getProductListByVender(s);
					}).flatMap(sList -> Observable.fromArray(sList.toArray(new Product[sList.size()])))
					.subscribeOn(Schedulers.from(venderPool))
					.observeOn(Schedulers.from(subPool))
					.subscribe(p -> {
//						System.out.println(String.format("sub %s in [%s] ", p.getName(), Thread.currentThread().getName()));
						productList.add(p);
//						System.out.println(String.format("sub %s out [%s] ", p.getName(), Thread.currentThread().getName()));
					}, error -> {
						System.out.println("Exception sub " + Thread.currentThread().getName());
						System.out.println("Exception sub " + error.getMessage());
					});
			System.out.println("===================" + Thread.currentThread().getName());
			System.out.println(JSON.toJSON(productList));
		} catch (Exception e){
			System.out.println("Exception main " + Thread.currentThread().getName());
			System.out.println("Exception main " + e.getMessage());
			throw new RuntimeException(e);
		}
	}


	public void doSync() {
		try{
			List<String> venderList = getVenderList();
			List<Product> productList = new ArrayList<>();
			CountDownLatch venderLatch = new CountDownLatch(venderList.size());
			ConcurrentHashMap<String, AtomicInteger> venderMap = new ConcurrentHashMap<>();
			Observable.fromArray(venderList.toArray(new String[venderList.size()]))
					.flatMap(s ->
						Observable.just(s).subscribeOn(Schedulers.from(venderPool)).flatMap(ss -> {
							System.out.println(String.format("emit %s in [%s] ", ss, Thread.currentThread().getName()));
							List<Product> venderProductList = getProductListByVender(ss);
							prepareCountLatch(venderMap, venderLatch, ss, venderProductList.size());
							return Observable.fromArray(venderProductList.toArray(new Product[venderProductList.size()]));
						})
					)
					.flatMap(s ->
						Observable.just(s).subscribeOn(Schedulers.from(productPool)).map(ss -> {
							System.out.println(String.format("emit2 %s in [%s] ", ss.getName(), Thread.currentThread().getName()));
							getProductPrice(ss);
							return ss;
						})
					)
					.observeOn(Schedulers.from(subPool))
					.subscribe(p -> {
						System.out.println(String.format("sub %s in [%s] ", p.getName(), Thread.currentThread().getName()));
						productList.add(p);
						if (0 == venderMap.get(p.getVender()).decrementAndGet()){
							venderLatch.countDown();
						}
						System.out.println(String.format("sub %s out [%s] ", p.getName(), Thread.currentThread().getName()));
					}, error -> {
						System.out.println("Exception sub " + Thread.currentThread().getName());
						System.out.println("Exception sub " + error.getMessage());
						releaseLatch(venderLatch, productList);
					});
			venderLatch.await();

			System.out.println("===================" + Thread.currentThread().getName());
			System.out.println(JSON.toJSON(productList));
		} catch (Exception e){
			System.out.println("Exception main " + Thread.currentThread().getName());
			System.out.println("Exception main " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private void prepareCountLatch(ConcurrentHashMap<String, AtomicInteger> venderMap, CountDownLatch venderLatch, String ss, int size) {
		if (size == 0){
			venderLatch.countDown();
			return;
		}
		venderMap.putIfAbsent(ss, new AtomicInteger(size));
	}

	private void releaseLatch(CountDownLatch venderLatch, List<Product> productList) {
		while (venderLatch.getCount() > 0){
			venderLatch.countDown();
		}
		productList.clear();
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
		p.setPrice((Long.parseLong(p.getVender().substring(1)) * Long.parseLong(p.getName().replace(p.getVender(), "").substring(3))));
//		p.setPrice((long)(random.nextInt(5000)));

//		if (p.getVender().equals("v2")){
//			throw new RuntimeException("error price");
//		}

		netWorkCost(random.nextInt(1000));

	}

	private List<Product> getProductListByVender(String vender) {
		System.out.println(String.format("call %s in [%s] ", vender, Thread.currentThread().getName()));
		List<Product> venderProductList = new ArrayList<>();
		Random random = new Random();
//		netWorkCost(random.nextInt(5000));
//		netWorkCost(2000);

		for (int i = 1; i < 130; i++){
			Product p = new Product();
			p.setVender(vender);
			p.setName(vender + "--" + "p" + i);
			venderProductList.add(p);
		}

//		if (vender.equals("v2") ){
//			throw new RuntimeException("error product");
//		}
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
