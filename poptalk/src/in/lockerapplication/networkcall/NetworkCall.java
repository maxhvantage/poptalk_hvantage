package in.lockerapplication.networkcall;


import in.lockerapplication.bean.CreditResponseBean;
import in.lockerapplication.constants.AppConstants;
import in.lockerapplication.parser.ParserResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ciao.app.constants.AppNetworkConstants;

public class NetworkCall 
{
	private static NetworkCall networkCall;
	private ParserResponse parserResponse;

	/**
	 * Constructor
	 * @param context
	 */
	public NetworkCall(Context context)
	{
		parserResponse = new ParserResponse();
	}

	/**
	 * Static method to find instance of class to use it as singleton
	 * @return
	 */
	public static NetworkCall getNetworkCallInstance(Context context)
	{	
		if(networkCall==null) 
			networkCall=new NetworkCall(context);
		return networkCall;
	}


	/**
	 * Method to hit addCredits web service
	 * @return
	 */
	public CreditResponseBean socialShare(JSONObject json)
	{
		String response=getMessageFromServerForJson(AppNetworkConstants.SOCIAL_SHARE, json);
		return parserResponse.parseCreditData(response);
	}

	/**
	 * Method to hit MessageFromServer webservice
	 * @param json
	 * @return
	 */
	private static String getMessageFromServerForJson(String url,JSONObject json)
	{
		String str = null;
		try
		{
			HttpURLConnection myURLConnection = (HttpURLConnection) new URL(url).openConnection();
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoInput(true);
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(json.toString().getBytes());
			myURLConnection.connect();
			if(myURLConnection.getInputStream()!=null)
			{
				str = convertStreamToString(myURLConnection.getInputStream());
				Log.e("Url--->"+url, "Response-->"+str);
				return str;
			}
		}

		catch (ConnectTimeoutException e) 
		{
			e.printStackTrace();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * Helper Method to  Convert Stream to String
	 * @param json
	 * @return
	 */
	private static String convertStreamToString(InputStream is)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
		}catch (IOException e){
		}finally{
			try {
				is.close();
			}catch (IOException e){
				throw new RuntimeException(e.getMessage());
			}
		}
		return sb.toString();
	}

	public static String networkHitForGet(String url)
	{
		Log.e("","Get url---> "+url);
		String websiteData = null;
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			URI uri = new URI(url);
			HttpGet method = new HttpGet(uri);
			method.addHeader("Content-Type", "text/html");
			HttpResponse res = client.execute(method);
			InputStream data = res.getEntity().getContent();
			websiteData = generateString(data);
			Log.e("response","response---> "+websiteData);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			websiteData = null;
		} catch (IOException e)
		{
			e.printStackTrace();
			websiteData = null;
		} catch (URISyntaxException e)
		{
			e.printStackTrace();
			websiteData = null;
		}
		return websiteData;
	}

	public static String generateString(InputStream stream)
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

	/* Method to hit Chekin web service
	 * @return
	 */
	public CreditResponseBean chekin(JSONObject json)
	{
		String response=getMessageFromServerForJson(AppNetworkConstants.CHECK_IN, json);
		return parserResponse.parseCreditData(response);
	}	
}


