package sp.phone.utils;

import gov.anzong.androidnga.activity.MyApp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import sp.phone.bean.ArticlePage;

public class HttpUtil {

	public final static String PATH_OLD = android.os.Environment
			.getExternalStorageDirectory()
			+ "/nga_cache";
	public static String PATH_AVATAR_OLD = PATH_OLD +
			 "/nga_cache";
	public static String PATH_IMAGES = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath()+"/Pictures";
	
	public static String PATH = android.os.Environment
			.getExternalStorageDirectory()
			+ "/nga_cache";
	public static String PATH_AVATAR = PATH +
			 "/nga_cache";
	public static String PATH_NOMEDIA = PATH + "/.nomedia";
	

	public static  final String PATH_ZIP = "";

	public static String Server = "http://bbs.ngacn.cc";
	public static final String NGA_ATTACHMENT_HOST = "img6.ngacn.cc";
	private static final String servers[] = {"http://bbs.ngacn.cc","http://bbs.ngacn.cc"};
	private static final String TAG = HttpUtil.class.getSimpleName();
	/*private static String[] host_arr = { "http://aa121077313.gicp.net:8099",
			"http://aa121077313.gicp.net:8098", "http://10.0.2.2:8099",
			"http://10.0.2.2:8098" };*/
	private static final String[] host_arr = {};

	public static String HOST = "";
	public static final String Servlet_phone = "/servlet/PhoneServlet";
	public static final String Servlet_timer = "/servlet/TimerServlet";

