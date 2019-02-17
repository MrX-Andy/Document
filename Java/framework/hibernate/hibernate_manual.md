```
增加
@Override
public boolean insertUser(UserBean bean){
	Session session = HibernateUtil.openSession();
	Transaction transaction = session.beginTransaction();
	try {
		session.save(bean);
		transaction.commit();
		return true;
	} catch (Exception e) {
		LogUtil.e(e);
		transaction.rollback();
	}finally{
		session.close();
	}
	return false;
}

查询
@Override
public UserBean queryUser(String phone, String pwd){
	Session session = HibernateUtil.openSession();
	Criteria criteria = session.createCriteria(UserBean.class);
	criteria.add(Restrictions.eq("u_phone", phone));
	if(ObjUtil.isNotEmpty(pwd)){
		criteria.add(Restrictions.eq("u_pwd", pwd));			
	}
	List<UserBean> list = criteria.list();
	if(ObjUtil.isEmpty(list)){
		return null;
	}
	return ObjUtil.isEmpty(list) ? null : list.get(0);
}


修改
@Override
public boolean updateUser(String hql)
{
	Session session = HibernateUtil.openSession();
	Transaction transaction = session.beginTransaction();
	try
	{
		Query query = session.createQuery(hql);
		int update = query.executeUpdate();
		transaction.commit();
		return update > 0;
	} catch (Exception e){
		LogUtil.e(e);
		transaction.rollback();
	}finally{
		session.close();
	}
	return false;
}


注意！！！
/*
 * 这里 是 实体类的 类名 - UserBean；
 * 不是 数据库的 表名 - t_book_food_user
 * */
String hql = "update UserBean set u_pwd="+pwd+" where u_phone="+phone;
boolean updateUser = userManDaoImp.updateUser(hql);


顺序占位符
/**
 * 这里 UserBean 是java 里面的 实体类， phone 是 实体类的属性名， pwd是 实体类的属性名
 */
String queryString = "FROM UserBean bean WHERE bean.phone = ? AND bean.pwd LIKE ?";
Query query = session.createQuery(queryString).setString(0, "13146008029").setString(1, "123456");
名称占位符
/**
 * 这里 UserBean 是java 里面的 实体类， phone 是 实体类的属性名， nickName是 实体类的属性名
 */
String queryString = "FROM UserBean bean WHERE bean.phone = :phone AND bean.nickName LIKE :nickName ORDER BY accountBalance";
Query query = session.createQuery(queryString).setString("phone", "13146008029").setString("nickName", "张三");
List<UserBean> list = query.list();
分页查询
/**
 * 这里 UserBean 是java 里面的 实体类， phone 是 实体类的属性名， pwd是 实体类的属性名
 */
String queryString = "FROM UserBean";
Query query = session.createQuery(queryString);
int pageIndex = 0;
int pageSize = 5;
query.setFirstResult(pageIndex * pageSize).setMaxResults(pageSize);
List<UserBean> list = query.list();
LogTrack.e(list.size());


命名查询
<hibernate-mapping package="com.alex.model.userman">
	<class name="UserBean" table="t_user" dynamic-update="true"
	</class> 
	<!-- 这里写的是HQL语句 -->
	<query name="nameList"><![CDATA[FROM UserBean user WHERE user.phone LIKE :phoneLast4Char]]></query>
</hibernate-mapping>

Query query = session.getNamedQuery("nameList");
List list = query.setString("phoneLast4Char", "%8029").list();
LogTrack.e(list);
投影查询
String hql = "SELECT new UserBean(user.phone, user.pwd) FROM UserBean user WHERE user.phone LIKE :phoneLast4Char";
Query query = session.createQuery(hql);
List list = query.setString("phoneLast4Char", "1314%").list();
LogTrack.e(list);


QBC查询
Criteria criteria = session.createCriteria(UserBean.class);
criteria.add(Restrictions.eq("phone", "13146008029"));
UserBean userBean = (UserBean) criteria.uniqueResult();
LogTrack.e(userBean);
Hibernate配置文件
<!-- 数据库方言 -->
<property name="dialect">org.hibernate.dialect.MySQLDialect</property>
<property name="connection.url">jdbc:mysql://localhost:3306/maternalshop</property>
<property name="connection.username">root</property>
<property name="connection.password">root</property>
<property name="connection.driver_class">com.mysql.jdbc.Driver</property>
<property name="myeclipse.connection.profile">mysql</property>
<!-- 输出 sql 语句 -->
<property name="show_sql">true</property>
<!-- 格式化输出 -->
<property name="format_sql">true</property>



hbm2ddl.auto的属性
以下说的表 数据表，就是数据库中的表
update
最常用的属性值，会根据.hbm.xml文件生成表，数据发生变化会自动更新表结构，但不会删除已有的行、列
create
会根据.hbm.xml文件生成数据表，每次都会删除上次的表，重新生成表
create-drop
会根据.hbm.xml文件生成表，但是SessionFactory一旦关闭，表就会自动删除
validate
会和数据库中的表进行比较，若 .hbm.xml文件中的列在数据表中不存在，泽抛出异常



数据库缓存
/*
 * flush 刷新缓存，是数据库中的记录和Session缓存中的对象的属性一致，
 * 为了保持一个可能会发送对应的SQL语句。
 * 1. 调用transaction.commit(); 会首先调用并执行 session.flush(); 刷新缓存，在提交事务
 * 2. 调用session.flush(); 可能会发送SQL语句，但不会提交事务
 * 3. 在执行 HQL 或 QBC 查询，会先进行session.flush(); 操作，为了得到数据库的记录中最新的数据
 * 4. 若记录的ID是由底层数据库通过自增的方式生成的， 在执行save方法，会立刻发送INSERT语句，
 * 	为了save方法后，必须保证对象的id是存在、有效的。
 * */
session.flush();

缓存刷新
/*
 * 会强制发送 select 语句，使得缓存中的数据和数据库中的记录保持一致
 * */
session.refresh(beanSession);

  
<!-- 设置MySQL隔离级别，读已提交 -->
<property name="connection.isolation">2</property>



```