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
 *  *  * һ��ʹ�� NIO �������ͨ�ŵ��������ģ�
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
 * 2. ��������Buffer�����������ݵĴ�ȡ
 * 
 * 3. ѡ������Selector������ SelectableChannel �Ķ�·�����������ڼ�� SelectableChannel �� IO ״��
 *
 * 
 * @author hxm Email:550085798@qq.com
 * @date 2020��10��27�� ����8:17:41
 */
public class TestNonBlockingNIO {
	
	//һ�� �ͻ���
	@Test
	public void client() {
		SocketChannel sChannel = null;
		try {
			
			//1. ��ȡͨ��
			sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",6666));
			
			//2. �л�Ϊ������
			sChannel.configureBlocking(false);
			
			//3. ����ָ����С�Ļ�����
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			//4. �������ݸ������
			buffer.put(new Date().toString().getBytes());
			buffer.flip();
			sChannel.write(buffer);
			buffer.clear();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
			//5. �ر�ͨ��
			if (sChannel != null) {
				try {
					sChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	//���� �����
	@Test
	public void server() {
		
		ServerSocketChannel ssChannel = null;
		try {
			
			//1. ��ȡͨ��
			ssChannel = ServerSocketChannel.open();
			
			//2. ���÷�����
			ssChannel.configureBlocking(false);
			
			//3. ��
			ssChannel.bind(new InetSocketAddress(6666));
			
			//4. ��ȡѡ����
			Selector selector = Selector.open();
			
			//5. ��ͨ��ע�ᵽѡ������, ����ָ�������������¼���
			ssChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			//6. ��ѯʽ�Ļ�ȡѡ�������Ѿ���׼�����������¼�
			while (selector.select() > 0) {
				
				//7. ��ȡ��ǰѡ����������ע��ġ�ѡ���(�Ѿ����ļ����¼�)��
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				
				while (iterator.hasNext()) {
					//8. ��ȡ׼���������������¼�
					SelectionKey selectionKey = iterator.next();
					
					//9. �жϾ�����ʲô�¼�׼������
					if (selectionKey.isAcceptable()) {
						//10. �������վ���������ȡ�ͻ�������
						SocketChannel sChannel = ssChannel.accept();
						
						//11. �л�������ģʽ
						sChannel.configureBlocking(false);
						
						//12. ����ͨ��ע�ᵽѡ������
						sChannel.register(selector, SelectionKey.OP_READ);
						
					
					}else if (selectionKey.isReadable()) {
						//13. ��ȡ��ǰѡ�����ϡ���������״̬��ͨ��
						SocketChannel sChannel = (SocketChannel) selectionKey.channel();
						
						//14. ��ȡ����
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						
						int len;
						while ((len = sChannel.read(buffer)) != -1) {
							buffer.flip();
							System.out.println(new String(buffer.array(), 0, len));
							buffer.clear();
						}
						
						
					}
					
					//15. ��ȡ���ݺ�ȡ��ѡ��� SelectionKey
					iterator.remove();
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{

			//16. �ر�ͨ��
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
