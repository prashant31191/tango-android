package or.tango.android.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

import org.apache.http.entity.ByteArrayEntity;

import or.tango.android.activity.PicActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyByteArrayEntity extends ByteArrayEntity{
	private Handler handler;
	private ExecutorService service;

	/**
	 * @param b 数据
	 * @param handler 发送进度
	 * @param service 判断是否中止了任务
	 */
	public MyByteArrayEntity(byte[] b,Handler handler,ExecutorService service) {
		super(b);
		this.service=service;
		this.handler=handler;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		ByteArrayInputStream ips=null;
		long dataSize=getContentLength();
		try {
			byte[] buffer = new byte[10*1024];
			ips = new ByteArrayInputStream(content);
			int len=0;
			int total=0;
			while((len=ips.read(buffer))!=-1&&!service.isShutdown()){
				outstream.write(buffer, 0, len);
				total+=len;
				//发送进度
				Message msg = handler.obtainMessage();
				msg.what=PicActivity.UPLOAD_FLAG;
				msg.arg1=(int)dataSize;//总数据大小 long转为了int，即不能超过2147483647，2g大小
				msg.arg2=total;
				handler.sendMessage(msg);
				Log.e("food", ""+total);
				
				Thread.sleep(500);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(ips!=null)ips.close();
			if(outstream!=null)outstream.close();
		}
		
	}
	
	@Override
	public InputStream getContent() {
		return super.getContent();
	}
	
	@Override
	public long getContentLength() {
		long len = super.getContentLength();
		return len;
	}
}
