package com.hxm.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

import org.junit.Test;

/**
 * @author hxm Email:550085798@qq.com
 * @date 2020年10月27日 下午9:31:21
 */
public class TestPipe {
	
	@Test
	public void test1() {
		
		Pipe.SinkChannel sinkChannel = null;
		//3. 读取缓冲区中的数据
		Pipe.SourceChannel sourceChannel = null;
		try {
			//1. 获取管道
			Pipe pipe = Pipe.open();
			
			//2. 将缓冲区中的数据写入管道
			ByteBuffer buf = ByteBuffer.allocate(1024);
			
			sinkChannel = pipe.sink();
			buf.put("通过单向管道发送数据".getBytes());
			buf.flip();
			sinkChannel.write(buf);
			
			sourceChannel = pipe.source();
			buf.flip();
			int len = sourceChannel.read(buf);
			System.out.println(new String(buf.array(), 0, len));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (sourceChannel != null) {
				try {
					sourceChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (sinkChannel != null) {
				try {
					sinkChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
