package com.hxm.nio;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * @Description NIO缓冲区的数据读取
 * ByteBuffer  extends Buffer
 * 
 * 一、 4个核心属性
	  	private int position = 0;	正在操作的位置
	    private int limit;			界限 ，可操作数据大小
	    private int capacity;		缓冲区最大容量，声明后就不可变
	    
      	private int mark = -1;		标记position的位置，reset返回标记的位置
      	
 * 		 mark <= position <= limit <= capacity
 * 
 * @author hxm Email:550085798@qq.com
 * @date 2020年10月27日 下午3:13:59
 */
public class TestBuffer {
	/*
	 * 进行读取操作前一定先设置	buffer.flip();
	 * 可重复读数据		buffer.rewind();
	 */
	@Test
	public void test2() {
		//1. 分配
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		//2. 写入
		buffer.put("abcde".getBytes());
		
		//3. 读取
		buffer.flip();
		byte[] dst = new byte[buffer.limit()];
		buffer.get(dst,0,2);
		System.out.println(new String(dst,0,2));
		System.out.println("正在操作位置:"+buffer.position());
		
		//1. 标记
		buffer.mark();
		buffer.get(dst,2,2);
		System.out.println(new String(dst,2,2));
		System.out.println("正在操作位置:"+buffer.position());
		
		//2. reset() 返回标记位置
		buffer.reset();
		buffer.get(dst,0,2);
		System.out.println(new String(dst,0,2));
		System.out.println("正在操作位置:"+buffer.position());
		
		if (buffer.hasRemaining()) {
			System.out.println(buffer.remaining());
			
		}
		
		
	}

	@Test
	public void test1() {
		//1. 分配指定大小的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		System.out.println("正在操作位置"+buffer.position());
		System.out.println("界限"+buffer.limit());
		System.out.println("容量"+buffer.capacity());
		
		//2. put()写入操作
		buffer.put("abcde".getBytes());
		System.out.println(buffer.position());
		
		//3. 切换操作模式为读
		buffer.flip();
		
		System.out.println("**************");
		//4. get()
		byte[] des = new byte[5];
		buffer.get(des);
		System.out.println(new String(des,0,des.length));
		
		System.out.println("正在操作位置"+buffer.position());
		System.out.println("界限"+buffer.limit());
		System.out.println("容量"+buffer.capacity());
		
		//5. rewind(): 可重复读数据
		buffer.rewind();
		System.out.println("****************rewimd()************");
		System.out.println("正在操作位置"+buffer.position());
		System.out.println("界限"+buffer.limit());
		System.out.println("容量"+buffer.capacity());
		
		//6. clear(): 清空缓冲区，但是数据还在，只是被“遗忘了”
		buffer.clear();
		System.out.println("****************clear()************");
		System.out.println("正在操作位置:"+buffer.position());
		System.out.println("界限:"+buffer.limit());
		System.out.println("容量:"+buffer.capacity());
		
	}
}
