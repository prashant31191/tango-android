package or.tango.android.util;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpUtil {
	private static final String CHARSETNAME = "UTF-8";

	public static String sendRequest(String httpUrl, Map<String, String> headerParams, Map<String, Object> httpParams, String httpMethod) {
		if ((httpMethod != null & httpMethod.toLowerCase().endsWith("get")))
			return doGET(httpUrl, headerParams, httpParams);
		if ((httpMethod != null & httpMethod.toLowerCase().endsWith("post"))) {
			return doPOST(httpUrl, headerParams, httpParams);
		}

		return null;
	}

	private static String doGET(String httpUrl, Map<String, String> headerParams, Map<String, Object> httpParams) {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			StringBuffer params = new StringBuffer("");
			if ((httpParams != null) && (httpParams.size() != 0)) {
				Set<Entry<String, Object>> entrySet = httpParams.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					params.append("&" + (String) entry.getKey() + "=" + (String) entry.getValue());
				}
			}
			HttpGet httpget = new HttpGet();
			System.out.println("GET请求URL---------------------》" + httpUrl + params);
			URI uri = new URI(httpUrl + params);
			httpget.setURI(uri);
			if ((headerParams != null) && (headerParams.size() != 0)) {
				Set<Entry<String, String>> entrySet = headerParams.entrySet();
				for (Entry<String, String> entry : entrySet) {
					httpget.setHeader((String) entry.getKey(), (String) entry.getValue());
				}
			}
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity resEntity = response.getEntity();
			String str = EntityUtils.toString(resEntity,"utf-8");
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}

	private static String doPOST(String httpUrl, Map<String, String> headerParams, Map<String, Object> httpParams) {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			URI uri = new URI(httpUrl);
			HttpPost httppost = new HttpPost(uri);
			httppost.addHeader("charset", CHARSETNAME);
			
			if ((headerParams != null) && (headerParams.size() != 0)) {
				Set<Entry<String, String>> entrySet = headerParams.entrySet();
				for (Entry<String, String> entry : entrySet) {
					httppost.setHeader((String) entry.getKey(), (String) entry.getValue());
				}
			}
			List<BasicNameValuePair> list  = new ArrayList<BasicNameValuePair>();
			
			if ((httpParams != null) && (httpParams.size() != 0)) {
				Set<Entry<String, Object>> entrySet = httpParams.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					if ((entry.getValue() instanceof String)) {
						list.add(new BasicNameValuePair(entry.getKey(), (String)entry.getValue()));
					} else if ((entry.getValue() instanceof File)) {
						//reqEntity.addPart((String) entry.getKey(), new FileBody((File) entry.getValue()));
					} else {
						return "未设定的参数类型";
					}
				}
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,"UTF-8");
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			String str1 = EntityUtils.toString(resEntity);
			return str1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
	
	private static String doPOST2(String url, Map<String,String> httpParams){
		HttpClient mHttpClient = createHttpClient();;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Iterator<String> iterator = httpParams.keySet().iterator();
        while(iterator.hasNext()){
        	String key = iterator.next();
        	params.add(new BasicNameValuePair(key,httpParams.get(key)));
        }
        HttpPost post = new HttpPost(url);
        try{
        	post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
        	HttpResponse httpResponse = mHttpClient.execute(post);
        	String result = EntityUtils.toString(httpResponse.getEntity());
        	return result;
        }catch(Exception e){
        	
        }
        return null;

	}
	
	 private static HttpClient createHttpClient()
	    {
	        HttpParams basichttpparams = new BasicHttpParams();
	        HttpProtocolParams.setVersion(basichttpparams, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(basichttpparams, HTTP.DEFAULT_CONTENT_CHARSET);
	        HttpProtocolParams.setUseExpectContinue(basichttpparams, true);
	        SchemeRegistry schemeregistry = new SchemeRegistry();
	        schemeregistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        schemeregistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
	        ClientConnectionManager connManager = new ThreadSafeClientConnManager(basichttpparams,schemeregistry);
	        return new DefaultHttpClient(connManager, basichttpparams);
	    }
}