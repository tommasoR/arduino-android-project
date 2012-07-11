/**
 * code from the book Getting Started with IOIO
 * <br>Copyright 2011 Simon Monk
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation (see COPYING).
 * 
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */


package com.ioiobook.alarm;


import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalInput.Spec.Mode;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AbstractIOIOActivity implements OnCheckedChangeListener {
	
	private static final int PIR_PIN = 48;
	private static final int LED_PIN = 0;
	
	private static final String PREFS_NAME = "IOIOIntruder";
	private TextView sms_;
	private TextView message_;
	private ToggleButton testButton_;
	private ToggleButton runButton_;
	
	long startTime_ = 0;
	

	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        sms_ = (TextView)findViewById(R.id.sms);
        message_ = (TextView)findViewById(R.id.message);
        testButton_ = (ToggleButton)findViewById(R.id.testButton);
        testButton_.setChecked(true);
        runButton_ = (ToggleButton)findViewById(R.id.runButton);
        sms_.setText(settings.getString("sms", ""));   
        message_.setText(settings.getString("message", "Something Moved!"));
        runButton_.setOnCheckedChangeListener(this);
	}

	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		private DigitalInput pir_;
		private DigitalOutput led_;
		private long lastTime = 0;
		private int movementCount = 0;

		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			toast("HERE");
			pir_ = ioio_.openDigitalInput(PIR_PIN, Mode.FLOATING);
			led_ = ioio_.openDigitalOutput(LED_PIN);
		}

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * @throws InterruptedException 
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		 */
		@Override
		protected void loop() throws ConnectionLostException {
			boolean wasMovement = false;
			try {
				wasMovement = ! pir_.read();;
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block 
				e1.printStackTrace();
			}
			led_.write(! wasMovement); // LED false = on
			if (wasMovement) {
				movementCount ++;
			}
			long now = System.currentTimeMillis();
			if (now > lastTime + 1000) {
				// every second
				lastTime = now;
				if (movementCount > 50) {
					if (now > startTime_ + 10000 && runButton_.isChecked()) {
						handleAlarm();						
					}
				}
				movementCount = 0;
			}
			try {
				sleep(10);
			} catch (InterruptedException e) {
			}
		}

		private void handleAlarm() {
			if (testButton_.isChecked()) {
				toast("Test Mode, no SMS sent");
			}
			else {
				sendSMS();
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					runButton_.setChecked(false);
				}
			});
		}

		private void sendSMS() {
			String number = sms_.getText().toString();
			String message = message_.getText().toString();
			Context context = getApplicationContext();
			PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);                
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(number, null, message, pi, null);  
			toast("SMS Sent");
		}
	}

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}
	
	private void toast(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, message, duration);
				toast.show();
			}
		});


	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			toast("You have 10 seconds before sensing starts");
			startTime_ = System.currentTimeMillis();
		}
		// save the fields in prefs so they are there next time
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString("sms", sms_.getText().toString());
	    editor.putString("message", message_.getText().toString());
	    editor.commit();
	}
}