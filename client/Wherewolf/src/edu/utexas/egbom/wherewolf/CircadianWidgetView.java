package edu.utexas.egbom.wherewolf;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CircadianWidgetView extends View {
	private Paint canvasPaint, drawPaint;
	private Bitmap canvasBitmap, moonBitmap, sunBitmap, nightBitmap, dayBitmap, dusk1Bitmap, dusk2Bitmap;
	private Canvas drawCanvas;
	public double currentTime;
	private static final String TAG = "time";
	public CircadianWidgetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i("circ widget", "got to the widget");		
		initPaint();
		Log.i("circ widget", "got to the widget");
	}
	
	private void initPaint() {
		drawPaint = new Paint();
		canvasPaint = new Paint(Paint.DITHER_FLAG);
//		drawCanvas = new Canvas();
	    // be sure that you have pngs or jpgs in your drawables folder with 
		// the corresponding names (moon, night, etc)
		moonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moon3);
		nightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.night);
		sunBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
		dayBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.day);
		dusk1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dusk1);
		dusk2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dusk2);		
	}
	
	public void changeTime(double time) {
		  currentTime = time;
		  Log.i(TAG, String.valueOf(currentTime));
		  
		  invalidate(); // causes the onDraw method to be invoked
	}
	
	protected void onDraw(Canvas canvas) {
		  super.onDraw(canvas);
		  float w = (float)drawCanvas.getWidth();
		  float h = (float)drawCanvas.getHeight();

		  int iW = moonBitmap.getWidth() / 2;
		  int iH = moonBitmap.getHeight() / 2;
		  int sW = sunBitmap.getWidth() / 2;
		  int sH = sunBitmap.getHeight() / 2;
		  
		  // draw the backdrop here
//		  drawPaint.setTextSize(20);
//		  canvas.drawText("Time is "+ currentTime, 50, 50, drawPaint);
		  
		  if (currentTime<= 7.0 || currentTime >= 20.0){
			  drawCanvas.drawBitmap(nightBitmap, 0, 0, drawPaint);
		  } else if(currentTime>7.0 && currentTime<= 10.0) {
			  drawCanvas.drawBitmap(dusk1Bitmap, 0, 0, drawPaint);
		  }else if(currentTime>17.0 && currentTime<= 20.0) {
			  drawCanvas.drawBitmap(dusk2Bitmap, 0, 0, drawPaint);
		  }else {
			  drawCanvas.drawBitmap(dayBitmap, 0, 0, drawPaint);
		  }
		  
//		  canvas.drawBitmap(dayBitmap, 0, 0, drawPaint);
//		  canvas.drawBitmap(nightBitmap, 0, 0, drawPaint);
		  
		  
		  // calculate the angle the moon should appear in the sky
		  double theta = Math.PI / 2 + Math.PI * currentTime / 12;
		  double sunTheta = theta+Math.PI;
		  
		  // calculate the x and y coordinates of where to draw the images
		  // keep in mind the coordinates are the top left of the images
		  // so you can use the bitmap width and height to compensate.

		  double moonPosX = w / 2 - w / 3 * Math.cos(theta);
		  double moonPosY = h / 2 - h / 3 * Math.sin(theta) + h/6; // replace this with your value

		  double sunPosX = w / 2 - w / 3 * Math.cos(sunTheta);
		  double sunPosY = h / 2 - h / 3 * Math.sin(sunTheta) + h/6;
		  
		  drawCanvas.drawBitmap(moonBitmap, (int) moonPosX - iW, (int) moonPosY + iH, drawPaint);
		  drawCanvas.drawBitmap(sunBitmap, (int) sunPosX - sW, (int) sunPosY + sH, drawPaint);

		  // draw your sun and other things here as well.
		  canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
		  
		  // experiment with drawCanvas.drawText for putting labels of whether it is day
		  // or night.
		  
		}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, h, oldw, oldh);

	    canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
	    drawCanvas = new Canvas(canvasBitmap);
	}
}
