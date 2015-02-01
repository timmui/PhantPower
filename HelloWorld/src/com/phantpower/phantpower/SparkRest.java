package com.phantpower.phantpower;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.R.integer;
import android.os.StrictMode;

public class SparkRest {
	public SparkRest() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);
	}

	public int sendToggle() throws Exception {
		URL obj = new URL(Credentials.toggleURL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestMethod("POST");

		con.setDoOutput(true);

		con.getResponseCode();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		int i = 0;
		while ((inputLine = in.readLine()) != null) {
		  response.append(inputLine);
		}
		in.close();
		
		String responseStr = response.toString();
		
		return Integer.parseInt(responseStr.substring(responseStr.length()-2, responseStr.length()-1));
	}
	
	public int sendOn() throws Exception {
		URL obj = new URL(Credentials.onURL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestMethod("POST");

		con.setDoOutput(true);

		con.getResponseCode();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		int i = 0;
		while ((inputLine = in.readLine()) != null) {
		  response.append(inputLine);
		}
		in.close();
		
		String responseStr = response.toString();
		
		return Integer.parseInt(responseStr.substring(responseStr.length()-2, responseStr.length()-1));
	} 
	public int sendOff() throws Exception {
		URL obj = new URL(Credentials.offURL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestMethod("POST");

		con.setDoOutput(true);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		int i = 0;
		while ((inputLine = in.readLine()) != null) {
		  response.append(inputLine);
		}
		in.close();
		
		String responseStr = response.toString();
		
		return Integer.parseInt(responseStr.substring(responseStr.length()-2, responseStr.length()-1));
	} 
	public int getStatus() throws Exception {
		URL obj = new URL(Credentials.statusURL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestMethod("POST");

		con.setDoOutput(true);

		con.getResponseCode();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		int i = 0;
		while ((inputLine = in.readLine()) != null) {
		  response.append(inputLine);
		}
		in.close();
		
		String responseStr = response.toString();
		
		return Integer.parseInt(responseStr.substring(responseStr.length()-2, responseStr.length()-1));
	} 
}