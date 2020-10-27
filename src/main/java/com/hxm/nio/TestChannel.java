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
 * 一、 通道（channel）
 * 		用户目标与原节点的链接，不复制传输，需要配合缓冲区操作
 * 
 * 二、 通道的主要实现类
 * 		|---FileChannel		本地
 * 		|--SocketChanne		网络
 * 		|--ServerSocketChannel		网络
 * 		|--DataGramChannel		网络
 * 三、 获取通道
 * 	1、 通过java提供getChannel()方法的类
 * 		本地IO
 * 		FileInputStream、FileOutputStream
 * 		RandomAccessible、
 * 
 * 		网络IO
 * 		Socket
 * 		ServerSocket
 * 		DataGramSocket
 * 	2. 在jdk 1.7 的NIO.2 中提供的open()静态方法
 * 	3. 在jdk 1.7 的NIO.2 中Files工具类的newByteChannel方法
 *  
 * 四、 通道之间数据传输
 * 		transferFrom()
 * 		transferTo()
 * 
 * 
 * 五、分散(Scatter)与聚集(Gather)
 * 分散读取（Scattering Reads）：将通道中的数据分散到多个缓冲区中
 * 聚集写入（Gathering Writes）：将多个缓冲区中的数据聚集到通道中
 * 
 * 六、字符集：Charset
 * 编码：字符串 -> 字节数组
 * 解码：字节数组  -> 字符串
 * 
 * 
 * @author hxm Email:550085798@qq.com
 * @date 2020年10月27日 下午4:18:20
 */
public class TestChannel {
	//字符集
	@Test
	public void test6() {
		try {
			Charset cs1 = Charset.forName("GBK");
			
			//获取编码器
			CharsetEncoder ce = cs1.newEncoder();//编码器
			
			//获取解码器！
			CharsetDecoder cd = cs1.newDecoder();//解码器
			
			CharBuffer cBuf = CharBuffer.allocate(1024);
			cBuf.put("尚硅谷威武！");
			cBuf.flip();
			
			//编码
			ByteBuffer bBuf = ce.encode(cBuf);
			
			for (int i = 0; i < 12; i++) {
				System.out.println(bBuf.get());
			}
			
			//解码
			bBuf.flip();//否则乱码
			CharBuffer cBuf2 = cd.decode(bBuf);
			System.out.println(cBuf2.toString());
			
			System.out.println("------------------------------------------------------");
			
			Charset cs2 = Charset.forName("UTF-8");//乱码
			bBuf.flip();
			CharBuffer cBuf3 = cs2.decode(bBuf);
			System.out.println(cBuf3.toString());
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	//查看有多少字符集
	@Test
	public void test5(){
		Map<String, Charset> map = Charset.availableCharsets();
		
		Set<Entry<String, Charset>> set = map.entrySet();
		
		for (Entry<String, Charset> entry : set) {
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
	}

	//分散和聚集
	@Test
	public void test4() {
		RandomAccessFile raf1 = null;
		//1. 获取通道
		FileChannel channel1 = null;
		//4. 聚集写入
		RandomAccessFile raf2 = null;
		FileChannel channel2 = null;
		try {
			raf1 = new RandomAccessFile("1.txt", "rw");
			
			channel1 = raf1.getChannel();
			
			//2. 分配指定大小的缓冲区
			ByteBuffer buf1 = ByteBuffer.allocate(100);
			ByteBuffer buf2 = ByteBuffer.allocate(1024);
			
			//3. 分散读取
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
	
	//直接缓冲区，通道之间数据传输,
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
	
	//直接缓冲区，内存映射
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
		
	
	//文件复制  非直接缓冲区
	@Test
	public void test1(){
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		//1. 获取通道
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			fis = new FileInputStream("1.jpg");
			fos = new FileOutputStream("2.jpg");
			
			inChannel = fis.getChannel();
			outChannel = fos.getChannel();
			
			//2. 分配缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			//2. 读取操作
			//4. 写入操作
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
