package com.example.user.myapplication;

import android.app.ProgressDialog;
import java.io.IOException;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.*;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.Log;

public class HttpServerRequest extends Thread{
	private static final String SERVER_URL = "http://125.131.73.154:8080/AndroidArduinoServer/";
	private static final String COMMAND_KEY = "cmd";

	static private ProgressDialog progressDialog;

	private String servletName;
	private String cmd;
	private Handler handler;
	private List<NameValuePair> nameValuePairs;
	private Context context;
	private boolean showDialog;
	private int what;


	public static boolean isNetworkAvailable(Context context) {
		boolean isNetworkAvailable = false;
		final Activity activity = (Activity)context; 

		ConnectivityManager mgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = mgr.getActiveNetworkInfo();

		if (info != null && info.isConnected())
			isNetworkAvailable = true;
		else {
			
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					activity.finish();
				}
			});
		

			dialog.setTitle("network error");
			dialog.setMessage("Check network connection please");
			dialog.show();
		}

		return isNetworkAvailable;
	}


	public HttpServerRequest(Context context, String servletName, String cmd, ResponseHandler handler, boolean showDialog) {
		this.servletName = servletName;
		this.cmd = cmd;
		this.handler = handler;
		this.context = context;
		this.showDialog = showDialog;
		this.what = 0;
	}

	
	public HttpServerRequest(Context context, String servletName, String cmd, ResponseHandler handler, boolean showDialog, int what) {
		this(context, servletName, cmd, handler, showDialog);
		this.what = what;
	}

	public void setParameter(String name, String value) {
		if (nameValuePairs == null) {
			nameValuePairs = new ArrayList<NameValuePair>(); 
		}

		nameValuePairs.add(new BasicNameValuePair(name, value));
	}

	
	public void submit() {
		if (showDialog) {
			if(progressDialog == null) {

				progressDialog = new ProgressDialog(context);
			}

			if (!progressDialog.isShowing()) {
				progressDialog.setTitle("������ ���� ��");
				//progressDialog.setMessage("Wait");
				progressDialog.show();
			}
		}

		start();
	}

	@Override
	public void run() {
		HttpPost post = new HttpPost(SERVER_URL + servletName + "?" + COMMAND_KEY + "=" + cmd);
		HttpClient client = new DefaultHttpClient();
		String responseStr = "";
		boolean isError = true;

		try {
			if (nameValuePairs != null) {
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
			}

			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			responseStr = EntityUtils.toString(entity);
			isError = false;
		} catch (ClientProtocolException e) {
			Log.e("[HTTPServerRequest.run()", e.getMessage());
		} catch (IOException e) {
			Log.e("[HTTPServerRequest.run()", e.getMessage());
		} catch (Exception e) {
			Log.e("[HTTPServerRequest.run()", e.getMessage());
		}
		finally {
			if (handler != null) {
				Message msg = new Message();
				Bundle bundle = new Bundle();

				bundle.putString("result", responseStr);
				bundle.putInt("what", what);
				bundle.putBoolean("isError", isError);

				msg.setData(bundle);

				handler.sendMessageDelayed(msg, 10);
			} else {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		}
	}

	public abstract static class ResponseHandler extends Handler {
		public String responseString;
		public int what;
		public boolean isError;

		public ResponseHandler() {
			super();
		}

		public JSONArray getJsonArray() {
			try {
				JSONArray jsonArray = new JSONArray(responseString);

				return jsonArray;
			} catch (JSONException e) {
				return null;
			}
		}

		public JSONObject getJsonObject() {
			try {
				JSONObject jsonObject = new JSONObject(responseString);

				return jsonObject;
			} catch (JSONException e) {
				return null;
			}
		}

		public void handleMessage(Message msg)   {
			if (progressDialog != null) { 
				progressDialog.dismiss();
				progressDialog = null;
			}


			responseString = msg.getData().getString("result");
			what = msg.getData().getInt("what");
			isError = msg.getData().getBoolean("isError");

			try {
				responseArrived(getJsonArray(), isError, what);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		};

		public abstract void responseArrived (JSONArray jsonArray, boolean isError, int what) throws JSONException;
	}
}
