package com.hxm.nio.nonblock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;


import org.junit.Test;

/**
 *  *  * 一、使用 NIO 完成网络通信的三个核心：
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
 * 2. 缓冲区（Buffer）：负责数据的存取
 * 
 * 3. 选择器（Selector）：是 SelectableChannel 的多路复用器。用于监控 SelectableChannel 的 IO 状况
 *
 * 
 * @author hxm Email:550085798@qq.com
 * @date 2020年10月27日 下午8:17:41
 */
public class TestNonBlockingNIO {
	
	//一、 客户端
	@Test
	public void client() {
		SocketChannel sChannel = null;
		try {
			
			//1. 获取通道
			sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",6666));
			
			//2. 切换为非阻塞
			sChannel.configureBlocking(false);
			
			//3. 分配指定大小的缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			//4. 发送数据给服务端
			buffer.put(new Date().toString().getBytes());
			buffer.flip();
			sChannel.write(buffer);
			buffer.clear();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
			//5. 关闭通道
			if (sChannel != null) {
				try {
					sChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	//二、 服务端
	@Test
	public void server() {
		
		ServerSocketChannel ssChannel = null;
		try {
			
			//1. 获取通道
			ssChannel = ServerSocketChannel.open();
			
			//2. 设置非阻塞
			ssChannel.configureBlocking(false);
			
			//3. 绑定
			ssChannel.bind(new InetSocketAddress(6666));
			
			//4. 获取选择器
			Selector selector = Selector.open();
			
			//5. 将通道注册到选择器上, 并且指定“监听接收事件”
			ssChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			//6. 轮询式的获取选择器上已经“准备就绪”的事件
			while (selector.select() > 0) {
				
				//7. 获取当前选择器中所有注册的“选择键(已就绪的监听事件)”
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				
				while (iterator.hasNext()) {
					//8. 获取准备“就绪”的是事件
					SelectionKey selectionKey = iterator.next();
					
					//9. 判断具体是什么事件准备就绪
					if (selectionKey.isAcceptable()) {
						//10. 若“接收就绪”，获取客户端连接
						SocketChannel sChannel = ssChannel.accept();
						
						//11. 切换非阻塞模式
						sChannel.configureBlocking(false);
						
						//12. 将该通道注册到选择器上
						sChannel.register(selector, SelectionKey.OP_READ);
						
					
					}else if (selectionKey.isReadable()) {
						//13. 获取当前选择器上“读就绪”状态的通道
						SocketChannel sChannel = (SocketChannel) selectionKey.channel();
						
						//14. 读取数据
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						
						int len;
						while ((len = sChannel.read(buffer)) != -1) {
							buffer.flip();
							System.out.println(new String(buffer.array(), 0, len));
							buffer.clear();
						}
						
						
					}
					
					//15. 读取数据后取消选择键 SelectionKey
					iterator.remove();
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{

			//16. 关闭通道
			if (ssChannel != null) {
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
