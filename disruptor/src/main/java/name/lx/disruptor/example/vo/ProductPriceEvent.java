package name.lx.disruptor.example.vo;


/**
 * Created in 2018/6/8 10:23
 *
 */
public class ProductPriceEvent {

	private String venderId;

	private Product product;


	public String getVenderId() {
		return venderId;
	}

	public void setVenderId(String venderId) {
		this.venderId = venderId;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
