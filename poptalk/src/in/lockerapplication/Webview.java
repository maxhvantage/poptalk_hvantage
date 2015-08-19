package in.lockerapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.poptalk.app.R;

public class Webview extends Activity {

	private WebView webView;
	String url1;
	private ProgressBar progressBar;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.webview);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		webView = (WebView) findViewById(R.id.webview1);
		
		Bundle b= getIntent().getExtras();
		url1= b.getString("url");
		webView = (WebView) findViewById(R.id.webview1);
		startWebView(url1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		finish();
	}

	private boolean isRedirected;
	private void startWebView(String url) {

		// Create new webview Client to show progress dialog
		// When opening a url or click on link

		// Javascript inabled on webview
		webView.getSettings().setJavaScriptEnabled(true);

		// Other webview options
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setScrollbarFadingEnabled(false);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int progress) {
				if(progress == 100)
					progressBar.setVisibility(View.GONE);
				else {
					progressBar.setVisibility(View.VISIBLE);
					progressBar.setProgress(progress * 100);
				}
			}
		});
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Log.e("Webview", "errorCode: " + errorCode + ", description: " + description);
				Log.e("Webview", "failingUrl: " + failingUrl);
				view.loadUrl(failingUrl);
			}

			// If you will not use this method url links are opeen in new brower
			// not in webview
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				isRedirected = true;
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);

				webView.setVisibility(View.VISIBLE);

				if (!isRedirected) {
					DownloadTask task = new DownloadTask();
					task.execute();

				}
				isRedirected = false;
			}

			// Show loader on url load
			public void onLoadResource(WebView view, String url) {

			}

			public void onPageFinished(WebView view, String url) {
				try {
					if (!isRedirected) {
						if (progressBar.isShown()) {
							progressBar.setVisibility(View.GONE);
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		});

		// Load url in webview
		webView.loadUrl(url);



	}
	
	
	class DownloadTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			//progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				//progressBar.setVisibility(View.INVISIBLE);
				// textView.setVisibility(View.VISIBLE);
			}
		}
	}

}