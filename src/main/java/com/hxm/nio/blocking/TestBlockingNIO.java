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
 *  * һ��ʹ�� NIO �������ͨ�ŵ��������ģ�
 * 
 * 1. ͨ����Channel������������
 * 		
 * 	   java.nio.channels.Channel �ӿڣ�
 * 			|--SelectableChannel
 * 				|--SocketChannel
 * 				|--ServerSocketChannel
 * 				|--DatagramChannel
 * 
 * 				|--Pipe.SinkChannel
 * 				|--Pipe.SourceChannel
 * 
 * 
 * 2. ��������Buffer�����������ݵĴ�ȡ
 * 
 * 3. ѡ������Selector������ SelectableChannel �Ķ�·�����������ڼ�� SelectableChannel �� IO ״��
 * 
 * @author hxm Email:550085798@qq.com
 * @date 2020��10��27�� ����7:24:26
 */
public class TestBlockingNIO {

	@Test
	public void client() {
		
		//1. ����ͨ��
		SocketChannel sChannel = null;
		FileChannel iChannel = null;
		try {
			sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9999));
			
			iChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
			
			//2. ����������
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			//3. ��ȡ���ݲ�����
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
				//4. �ر���Դ
				
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
		
		//1. ��ȡͨ��
		ServerSocketChannel ssChannel = null;
		FileChannel outChannel = null;
		//3. ��ȡ�ͻ������ӵ�ͨ��
		SocketChannel sChannel = null;
		try {
			ssChannel = ServerSocketChannel.open();
					
			outChannel = FileChannel.open(Paths.get("66.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
			//2. ������
			ssChannel.bind(new InetSocketAddress(9999));
			
			
			sChannel = ssChannel.accept();
			
			
			//3. ��ȡ����
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
