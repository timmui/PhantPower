package com.thalmic.android.sample.helloworld;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.os.StrictMode;
import android.util.Log;

public class Yo {
	private static String server = "https://api.justyo.co/yo/";
	private static String apikey;
	
	public Yo(String apikey) {
		this.apikey = apikey;
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
	}

	public void sendYo() throws Exception {
		URL obj = new URL(server);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestMethod("POST");

		String urlParameters = "api_token="+apikey+"&username=THEBLNAKNESS";

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
		  response.append(inputLine);
		}
		in.close();
	}
}