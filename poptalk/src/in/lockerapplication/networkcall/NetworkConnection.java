package in.lockerapplication.networkcall;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;


public class NetworkConnection 
{	
	public static String networkhitforpost(List<NameValuePair> pairs , String url){
		Log.e("","Post url---> "+url);
		Log.e("","Post Params---> "+pairs.toString());
		String responseEntity = null;
		try{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,10000); //10000
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost post= new HttpPost(url);
			//			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(new UrlEncodedFormEntity(pairs,"UTF-8"));	
			Log.e("SERVER RESPONSE","http post : "+post);
			HttpResponse response = client.execute(post); 
			responseEntity = EntityUtils.toString(response.getEntity());
			Log.e("SERVER RESPONSE","SERVER RESPONSE : "+responseEntity);
		}
		catch (Throwable e) 
		{
			responseEntity=null;
			e.printStackTrace();
		}
		return responseEntity;
	}

	public static String networkHitForGet(String url)
	{
		Log.e("","Get url---> "+url);
		String websiteData = null;
		try{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			URI uri = new URI(url);
			HttpGet method = new HttpGet(uri);
			method.addHeader("Content-Type", "text/html");
			HttpResponse res = client.execute(method);
			InputStream data = res.getEntity().getContent();
			websiteData = generateString1(data);
			Log.e("websiteData",websiteData+"*");
		}catch(ClientProtocolException e){
			e.printStackTrace();
			websiteData = null;
		}catch(IOException e){
			e.printStackTrace();
			websiteData = null;
		}catch(URISyntaxException e){
			e.printStackTrace();
			websiteData = null;
		}
		return websiteData;
	}

	public static String generateString1(InputStream stream)
	{
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader buffer = new BufferedReader(reader);
		StringBuilder sb = new StringBuilder();
		try {
			String cur;
			while ((cur = buffer.readLine()) != null)
			{
				sb.append(cur+"");
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
		}

		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}