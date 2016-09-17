# rabbitmq-2
rabbit和spring整合
## 发送
1. 整合
	1. 引入jar包

			<dependency>
				<groupId>com.rabbitmq</groupId>
				<artifactId>amqp-client</artifactId>
				<version>3.5.4</version>
			</dependency>
			<dependency>
				<groupId>org.springframework.amqp</groupId>
				<artifactId>spring-rabbit</artifactId>
				<version>1.6.1.RELEASE</version>
			</dependency>
	2. 配置文件

			<!-- 定义连接工厂bean -->
			<rabbit:connection-factory id="connectionFactory" host="localhost" port="5672" username="guest" password="guest" channel-cache-size="25"/>
			<!-- 不能是否配置文件方式引入 -->
			<!-- <rabbit:connection-factory id="connectionFactory" host="${hostName}" port="${portNumber}" username="${userName}" password="${password}" channel-cache-size="25"/> -->
			<!-- 配置消息发送器 -->
			<rabbit:template id="amqpTemplate" connection-factory="connectionFactory"/>
			<!-- 定义代理：管理exchange和queue以及绑定 -->
			<rabbit:admin connection-factory="connectionFactory"/>
			<!-- 定义exchange -->
			<rabbit:direct-exchange name="exchange"/>
			<!-- 申明队列：用于接收数据 -->
			<rabbit:queue name="queueName"/>
	3. 使用

			@Resource
			private AmqpTemplate amqpTemplate;
		
			@RequestMapping("/send")
			public String send(Model model,Message message){
				//将发送的数据转换成rabbit的message对象，但是其body为byte[]古取传递字节数组
				this.amqpTemplate.convertAndSend("test",routekey, message.getContent().getBytes());
				return "index";
			}
2. 注意事项
	1. 配置文件中不能使用分离配置文件的方式设置连接的信息
	2. **使用AmqpTemplate发送消息时**，如果带exchange、queue，则这些信息必须在配置文件中先声明
	3. 发送消息时：消息的内容得先转换成byte[]再进行传输

## 接收
3. 接受者
	1. 发送者采用direct方式发送数据
		1. 连接订阅信息


				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(hostName);
				factory.setUsername(userName);
				factory.setPassword(password);
				factory.setPort(portNumber);
				//创建连接
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();
				//声明exchange
				//channel.exchangeDeclare(exchangeName, "direct",true);
				//申明队列
				channel.queueDeclare(queueName, true, false, false, null);
				//channel.queueBind(queueName, exchangeName, routekey);
				QueueingConsumer consumer = new QueueingConsumer(channel);
				//接受指定消息队列中的数据
				channel.basicConsume(queueName, true, consumer);
		2. 处理信息

				Delivery delivery = consumer.nextDelivery();
				String message=new String(delivery.getBody());
				System.out.println("接收到的数据为："+message);
				consumer.handleRecoverOk(consumer.getConsumerTag());
2. 注意事项
	1. 如果是direct方式发送数据，订阅的路由要和发送的路由一致
	2. 使用@Value注解注入数据时(${属性名})，注意该属性所有的bean是由谁创建的，则该属性所在的配置文件就由谁管理。如：routedKey所在的Message
	SendAction由springmvc创建，则属性文件配置在springmvc的配置文件中