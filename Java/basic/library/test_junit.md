### 单元测试  

```
/**
 * 作者： Alex
 * 时间：2017-3-11 上午12:18:08 
 * 简述：
 */
@SuppressWarnings("all")
public class HibernateTest {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;
	
	/**
	 * 必须 public 类型
	 * 单元测试开始前， 一定会调用，可以初始化一些参数
	 */
	@org.junit.Before
	public void onCreate(){
		LogTrack.w("onCreate");
		
	}
	/**
	 * 必须 public 类型
	 * 单元测试结束后，一定会调用，可以回收一些资源
	 */
	@org.junit.After
	public void onDestroy(){
		LogTrack.w("onDestroy");
	}
	
	/**
	 * 必须 public 类型
	 * 必须 是Test后缀
	 */
	@org.junit.Test
	public void simpleTest(){
		LogTrack.w("simpleTest");
		
	}
}
```