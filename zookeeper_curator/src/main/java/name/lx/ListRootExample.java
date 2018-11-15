package name.lx;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.GetDataResponse;

import java.util.List;

/**
 * Created in 2018/10/15 10:43
 *
 */
public class ListRootExample {
	private static final String CONN_HOST = "127.0.0.1:2181";
	private static final String ROOT_PATH = "/";

	private static final String PREFIX_1 = "|";
	private static final String PREFIX_2 = "-";
	private static final String PREFIX_3 = "	";
	private static final String PREFIX_4 = "/";



	public static void main(String[] args) throws Exception {
		CuratorFramework client =
				CuratorFrameworkFactory
						.builder()
						.connectString(CONN_HOST)
						.retryPolicy(new ExponentialBackoffRetry(1000, 3))
						.sessionTimeoutMs(3000)
						.connectionTimeoutMs(1000)
						.build();
		client.start();
		listPath(client, ROOT_PATH, 0);

	}

	private static void listPath(CuratorFramework client, String path, int depth) throws Exception {
//		System.out.println("--------------" + path);
		List<String> childrenPathList = client.getChildren().forPath(path);
		if (childrenPathList == null || childrenPathList.size() == 0){
			return;
		}
		depth++;
		for(String childPath : childrenPathList){
			String childAbsPath ;
			if (path.equals(ROOT_PATH)){
				childAbsPath = path + childPath;
			} else {
				childAbsPath = path + PREFIX_4 + childPath;
			}
			printPath(client, childAbsPath, childPath, depth);
			listPath(client, childAbsPath, depth);
		}
	}

	private static void printPath(CuratorFramework client, String childAbsPath, String childPath, int depth)  throws Exception{
		Stat stat = new Stat();
		String data = new String(client.getData().storingStatIn(stat).forPath(childAbsPath));
		if (depth > 1){
			System.out.print(PREFIX_1);
			for (int i = 1; i < depth; i++){
				System.out.print(PREFIX_3);
			}
		}
		System.out.println(String.format("%s=%s", PREFIX_1 + PREFIX_2 + childPath, data));
	}
}
