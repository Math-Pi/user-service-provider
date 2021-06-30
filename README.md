# Dubbo Spring配置文件使用

## 一、API(分包)

> 建议将服务接口、服务模型、服务异常等均放在 API 包中，因为服务模型和异常也是 API 的一部分，这样做也符合分包原则：重用发布等价原则(REP)，共同重用原则(CRP)。

- bean
  - UserAddress
- service
  - OrderService
  - UserService

## 二、服务提供者

### 2.1 引入Maven依赖

1. API包
2. dubbo包
3. 操作zookeeper的包

`pom.xml`

```xml
<dependencies>
    <!-- 引入api -->
  	<dependency>
  		<groupId>com.cjm.gmall</groupId>
  		<artifactId>gmall-interface</artifactId>
  		<version>0.0.1-SNAPSHOT</version>
  	</dependency>
  	<!-- 引入dubbo -->
	<dependency>
	    <groupId>com.alibaba</groupId>
	    <artifactId>dubbo</artifactId>
	    <version>2.6.2</version>
	</dependency>
  	<!-- 注册中心使用的是zookeeper，引入操作zookeeper的客户端 -->
	<dependency>
	    <groupId>org.apache.curator</groupId>
	    <artifactId>curator-framework</artifactId>
	    <version>2.12.0</version>
	</dependency>
</dependencies>
```

### 2.2 编写实现类

`UserServiceImpl`

```java
/**
 * 1、将服务提供者注册到注册中心
 * 		1）导入dubbo依赖（2.6.2）、操作zookeeper的客户端依赖(curator)
 * 		2）配置服务提供者
 * 2、让服务消费者去注册中心订阅服务提供者的服务地址
 * @author 陈嘉名
 *
 */
public class UserServiceImpl implements UserService {

	public List<UserAddress> getUserAddressList(String userId) {
		System.out.println("UserServiceImpl.....old...");
		// TODO Auto-generated method stub
		UserAddress address1 = new UserAddress(1, "北京市昌平区宏福科技园综合楼3层", "1", "李老师", "010-56253825", "Y");
		UserAddress address2 = new UserAddress(2, "深圳市宝安区西部硅谷大厦B座3层（深圳分校）", "1", "王老师", "010-56253825", "N");
		/*try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return Arrays.asList(address1,address2);
	}
}
```

### 2.3 Spring配置文件

1. 服务名：<dubbo:application>
2. 注册中心：<dubbo:registry>
3. 通信协议：<dubbo:protocol>
4. 暴露服务：<dubbo:service
5. 服务提供方：<dubbo:provider>

`provider.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 			 http://www.springframework.org/schema/beans/spring-beans.xsd
		http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd
		http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

	<!-- 1、指定当前服务/应用的名字（同样的服务名字相同，不要和别的服务同名） -->
	<dubbo:application name="user-service-provider"></dubbo:application>
	
	<!-- 2、指定注册中心的位置 -->
	<!-- <dubbo:registry address="zookeeper://127.0.0.1:2181"></dubbo:registry> -->
	<dubbo:registry protocol="zookeeper" address="127.0.0.1:2181"></dubbo:registry>
	
	<!-- 3、指定通信规则（通信协议？通信端口） -->
	<dubbo:protocol name="dubbo" port="20882"></dubbo:protocol>
	
	<!-- 4、暴露服务   ref：指向服务的真正的实现对象 -->
	<dubbo:service interface="com.cjm.gmall.service.UserService" ref="userServiceImpl"></dubbo:service>
	
	<!-- 服务的实现 -->
	<bean id="userServiceImpl" class="com.cjm.gmall.service.impl.UserServiceImpl"></bean>
	
	<!--统一设置服务提供方的规则  -->
	<dubbo:provider timeout="1000"></dubbo:provider>
	
</beans>
```

### 2.4 启动类

> 读取并加载Spring配置文件provider.xml，启动项目。

`MainApplication`

```java
public class MainApplication {
	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext ioc = new ClassPathXmlApplicationContext("provider.xml");
		ioc.start();
		System.in.read();
	}
}
```

## 三、服务消费者

### 3.1 引入Maven依赖

1. API包
2. dubbo包
3. 操作zookeeper的包

`pom.xml`

```xml
<dependencies>
    <!-- 引入api -->
  	<dependency>
  		<groupId>com.cjm.gmall</groupId>
  		<artifactId>gmall-interface</artifactId>
  		<version>0.0.1-SNAPSHOT</version>
  	</dependency>
  	<!-- 引入dubbo -->
	<dependency>
	    <groupId>com.alibaba</groupId>
	    <artifactId>dubbo</artifactId>
	    <version>2.6.2</version>
	</dependency>
  	<!-- 注册中心使用的是zookeeper，引入操作zookeeper的客户端 -->
	<dependency>
	    <groupId>org.apache.curator</groupId>
	    <artifactId>curator-framework</artifactId>
	    <version>2.12.0</version>
	</dependency>
</dependencies>
```

### 3.2 编写实现类

`OrderUserviceImpl`

```java
/**
 * 1、将服务提供者注册到注册中心
 * 		1）导入dubbo依赖（2.6.2）、操作zookeeper的客户端依赖(curator)
 * 		2）配置服务提供者
 * 2、让服务消费者去注册中心订阅服务提供者的服务地址
 * @author 陈嘉名
 *
 */
@Service
public class OrderUserviceImpl implements OrderService{
	
	@Autowired
	private UserService userService;
	
	public void initOrder(String userId) {
		// TODO Auto-generated method stub
		System.out.println("用户id："+userId);
		//1、查询用户的收货地址
		List<UserAddress> addressList= userService.getUserAddressList(userId);
		System.out.println(addressList);
		for(UserAddress userAddress:addressList) {
			System.out.println(userAddress.getUserAddress());
		}
	}	
}
```

### 3.3 Spring配置文件

1. 服务名：<dubbo:application>
2. 注册中心：<dubbo:registry>
3. 引用服务：<dubbo:reference>
4. 服务消费方：<dubbo:consumer>

`consumer.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.3.xsd
		http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd
		http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">
	<context:component-scan base-package="com.cjm.gmall.service.impl"></context:component-scan>


	<dubbo:application name="order-service-consumer"></dubbo:application>
	
	<dubbo:registry address="zookeeper://127.0.0.1:2181"></dubbo:registry>
	
	<dubbo:reference interface="com.cjm.gmall.service.UserService" id="userService"></dubbo:reference>
		
	<!-- 配置当前消费者的统一规则：所有的服务都不检查 -->
	<dubbo:consumer check="false" timeout="5000"></dubbo:consumer>
	
</beans>
```

### 3.4 启动类

`MainApplication`

```java
public class MainApplication {

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("consumer.xml");
		OrderService orderService = applicationContext.getBean(OrderService.class);
		orderService.initOrder("1");
		System.out.println("调用完成...");
		System.in.read();
	}
}
```

