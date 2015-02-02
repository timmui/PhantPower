package com.phantpower.phantpower;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.phantpower.phantpower.R;

public class MainActivity extends Activity {
	
	private static final UUID WATCHAPP_UUID = UUID.fromString("6092637b-8f58-4199-94d8-c606b1e45040");
	private static final String WATCHAPP_FILENAME = "android-example.pbw";
	
	private static final int
		KEY_BUTTON = 0,
		KEY_VIBRATE = 1,
		BUTTON_UP = 0,
		BUTTON_SELECT = 1,
		BUTTON_DOWN = 2;
	
	private Handler handler = new Handler();
	private PebbleDataReceiver appMessageReciever;
	private TextView whichButtonView;
	private TextView batteryInfo;
	private TextView bound;
	private ImageView imageBatteryState;
	private SparkRest spark;
	NumberPicker np;
	NumberPicker np1;
	
	int up = 90;
	int down = 80;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		spark = new SparkRest();

		np = (NumberPicker) findViewById(R.id.numPick);
		np1 = (NumberPicker) findViewById(R.id.numPick1);
		
		np.setMinValue(0);
        np.setMaxValue(100);
        np.setWrapSelectorWheel(true); 
        
        np1.setMinValue(0);
        np1.setMaxValue(100);
        np1.setWrapSelectorWheel(true); 
        
		bound = (TextView) findViewById(R.id.bounds);
		bound.setText(String.format("%d - %d",down,up));
        
        //Add the confirm button
        Button confirmButton = (Button)findViewById(R.id.pick);
		confirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (np1.getValue() > np.getValue())
				{
					down = np.getValue();
					up = np1.getValue();
				
					bound.setText(String.format("%d - %d",down,up));
				}
				else
				{
					Toast toast = Toast.makeText(getApplicationContext(), "Invalid Entry!", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
			
		});
        
		
		
        batteryInfo=(TextView)findViewById(R.id.textViewBatteryInfo);
        this.registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        
		// Customize ActionBar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("PhantPower");
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_orange)));
		
		// Add Install Button behavior
		Button installButton = (Button)findViewById(R.id.button_install);
		installButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Install 
				Toast.makeText(getApplicationContext(), "Installing watchapp...", Toast.LENGTH_SHORT).show();
				sideloadInstall(getApplicationContext(), WATCHAPP_FILENAME);
			}
			
		});
		// Add output TextView behavior
		
		
	}
	
	
	
	private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
			int  plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
			
			
			if (level > up && plugged > 0)
			{
				throwOff();
			}
			if (level < down)
			{
				throwOn();
			}
			
			//higher
			
			batteryInfo.setText("Level: "+level+"\n"+
								"Plugged: "+plugged);
		}
	};
	
	public void vib() {
		// Send KEY_VIBRATE to Pebble
		PebbleDictionary out = new PebbleDictionary();
		out.addInt32(KEY_VIBRATE, 0);
		PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, out);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Define AppMessage behavior
		if(appMessageReciever == null) {
			appMessageReciever = new PebbleDataReceiver(WATCHAPP_UUID) {
				
				@Override
				public void receiveData(Context context, int transactionId, PebbleDictionary data) {
					// Always ACK
					PebbleKit.sendAckToPebble(context, transactionId);
					
					// What message was received?
					if(data.getInteger(KEY_BUTTON) != null) {
						// KEY_BUTTON was received, determine which button
						final int button = data.getInteger(KEY_BUTTON).intValue();
						
						// Update UI on correct thread
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								switch(button) {
								case BUTTON_UP:
									
									throwOn();
									
									break;
								case BUTTON_SELECT:
									
									throwToggle();
									
									break;
								case BUTTON_DOWN:
									
									throwOff();
									
									break;
								default:
									
									throwToggle();
									
									break;
								}
							}
							
						});
					} 
				}
			};
		
			// Add AppMessage capabilities
			PebbleKit.registerReceivedDataHandler(this, appMessageReciever);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// Unregister AppMessage reception
		if(appMessageReciever != null) {
			unregisterReceiver(appMessageReciever);
			appMessageReciever = null;
		}
	}
	
	/**
     * Alternative sideloading method
     * Source: http://forums.getpebble.com/discussion/comment/103733/#Comment_103733 
     */
    public static void sideloadInstall(Context ctx, String assetFilename) {
        try {
            // Read .pbw from assets/
        	Intent intent = new Intent(Intent.ACTION_VIEW);    
            File file = new File(ctx.getExternalFilesDir(null), assetFilename);
            InputStream is = ctx.getResources().getAssets().open(assetFilename);
            OutputStream os = new FileOutputStream(file);
            byte[] pbw = new byte[is.available()];
            is.read(pbw);
            os.write(pbw);
            is.close();
            os.close();
             
            // Install via Pebble Android app
            intent.setDataAndType(Uri.fromFile(file), "application/pbw");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(ctx, "App install failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    //throwstuff here

    private void throwToggle(){
    	int duration = Toast.LENGTH_SHORT;
    	int result;
    	Toast toast;
    	
    	try {
			Log.d("REST","Run Toggle");
			
			result = spark.sendToggle();
			
			if (result == 0){
				toast = Toast.makeText(getApplicationContext(), "Unsuccessful. Please Try Again.", duration);
				toast.show();
			}
			else if (result == 1){
				toast = Toast.makeText(getApplicationContext(), "Success!", duration);
				toast.show();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			toast = Toast.makeText(getApplicationContext(), "Error: Please Try Again", duration);
			toast.show();
			Log.d("REST",String.format("REST exception: %s",e));
			e.printStackTrace();
		}
    	
    	
    }
    private void throwOn(){
    	int duration = Toast.LENGTH_SHORT;
    	int result;
    	Toast toast;
    	
    	try {
			Log.d("REST","Run Toggle");
			
			result = spark.sendOn();
			
			if (result == 0){
				toast = Toast.makeText(getApplicationContext(), "Unsuccessful. Please Try Again.", duration);
				toast.show();
			}
			else if (result == 1){
				toast = Toast.makeText(getApplicationContext(), "Success!", duration);
				toast.show();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			toast = Toast.makeText(getApplicationContext(), "Error: Please Try Again", duration);
			toast.show();
			Log.d("REST",String.format("REST exception: %s",e));
			e.printStackTrace();
		}
    	
    }
    private void throwOff(){
    	int duration = Toast.LENGTH_SHORT;
    	int result;
    	Toast toast;
    	
    	try {
			Log.d("REST","Run Toggle");
			
			result = spark.sendOff();
			
			if (result == 0){
				toast = Toast.makeText(getApplicationContext(), "Unsuccessful. Please Try Again.", duration);
				toast.show();
			}
			else if (result == 1){
				toast = Toast.makeText(getApplicationContext(), "Success!", duration);
				toast.show();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			toast = Toast.makeText(getApplicationContext(), "Error: Please Try Again", duration);
			toast.show();
			Log.d("REST",String.format("REST exception: %s",e));
			e.printStackTrace();
		}
    	
    }
}
