package ro.pub.cs.systems.pdsd.lab07.calculatorwebservice.graphicaluserinterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import ro.pub.cs.systems.pdsd.lab07.calculatorwebservice.R;
import ro.pub.cs.systems.pdsd.lab07.calculatorwebservice.general.Constants;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CalculatorWebServiceActivity extends Activity {
	
	private EditText operator1EditText, operator2EditText;
	private TextView resultTextView;
	private Spinner operationsSpinner, methodsSpinner;
	
	private class CalculatorWebServiceThread extends Thread {
		
		@Override
		public void run() {
			
			// TODO: exercise 4
			// get operators 1 & 2 from corresponding edit texts (operator1EditText, operator2EditText)
			final String err;
			if(operator1EditText.getText().toString().matches("") || operator2EditText.getText().toString().matches("")) {
				err = "Operatorii nu pot fi null.";
				resultTextView.post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						resultTextView.setText(err);
					}
					
				});
				return;
			}
			Integer op1, op2;
			op1 = Integer.parseInt(operator1EditText.getText().toString());
			op2 = Integer.parseInt(operator2EditText.getText().toString());
			// signal missing values through error messages
			// get operation from operationsSpinner
			// create an instance of a HttpClient object
			String operation = operationsSpinner.getSelectedItem().toString();
			String method = methodsSpinner.getSelectedItem().toString();
			
			
			// get method used for sending request from methodsSpinner
			final String res;
			HttpClient httpClient = new DefaultHttpClient();
			// 1. GET
			if(method.matches("GET")){ 
				// a) build the URL into a HttpGet object (append the operators / operations to the Internet address)
				// b) create an instance of a ResultHandler object
				// c) execute the request, thus generating the result
				try{
					
					HttpGet httpGet = new HttpGet(Constants.GET_WEB_SERVICE_ADDRESS
                        + "?" + Constants.OPERATION_ATTRIBUTE + "=" + operation
                        + "&" + Constants.OPERATOR1_ATTRIBUTE + "=" + op1
                        + "&" + Constants.OPERATOR2_ATTRIBUTE + "=" + op2);
					HttpResponse httpGetResponse = httpClient.execute(httpGet);
					HttpEntity httpGetEntity = httpGetResponse.getEntity();
					  if (httpGetEntity != null) {  
					    // do something with the response
					    res = EntityUtils.toString(httpGetEntity);
					    resultTextView.post(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								resultTextView.setText(res);
							}
							
						});
					    
					    
					  }
				} catch (Exception e) {
					Log.e(Constants.TAG, e.getMessage());
					if (Constants.DEBUG) {
						e.printStackTrace();
					}
				}
			} else {
				// 2. POST
				// a) build the URL into a HttpPost object
				// b) create a list of NameValuePair objects containing the attributes and their values (operators, operation)
				// c) create an instance of a UrlEncodedFormEntity object using the list and UTF-8 encoding and attach it to the post request
				// d) create an instance of a ResultHandler object
				// e) execute the request, thus generating the result
				HttpPost httpPost = new HttpPost(Constants.POST_WEB_SERVICE_ADDRESS);
				List<NameValuePair> params = new ArrayList<NameValuePair>();        
				params.add(new BasicNameValuePair(Constants.OPERATION_ATTRIBUTE, operationsSpinner.getSelectedItem().toString()));
				params.add(new BasicNameValuePair(Constants.OPERATOR1_ATTRIBUTE, op1.toString()));
				params.add(new BasicNameValuePair(Constants.OPERATOR2_ATTRIBUTE, op2.toString()));
				try {
				  UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				  httpPost.setEntity(urlEncodedFormEntity);
				  try {
				  HttpResponse httpPostResponse = httpClient.execute(httpPost);  
				  HttpEntity httpPostEntity = httpPostResponse.getEntity();  
				  if (httpPostEntity != null) {
				    // do something with the response
					  res = EntityUtils.toString(httpPostEntity);
					    resultTextView.post(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								resultTextView.setText(res);
							}
							
						});
				   }
				  
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
				  Log.e(Constants.TAG, unsupportedEncodingException.getMessage());
				  if (Constants.DEBUG) {
				    unsupportedEncodingException.printStackTrace();
				  }						
				}
				}catch (Exception e) {
					Log.e(Constants.TAG, e.getMessage());
					  if (Constants.DEBUG) {
					    e.printStackTrace();
					  }
				}
			}
			// display the result in resultTextView
			
		}
	}
	
	private DisplayResultButtonClickListener displayResultButtonClickListener = new DisplayResultButtonClickListener();
	private class DisplayResultButtonClickListener implements Button.OnClickListener {
		
		@Override
		public void onClick(View view) {
			new CalculatorWebServiceThread().start();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculator_web_service);
		
		operator1EditText = (EditText)findViewById(R.id.operator1_edit_text);
		operator2EditText = (EditText)findViewById(R.id.operator2_edit_text);
		
		resultTextView = (TextView)findViewById(R.id.result_text_view);
		
		operationsSpinner = (Spinner)findViewById(R.id.operations_spinner);
		methodsSpinner = (Spinner)findViewById(R.id.methods_spinner);
		
		Button displayResultButton = (Button)findViewById(R.id.display_result_button);
		displayResultButton.setOnClickListener(displayResultButtonClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calculator_web, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
