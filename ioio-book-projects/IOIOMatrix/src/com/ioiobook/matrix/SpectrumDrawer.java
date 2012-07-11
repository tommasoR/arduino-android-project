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

import org.hermit.dsp.FFTTransformer;
import org.hermit.dsp.Window;

import android.util.Log;

public class SpectrumDrawer {

    private float gain_ = 1000000.0f;
    private int[][] displayArray_;
    private Window win_;
    private FFTTransformer spectrumAnalyser_;
    private int historyIndex_;
    private float[] average_;
    private float[][] histories_;

    // 128 values in average_ we just want 8 - Fn = n * Fs / N where Fn is freq
    // at
    // data point n, Fs is the sample freq and N is the buffer size
    private final int[] frequencies_ = { 2, 4, 6, 10, 15, 25, 55, 80 };
    private final int[] colors_ = { 2, 2, 3, 3, 3, 1, 1, 1 };

    public SpectrumDrawer(int[][] display) {
        displayArray_ = display;
        win_ = new Window(MainActivity.AUDIO_BUFFER_SIZE,
                Window.Function.BLACKMAN_HARRIS);
        spectrumAnalyser_ = new FFTTransformer(MainActivity.AUDIO_BUFFER_SIZE,
                win_);
        average_ = new float[MainActivity.AUDIO_BUFFER_SIZE / 2];
        histories_ = new float[MainActivity.AUDIO_BUFFER_SIZE / 2][MainActivity.AUDIO_BUFFER_SIZE / 2];
    }

    public void calculateSpectrum(short[] buffer) {
        // apply FFT to the buffer to get the spectrum, but we only have 8
        // columns
        // so sum into 8 bands
        spectrumAnalyser_.setInput(buffer, 0, MainActivity.AUDIO_BUFFER_SIZE);
        spectrumAnalyser_.transform();
        historyIndex_ = spectrumAnalyser_.getResults(average_, histories_,
                historyIndex_);

        for (int c = 0; c < 8; c++) {
            int resultIndex = frequencies_[c];
            // Do we need to log this?
            int power = (int) (Math.log(average_[resultIndex] * gain_));
            Log.d("SRM", "" + power);
            if (power > 7)
                power = 7;
            for (int r = 0; r < 8; r++) {
                if (power > r) {
                    displayArray_[7 - r][c] = colors_[r];
                } else {
                    displayArray_[7 - r][c] = 0;
                }
            }
        }

    }

}
