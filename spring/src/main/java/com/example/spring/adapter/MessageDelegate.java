package com.example.spring.adapter;

public class MessageDelegate { // 这一行的写法是固定的

//    public void handleMessage(byte[] messageBody) {
//        System.err.println("默认方法, 消息内容:" + new String(messageBody));
//    }
//
//    public void consumeMessage(byte[] messageBody) {
//        System.err.println("字节数组方法, 消息内容:" + new String(messageBody));
//    }
	public void consumeMessage(String messageBody) {
		System.err.println("字符串方法, 消息内容:" + messageBody);
	}

}
