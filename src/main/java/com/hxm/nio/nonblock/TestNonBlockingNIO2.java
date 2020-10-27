package com.hxm.nio.nonblock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Test;

/**
 * @author hxm Email:550085798@qq.com
 * @date 2020年10月27日 下午9:10:24
 */
public class TestNonBlockingNIO2 {

	// 发送端
	@Test
	public void send() {
		DatagramChannel dChannel = null;
		try {
			dChannel = DatagramChannel.open();
			
			dChannel.configureBlocking(false);
			
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			Scanner scanner = new Scanner(System.in);
			
			while (scanner.hasNext()) {
				String string = scanner.next();
				buffer.put((new Date().toString()+"\n"+string).getBytes());
				buffer.flip();
				dChannel.send(buffer, new InetSocketAddress("127.0.0.1",9999));
				buffer.clear();			
			}
			scanner.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (dChannel != null) {
				try {
					dChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	// 接收端
	@Test
	public void receiver() {
		DatagramChannel dChannel = null;
		try {
			
			dChannel = DatagramChannel.open();
			
			dChannel.configureBlocking(false);
			
			dChannel.bind(new InetSocketAddress(9999));
			
			Selector selector = Selector.open();
			
			dChannel.register(selector, SelectionKey.OP_READ);
			
			while (selector.select() > 0) {
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				
				while (iterator.hasNext()) {
					
					SelectionKey selectionKey = iterator.next();
					
					if (selectionKey.isReadable()) {
						
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						
						dChannel.receive(buffer);
						buffer.flip();
						System.out.println(new String(buffer.array(), 0, buffer.limit()));
						buffer.clear();
					}
				}
				iterator.remove();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (dChannel != null) {
				
				try {
					dChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
