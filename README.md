# rabbitmq-2
rabbit和spring整合
##
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