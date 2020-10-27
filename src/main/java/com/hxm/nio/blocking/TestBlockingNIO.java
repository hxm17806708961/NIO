package com.hxm.nio.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

/**
 * 
 *  * 一、使用 NIO 完成网络通信的三个核心：
 * 
 * 1. 通道（Channel）：负责连接
 * 		
 * 	   java.nio.channels.Channel 接口：
 * 			|--SelectableChannel
 * 				|--SocketChannel
 * 				|--ServerSocketChannel
 * 				|--DatagramChannel
 * 
 * 				|--Pipe.SinkChannel
 * 				|--Pipe.SourceChannel
 * 
 * 
 * 2. 缓冲区（Buffer）：负责数据的存取
 * 
 * 3. 选择器（Selector）：是 SelectableChannel 的多路复用器。用于监控 SelectableChannel 的 IO 状况
 * 
 * @author hxm Email:550085798@qq.com
 * @date 2020年10月27日 下午7:24:26
 */
public class TestBlockingNIO {

	@Test
	public void client() {
		
		//1. 创建通道
		SocketChannel sChannel = null;
		FileChannel iChannel = null;
		try {
			sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9999));
			
			iChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
			
			//2. 创建缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			//3. 读取数据并发送
			while (iChannel.read(buffer) != -1) {
				buffer.flip();
				sChannel.write(buffer);
				buffer.clear();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (sChannel != null) {
				//4. 关闭资源
				
				try {
					iChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			if (iChannel!=null) {
				
				try {
					sChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	@Test
	public void server(){
		
		//1. 获取通道
		ServerSocketChannel ssChannel = null;
		FileChannel outChannel = null;
		//3. 获取客户端连接的通道
		SocketChannel sChannel = null;
		try {
			ssChannel = ServerSocketChannel.open();
					
			outChannel = FileChannel.open(Paths.get("66.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
			//2. 绑定链接
			ssChannel.bind(new InetSocketAddress(9999));
			
			
			sChannel = ssChannel.accept();
			
			
			//3. 获取数据
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			while (sChannel.read(buffer) != -1) {
				buffer.flip();
				outChannel.write(buffer);
				buffer.clear();			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (sChannel!= null) {
				try {
					sChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			if (outChannel!=null) {
				try {
					outChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (ssChannel!=null) {
				try {
					ssChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}
}
