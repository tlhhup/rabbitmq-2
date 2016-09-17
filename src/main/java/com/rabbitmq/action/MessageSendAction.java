package com.rabbitmq.action;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rabbitmq.entity.Message;

@Controller
@RequestMapping("/MessageSendAction")
public class MessageSendAction {
	
	@Value("${routekey}")
	private String routekey;
	
	@Value("${exchangeName}")
	private String exchangeName;
	
	@Resource
	private AmqpTemplate amqpTemplate;

	@RequestMapping("/send")
	public String send(Model model,Message message){
		//将发送的数据转换成rabbit的message对象，但是其body为byte[]古取传递字节数组
		//指定exchange和路由值
		this.amqpTemplate.convertAndSend(exchangeName,routekey, message.getContent().getBytes());
		return "index";
	}
	
}
