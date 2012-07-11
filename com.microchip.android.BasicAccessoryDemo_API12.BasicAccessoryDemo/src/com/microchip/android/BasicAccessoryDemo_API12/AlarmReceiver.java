package com.microchip.android.BasicAccessoryDemo_API12;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {
	private static final String TAG = "BasicAccessoryDemo";

	  @Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
					Log.d(TAG, "Notified of boot");
				}
				Intent newIntent = new Intent(context, BasicAccessoryDemo.class);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(newIntent);
			} catch (Exception e) {
				Log.d(TAG, "An alarm was received but there was an error");
				e.printStackTrace();
			}
		}

}
