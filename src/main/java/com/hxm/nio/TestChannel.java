package com.hxm.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

/**
 * 
 * һ�� ͨ����channel��
 * 		�û�Ŀ����ԭ�ڵ�����ӣ������ƴ��䣬��Ҫ��ϻ���������
 * 
 * ���� ͨ������Ҫʵ����
 * 		|---FileChannel		����
 * 		|--SocketChanne		����
 * 		|--ServerSocketChannel		����
 * 		|--DataGramChannel		����
 * ���� ��ȡͨ��
 * 	1�� ͨ��java�ṩgetChannel()��������
 * 		����IO
 * 		FileInputStream��FileOutputStream
 * 		RandomAccessible��
 * 
 * 		����IO
 * 		Socket
 * 		ServerSocket
 * 		DataGramSocket
 * 	2. ��jdk 1.7 ��NIO.2 ���ṩ��open()��̬����
 * 	3. ��jdk 1.7 ��NIO.2 ��Files�������newByteChannel����
 *  
 * �ġ� ͨ��֮�����ݴ���
 * 		transferFrom()
 * 		transferTo()
 * 
 * 
 * �塢��ɢ(Scatter)��ۼ�(Gather)
 * ��ɢ��ȡ��Scattering Reads������ͨ���е����ݷ�ɢ�������������
 * �ۼ�д�루Gathering Writes����������������е����ݾۼ���ͨ����
 * 
 * �����ַ�����Charset
 * ���룺�ַ��� -> �ֽ�����
 * ���룺�ֽ�����  -> �ַ���
 * 
 * 
 * @author hxm Email:550085798@qq.com
 * @date 2020��10��27�� ����4:18:20
 */
public class TestChannel {
	//�ַ���
	@Test
	public void test6() {
		try {
			Charset cs1 = Charset.forName("GBK");
			
			//��ȡ������
			CharsetEncoder ce = cs1.newEncoder();//������
			
			//��ȡ��������
			CharsetDecoder cd = cs1.newDecoder();//������
			
			CharBuffer cBuf = CharBuffer.allocate(1024);
			cBuf.put("�й�����䣡");
			cBuf.flip();
			
			//����
			ByteBuffer bBuf = ce.encode(cBuf);
			
			for (int i = 0; i < 12; i++) {
				System.out.println(bBuf.get());
			}
			
			//����
			bBuf.flip();//��������
			CharBuffer cBuf2 = cd.decode(bBuf);
			System.out.println(cBuf2.toString());
			
			System.out.println("------------------------------------------------------");
			
			Charset cs2 = Charset.forName("UTF-8");//����
			bBuf.flip();
			CharBuffer cBuf3 = cs2.decode(bBuf);
			System.out.println(cBuf3.toString());
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	//�鿴�ж����ַ���
	@Test
	public void test5(){
		Map<String, Charset> map = Charset.availableCharsets();
		
		Set<Entry<String, Charset>> set = map.entrySet();
		
		for (Entry<String, Charset> entry : set) {
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
	}

	//��ɢ�;ۼ�
	@Test
	public void test4() {
		RandomAccessFile raf1 = null;
		//1. ��ȡͨ��
		FileChannel channel1 = null;
		//4. �ۼ�д��
		RandomAccessFile raf2 = null;
		FileChannel channel2 = null;
		try {
			raf1 = new RandomAccessFile("1.txt", "rw");
			
			channel1 = raf1.getChannel();
			
			//2. ����ָ����С�Ļ�����
			ByteBuffer buf1 = ByteBuffer.allocate(100);
			ByteBuffer buf2 = ByteBuffer.allocate(1024);
			
			//3. ��ɢ��ȡ
			ByteBuffer[] bufs = {buf1, buf2};
			channel1.read(bufs);
			
			for (ByteBuffer byteBuffer : bufs) {
				byteBuffer.flip();
			}
			
			System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
			System.out.println("-----------------");
			System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));
			
			raf2 = new RandomAccessFile("2.txt", "rw");
			channel2 = raf2.getChannel();
			
			channel2.write(bufs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (raf1 != null) {
				try {
					raf1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (channel1 != null) {
				try {
					channel1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			if (channel2 != null) {
				
				try {
					channel2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (raf2 != null) {
				
				try {
					raf2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	//ֱ�ӻ�������ͨ��֮�����ݴ���,
	@Test
	public void test3(){
		
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
			outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.READ,
					StandardOpenOption.WRITE,StandardOpenOption.CREATE);
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (inChannel != null) {
				try {
					inChannel.close();
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
		}
	}
	
	//ֱ�ӻ��������ڴ�ӳ��
	@Test
	public void test2() {
		
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
			outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.READ,
					StandardOpenOption.WRITE,StandardOpenOption.CREATE);
			
			MappedByteBuffer inMappedByteBuffer = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
			MappedByteBuffer outMappedByteBuffer = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());
			
			byte[] buffer = new byte[inMappedByteBuffer.limit()];
			inMappedByteBuffer.get(buffer);
			outMappedByteBuffer.put(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (inChannel != null) {
				try {
					inChannel.close();
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
		}
	}
		
	
	//�ļ�����  ��ֱ�ӻ�����
	@Test
	public void test1(){
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		//1. ��ȡͨ��
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			fis = new FileInputStream("1.jpg");
			fos = new FileOutputStream("2.jpg");
			
			inChannel = fis.getChannel();
			outChannel = fos.getChannel();
			
			//2. ���仺����
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			//2. ��ȡ����
			//4. д�����
			while (inChannel.read(buffer) != -1) {
				buffer.flip();
				outChannel.write(buffer);
				buffer.clear();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (inChannel!=null) {
				try {
					inChannel.close();
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
			if (fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
			}
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		

	}

}
