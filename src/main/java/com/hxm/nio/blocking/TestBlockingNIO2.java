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
 * @author hxm Email:550085798@qq.com
 * @date 2020年10月27日 下午7:58:40
 */
public class TestBlockingNIO2 {

	@Test
	public void client(){
		
		SocketChannel sChannel = null;
		FileChannel inChannel = null;
		try {
			sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",6666));
			
			inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
			
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			while (inChannel.read(buffer) != -1) {
				buffer.flip();
				sChannel.write(buffer);
				buffer.clear();	
			}
			
			sChannel.shutdownOutput();
			
			int len =0;
			while ((len = sChannel.read(buffer)) != -1) {
				
				buffer.flip();
				System.out.println(new String(buffer.array(),0,len));
				buffer.clear();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (sChannel != null) {
				try {
					sChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			if (inChannel != null) {
				try {
					inChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	@Test
	public void server() {
		
		ServerSocketChannel ssChannel = null;
		FileChannel outChannel = null;
		SocketChannel sChannel = null;
		try {
			ssChannel = ServerSocketChannel.open();
			
			outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);
			
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			ssChannel.bind(new InetSocketAddress(6666));
			
			
			sChannel = ssChannel.accept();
			
			
			while (sChannel.read(buffer) != -1) {
				buffer.flip();
				outChannel.write(buffer);
				buffer.clear();			
			}
			
			//反馈给客户端
			
			buffer.put("服务器接收数据了".getBytes());
			buffer.flip();
			sChannel.write(buffer);
			
			sChannel.shutdownOutput();
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (sChannel != null) {
				try {
					sChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (outChannel != null) {
				try {
					outChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
