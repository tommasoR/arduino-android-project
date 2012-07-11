package com.example.hh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "MyProgram_hh";


	@Override
	public void onReceive(Context context, Intent intent) {
		try {
	          if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
	        	  Log.d(TAG, "Notified of boot");
	           }
	          Intent startServiceIntent = new Intent(context, MainActivity.class);
	          
	  		  startServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
	  		  //context.startService(startServiceIntent);
	  		context.startActivity(startServiceIntent);
	    } catch (Exception e) {
	     Log.d(TAG, "An alarm was received but there was an error");
	     e.printStackTrace();
	     }
	    }

		


}
