package com.hxm.nio;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * @Description NIO�����������ݶ�ȡ
 * ByteBuffer  extends Buffer
 * 
 * һ�� 4����������
	  	private int position = 0;	���ڲ�����λ��
	    private int limit;			���� ���ɲ������ݴ�С
	    private int capacity;		���������������������Ͳ��ɱ�
	    
      	private int mark = -1;		���position��λ�ã�reset���ر�ǵ�λ��
      	
 * 		 mark <= position <= limit <= capacity
 * 
 * @author hxm Email:550085798@qq.com
 * @date 2020��10��27�� ����3:13:59
 */
public class TestBuffer {
	/*
	 * ���ж�ȡ����ǰһ��������	buffer.flip();
	 * ���ظ�������		buffer.rewind();
	 */
	@Test
	public void test2() {
		//1. ����
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		//2. д��
		buffer.put("abcde".getBytes());
		
		//3. ��ȡ
		buffer.flip();
		byte[] dst = new byte[buffer.limit()];
		buffer.get(dst,0,2);
		System.out.println(new String(dst,0,2));
		System.out.println("���ڲ���λ��:"+buffer.position());
		
		//1. ���
		buffer.mark();
		buffer.get(dst,2,2);
		System.out.println(new String(dst,2,2));
		System.out.println("���ڲ���λ��:"+buffer.position());
		
		//2. reset() ���ر��λ��
		buffer.reset();
		buffer.get(dst,0,2);
		System.out.println(new String(dst,0,2));
		System.out.println("���ڲ���λ��:"+buffer.position());
		
		if (buffer.hasRemaining()) {
			System.out.println(buffer.remaining());
			
		}
		
		
	}

	@Test
	public void test1() {
		//1. ����ָ����С�Ļ�����
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		System.out.println("���ڲ���λ��"+buffer.position());
		System.out.println("����"+buffer.limit());
		System.out.println("����"+buffer.capacity());
		
		//2. put()д�����
		buffer.put("abcde".getBytes());
		System.out.println(buffer.position());
		
		//3. �л�����ģʽΪ��
		buffer.flip();
		
		System.out.println("**************");
		//4. get()
		byte[] des = new byte[5];
		buffer.get(des);
		System.out.println(new String(des,0,des.length));
		
		System.out.println("���ڲ���λ��"+buffer.position());
		System.out.println("����"+buffer.limit());
		System.out.println("����"+buffer.capacity());
		
		//5. rewind(): ���ظ�������
		buffer.rewind();
		System.out.println("****************rewimd()************");
		System.out.println("���ڲ���λ��"+buffer.position());
		System.out.println("����"+buffer.limit());
		System.out.println("����"+buffer.capacity());
		
		//6. clear(): ��ջ��������������ݻ��ڣ�ֻ�Ǳ��������ˡ�
		buffer.clear();
		System.out.println("****************clear()************");
		System.out.println("���ڲ���λ��:"+buffer.position());
		System.out.println("����:"+buffer.limit());
		System.out.println("����:"+buffer.capacity());
		
	}
}