	public static String HOST_PORT = "";
	//public static String USER_AGENT = 
	//		"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.30 Safari/536.5";
	//软件名/版本 (硬件信息; 操作系统信息)
	//AndroidNga/571 (Xiaomi MI 2S; Android 4.1.1)
	public static final String USER_AGENT = new StringBuilder().append("AndroidNgaOfAnzong/").append(MyApp.version).append(" (").append(android.os.Build.MANUFACTURER).append(" ").append(android.os.Build.MODEL).append("; Android").append(android.os.Build.VERSION.RELEASE).append(")").toString();
//	public static final String USER_AGENT = new StringBuilder().append("Mozilla/5.0 (Linux; U; Android 2.3.3; zh-cn; SH12C Build/S4040) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1").toString();
	public static void selectServer2() {
		for (String host : host_arr) {
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) new URL(host).openConnection();
				conn.setConnectTimeout(6000);
				int result = conn.getResponseCode();
				String re = conn.getResponseMessage();
				if (result == HttpURLConnection.HTTP_OK) {
					System.out.println(re);
					System.out.println(host + "ok !");
					HOST = host;//
					break;
				} else {
					System.out.println(host + "fail !");
				}
			} catch (MalformedURLException e) {
				System.out.println(e);
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			}

		}
	}
	public static void switchServer(){
		int i = 0;
		for(; i< servers.length; ++i){
			if(Server.equals(servers[i]))
				break;
		}
		i = (i+1)%servers.length;
		Server = servers[i];
	}
	public static boolean selectServer() {
		boolean status = false;
		for (String host : host_arr) {
			String str = host + Servlet_phone;
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) new URL(str).openConnection();
				conn.setConnectTimeout(4000);
				int result = conn.getResponseCode();
				if (result == HttpURLConnection.HTTP_OK) {
					System.out.println(host + "ok !");
					HOST = str;//
					HOST_PORT = host;
					status = true;
					break;
				} else {
					System.out.println(host + "fail !");
				}
			} catch (MalformedURLException e) {
				System.out.println(e);
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			}
		}
		return status;
	}

	public static void downImage(String uri, String fileName) {
		try {
			URL url = new URL(uri);
			File file = new File(fileName);
			
			FileUtils.copyURLToFile(url, file, 2000, 5000);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(TAG, "failed to download img:" + uri+ "," + e.getMessage());
		}
	}

	public static InputStream downImage2(String uri, String fileName) {
		InputStream is = null;
		try {
			URL url = new URL(uri);
			is = url.openStream();
			File file = new File(fileName);
			FileUtils.copyInputStreamToFile(is, file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
		return is;
	}

	public static InputStream imageInputStream2(String uri,
			final String newFileName) {

		try {
			URL url = new URL(uri);
			URLConnection conn = url.openConnection();
			final InputStream is = conn.getInputStream();
			// InputStream is2 = is;
			System.out.println("get image is");
			new Thread() {
				public void run() {
					System.out.println("write image");
					writeFile(is, newFileName);
				};
			}.start();
			return is;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static void writeFile(InputStream is, String fileName) {
		try {
			FileUtils.copyInputStreamToFile(is, new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void writeFile(URL url, String fileName) {
		try {
			FileUtils.copyURLToFile(url, new File(fileName), 4000, 3000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getHtml(String uri, String cookie) {
		InputStream is = null;
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if(!StringUtil.isEmpty(cookie))
				conn.setRequestProperty("Cookie", cookie);
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Accept-Charset", "GBK");
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.setConnectTimeout(8000);
			conn.setReadTimeout(8000);
			conn.connect();
            if(conn.getResponseCode() == 200)
			    is = conn.getInputStream();
            else
                is = conn.getErrorStream();
			if( "gzip".equals(conn.getHeaderField("Content-Encoding")) )
				is = new GZIPInputStream(is);
			String encoding =  getCharset( conn, "GBK");
			return IOUtils.toString(is, encoding);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
		return null;
	}
	
	public static String iosGetHtml(String uri, String cookie) {
		InputStream is = null;
		final String ios_ua = "Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if(!StringUtil.isEmpty(cookie))
				conn.setRequestProperty("Cookie", cookie);
			conn.setRequestProperty("User-Agent", ios_ua);
			conn.setRequestProperty("Accept-Charset", "GBK");
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.setConnectTimeout(8000);
			conn.setReadTimeout(8000);
			conn.connect();
			is = conn.getInputStream();
			if( "gzip".equals(conn.getHeaderField("Content-Encoding")) )
				is = new GZIPInputStream(is);
			String encoding =  getCharset( conn, "GBK");
			return IOUtils.toString(is, encoding);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
		return null;
	}
	
	public static String getHtml(String uri, String cookie,String host,int timeout) {
		InputStream is = null;
		HostnameVerifier allHostsValid = new HostnameVerifier() {

			@Override
			public boolean verify(String hostname, SSLSession session) {
				// TODO Auto-generated method stub
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if(!StringUtil.isEmpty(cookie))
				conn.setRequestProperty("Cookie", cookie);
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Accept-Charset", "GBK");
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			if(!StringUtil.isEmpty(host)){
				conn.setRequestProperty("Host", host);
			}
			if(timeout>0){
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout*2);
			}
			
			conn.connect();
			is = conn.getInputStream();
			if( "gzip".equals(conn.getHeaderField("Content-Encoding")) )
				is = new GZIPInputStream(is);
			String encoding =  getCharset( conn, "GBK");
			
			return IOUtils.toString(is, encoding);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
		return null;
	}
	private static String getCharset(HttpURLConnection conn, String defaultValue){
		if(conn== null)
			return defaultValue;
		String contentType = conn.getHeaderField("Content-Type");
		if(StringUtil.isEmpty(contentType))
			return defaultValue;
		String startTag = "charset=";
		String endTag = " ";
		int start = contentType.indexOf(startTag);
		if( -1 == start)
			return defaultValue;
		start += startTag.length();
		int end = contentType.indexOf( endTag, start);
		if(-1==end)
			end = contentType.length();
		
		return contentType.substring(start, end);
	}
	public static  ArticlePage getArticlePage(String uri, String cookie) {
		ArticlePage ret= null;
		try {
			long start = System.currentTimeMillis();
			String html = getHtml(uri,cookie);
			long end  = System.currentTimeMillis();
			Log.i("ArticlePage","network const:" + (end-start));
			 ret = ArticleUtil.parserArticleList(html);
			 long end2  = System.currentTimeMillis(); 
			 Log.i("ArticlePage","parse action const:" + (end2-end));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static ArticlePage getArticlePageByJson(String uri) {
		System.out.println("from this");
		//TODO
		String json = getHtml(uri,"");
		long t = System.currentTimeMillis();
		ArticlePage ap = JSON.parseObject(json, ArticlePage.class);
		System.out.println(System.currentTimeMillis() - t);
		return ap;
	}
}
