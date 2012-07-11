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

package com.ioiobook.rover;

import android.graphics.Color;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AbstractIOIOActivity {

    private static final int AIN1_PIN = 41;
    private static final int AIN2_PIN = 40;
    private static final int PWMA_PIN = 39;
    private static final int PWMB_PIN = 45;
    private static final int BIN2_PIN = 44;
    private static final int BIN1_PIN = 43;
    private static final int STBY_PIN = 42;
    private static final int LED_PIN = 0;

    private static final int PWM_FREQ = 1000;

    private float left_ = 0;
    private float right_ = 0;

    private RoverControlView roverControlView_;

    /**
     * Called when the activity is first created. Here we normally initialize
     * our GUI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        roverControlView_ = new RoverControlView(this);
        roverControlView_.setBackgroundColor(Color.WHITE);
        setContentView(roverControlView_);
    }

    /**
     * This is the thread on which all the IOIO activity happens. It will be run
     * every time the application is resumed and aborted when it is paused. The
     * method setup() will be called right after a connection with the IOIO has
     * been established (which might happen several times!). Then, loop() will
     * be called repetitively until the IOIO gets disconnected.
     */
    class IOIOThread extends AbstractIOIOActivity.IOIOThread {
        private DigitalOutput ain1_;
        private DigitalOutput ain2_;
        private PwmOutput pwma_;
        private PwmOutput pwmb_;
        private DigitalOutput bin2_;
        private DigitalOutput bin1_;
        private DigitalOutput stby_;

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
            ain1_ = ioio_.openDigitalOutput(AIN1_PIN);
            ain2_ = ioio_.openDigitalOutput(AIN2_PIN);
            pwma_ = ioio_.openPwmOutput(PWMA_PIN, PWM_FREQ);
            pwmb_ = ioio_.openPwmOutput(PWMB_PIN, PWM_FREQ);
            bin2_ = ioio_.openDigitalOutput(BIN2_PIN);
            bin1_ = ioio_.openDigitalOutput(BIN1_PIN);
            stby_ = ioio_.openDigitalOutput(STBY_PIN);
            stby_.write(true); // On
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
            // make a dead off zone in the middle
            if (Math.abs(left_) < 0.2)
                left_ = 0.0f;
            if (Math.abs(right_) < 0.2)
                right_ = 0.0f;

            // make sure duty cycle never > 100%
            if (Math.abs(left_) > 1.0)
                left_ = 1.0f;
            if (Math.abs(right_) > 1.0)
                right_ = 1.0f;

            pwma_.setDutyCycle(Math.abs(left_));
            ain1_.write(left_ >= 0);
            ain2_.write(left_ < 0);

            pwmb_.setDutyCycle(Math.abs(right_));
            bin1_.write(right_ >= 0);
            bin2_.write(right_ < 0);

            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
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

    public void setMotors(float left, float right) {
        left_ = left;
        right_ = right;
    }

    public void toast(final String message) {
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
}