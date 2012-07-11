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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class RoverControlView extends View {
	
	private MainActivity context_;
    private Paint paint = new Paint();
    private int x0_;
    private int y0_;
    private int diameter_;
    private int width_;
    private int height_;
    private int x_ = 0;
    private int y_ = 0;
    
	private static final float sin135 = -0.7071f;
	private static final float cos135 = - sin135;


    
    public RoverControlView(MainActivity context) {
        super(context);
        context_ = context;
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10.0f);
    }
    
    

    @Override
    public void onDraw(Canvas canvas) {
		width_ = getWidth();
		height_ = getHeight();
		x0_ = width_ / 2;
		y0_ = height_ / 2; 
		diameter_ = height_ / 3;
		int leftX = (x0_ - diameter_);
    	int rightX = (x0_ + diameter_);
    	int topY = (y0_ - diameter_);
    	int bottomY = (y0_ + diameter_);
    	
        canvas.drawLine(leftX, topY, rightX, topY, paint);
        canvas.drawLine(leftX, bottomY, rightX, bottomY, paint);
        canvas.drawLine(leftX, topY, leftX, bottomY, paint);
        canvas.drawLine(rightX, topY, rightX, bottomY, paint);
        canvas.drawLine(x0_, 0, x0_, 1000, paint);
        canvas.drawLine(0, y0_, 1000, y0_, paint);
        canvas.drawCircle(x_, y_, 30, paint);
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    x_ = (int)event.getX();
	    y_ = (int)event.getY();
		int x1 = x_ - x0_;
		int y1 = y_ - y0_;
		float xf = (float)x1 / diameter_; // +- 0..1
		float yf = -(float)y1 / diameter_;
		float left = (float) (xf * cos135 - yf * sin135);
		float right = (float) (xf * sin135 + yf * cos135);		

	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    	context_.setMotors(left, right);
	    }
	    invalidate();
		return true;
	}

}