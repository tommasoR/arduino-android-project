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

package com.ioiobook.matrix;

import org.hermit.android.io.AudioReader;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class MainActivity extends AbstractIOIOActivity implements OnClickListener {

	public final static int AUDIO_BUFFER_SIZE = 256;
	public final static int F = 16000;

	private Button test1Button_;
	private Button test2Button_;
	private Button test3Button_;
	private Button animationButton_;
	private Button spectrumButton_;

	private AudioReader audioReader_;
	private SpectrumDrawer spectrumDrawer_;

	private final int IMAGE = 0;
	private final int ANIMATION = 1;
	private final int SPECTRUM = 2;

	private int state_ = ANIMATION;

	private long lastFrameChange_ = 0;
	private int lastFrame_ = 0;

	private int[][] display_;

	private int[][] spectrum_ = {
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0}
	};


	private int[][] testPattern1_ = {
			{1,1,1,1,1,1,1,1},
			{1,2,2,2,2,2,2,2},
			{1,2,3,3,3,3,3,3},
			{1,2,3,1,1,1,1,1},
			{1,2,3,1,2,2,2,2},
			{1,2,3,1,2,3,3,3},
			{1,2,3,1,2,3,1,1},
			{1,2,3,1,2,3,1,2}
	};

	private int[][] testPattern2_ = {
			{0,0,0,0,0,0,0,0},
			{0,0,1,1,1,1,0,0},
			{0,1,1,1,1,1,1,0},
			{1,1,2,1,1,2,1,1},
			{1,1,1,1,1,1,1,1},
			{0,0,3,0,0,3,0,0},
			{0,3,0,0,0,0,3,0},
			{0,0,3,0,0,3,0,0}
	};

	private int[][] testPatternIOIO_ = {
			{2,0,0,0,2,0,0,0},
			{2,0,0,0,2,0,0,0},
			{2,0,0,0,2,0,0,0},
			{2,0,1,0,2,0,1,0},
			{2,1,0,1,2,1,0,1},
			{2,1,0,1,2,1,0,1},
			{2,1,0,1,2,1,0,1},
			{2,0,1,0,2,0,1,0},
	};

	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		test1Button_ = (Button)findViewById(R.id.test1Button);
		test1Button_.setOnClickListener(this);
		test2Button_ = (Button)findViewById(R.id.test2Button);
		test2Button_.setOnClickListener(this);
		test3Button_ = (Button)findViewById(R.id.test3Button);
		test3Button_.setOnClickListener(this);
		animationButton_ = (Button)findViewById(R.id.animationButton);
		animationButton_.setOnClickListener(this);
		spectrumButton_ = (Button)findViewById(R.id.spectrumButton);
		spectrumButton_.setOnClickListener(this);

		display_ = testPattern2_;

		spectrumDrawer_ = new SpectrumDrawer(spectrum_);

		AudioReader.Listener listener = new AudioReader.Listener()
		{

			@Override
			public void onReadComplete(short[] buffer) {
				spectrumDrawer_.calculateSpectrum(buffer);
			}

			@Override
			public void onReadError(int error) {

			}

		};
		audioReader_ = new AudioReader();
		audioReader_.startReader(F, AUDIO_BUFFER_SIZE, listener);
	 }

	 /**
	  * This is the thread on which all the IOIO activity happens. It will be run
	  * every time the application is resumed and aborted when it is paused. The
	  * method setup() will be called right after a connection with the IOIO has
	  * been established (which might happen several times!). Then, loop() will
	  * be called repetitively until the IOIO gets disconnected.
	  */
	 class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		 /** The on-board LED. */

		 private DigitalOutput[] cc = new DigitalOutput[8];
		 private int[] ccPins = {1, 2, 17, 18, 6, 5, 4, 3};

		 private DigitalOutput[] g = new DigitalOutput[8];
		 private int[] greenPins = {35, 36, 37, 38, 39, 40, 41, 42};

		 private DigitalOutput[] r = new DigitalOutput[8];
		 private int[] redPins = {43, 44, 45, 46, 47, 48, 33, 34};

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
			 for (int i = 0; i < 8; i++)
			 {
				 g[i] = ioio_.openDigitalOutput(greenPins[i]);
				 r[i] = ioio_.openDigitalOutput(redPins[i]);
				 cc[i] = ioio_.openDigitalOutput(ccPins[i]);
				 cc[i].write(false);
			 }
		 }

		 /**
		  * Called repetitively while the IOIO is connected.
		  * 
		  * @throws ConnectionLostException
		  *             When IOIO connection is lost.
		  * 
		  * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		  */
		 @Override
		 protected void loop() throws ConnectionLostException {
			 if (state_ == ANIMATION) {
				 animate();
			 } 
			 else if (state_ == IMAGE) {
				 refreshMatrix();
			 }
			 else if (state_ == SPECTRUM) {
				 refreshMatrix();
			 }
		 }


		 private void animate() throws ConnectionLostException {
			 int n = TestAnimation.animation.length;
			 int delay = TestAnimation.frameDelay;
			 long now = System.currentTimeMillis();
			 refreshMatrix();
			 if (now > lastFrameChange_ + delay) {
				 display_ = TestAnimation.animation[lastFrame_];
				 lastFrame_ ++;
				 lastFrameChange_ = now;
				 if (lastFrame_ == n) lastFrame_ = 0;
			 }
		 }

		 private void refreshMatrix() throws ConnectionLostException {
			 for (int col = 0; col < 8; col++) {
				 clearPreviousColumn(col);
				 displayColumn(col);
				 delay(3);
			 }
		 }

		 private void clearPreviousColumn(int col) throws ConnectionLostException {
			 int columnToClear = col - 1;
			 if (columnToClear == -1)
			 {
				 columnToClear = 7;
			 }
			 cc[columnToClear].write(false);
			 for (int row = 0; row < 8; row++) {
				 r[row].write(false);
				 g[row].write(false);
			 }
		 }

		 private void displayColumn(int col) throws ConnectionLostException {
			 cc[col].write(true);
			 for (int row = 0; row < 8; row++) {
				 r[row].write((display_[col][row] & 1) > 0);
				 g[row].write((display_[col][row] & 2) > 0);
			 }
		 }

		 private void delay(long millis) {
			 try {
				 sleep(millis);
			 } catch (InterruptedException e) {
			 }
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

	 @Override
	 public void onClick(View v) {
		 if (v == test1Button_){
			 display_ = testPattern1_;
			 state_ = IMAGE;
		 }
		 else if (v == test2Button_) {
			 display_ = testPattern2_;
			 state_ = IMAGE;
		 }
		 else if (v == test3Button_) {
			 display_ = testPatternIOIO_;
			 state_ = IMAGE;
		 }
		 else if (v == animationButton_) {
			 state_ = ANIMATION;
		 }
		 else if (v == spectrumButton_) {
			 display_ = spectrum_;
			 state_ = SPECTRUM;
		 }
	 }

	 public void alert(String message)
	 {
		 AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		 alertDialog.setTitle("Alert");
		 alertDialog.setMessage(message);
		 alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface dialog, int which) {
				 return;
			 } 
		 }); 
		 alertDialog.show();
	 }
}