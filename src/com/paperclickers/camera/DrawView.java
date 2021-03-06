/*
 * Paperclickers - Affordable solution for classroom response system.
 * 
 * Copyright (C) 2015 Jomara Bindá <jbinda@dca.fee.unicamp.br>
 * Copyright (C) 2015-2016 Eduardo Valle Jr <dovalle@dca.fee.unicamp.br>
 * Copyright (C) 2015-2016 Eduardo Seiti de Oliveira <eduseiti@dca.fee.unicamp.br>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *   
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *   
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package com.paperclickers.camera;

import java.util.List;

import com.paperclickers.fiducial.PaperclickersScanner;
import com.paperclickers.fiducial.TopCode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceView;

public class DrawView extends SurfaceView {

	final static String TAG = "DrawView";
	
	private SparseArray<TopcodeValidator> mValidatorsList;
	
	private int mWidth;
	private int mHeight;
	
	private List<TopCode> mValidTopcodesList;
	
	private float strokeWidth;
	
	private Paint mPaintA;
	private Paint mPaintB;
	private Paint mPaintC;
	private Paint mPaintD;

	
	
	private void drawRectangle(Canvas whichCanvas, float x, float y, float halfWidth, float halfHeight, boolean rounded, Paint whichPaint) {
        
        RectF corners = new RectF(x - halfWidth,  y - halfHeight, x + halfWidth, y + halfHeight);
        
        if (rounded) {  
        	float roundRadius = halfHeight / 2;
        	
        	whichCanvas.drawRoundRect(corners, roundRadius, roundRadius, whichPaint);
        } else {
        	whichCanvas.drawRect(corners, whichPaint);        	
        }
	}
	
	
	
	private void drawTriangle(Canvas whichCanvas, float x, float y, float radius, Paint whichPaint) {
			
		whichCanvas.drawLine(x - radius - strokeWidth / 2, y + radius, x + radius + strokeWidth / 2, y + radius, whichPaint);
		whichCanvas.drawLine(x - radius, y + radius, x + strokeWidth / 8, y - radius, whichPaint);
		whichCanvas.drawLine(x + radius, y + radius, x - strokeWidth / 8, y - radius, whichPaint);		
	}
	
	
	
	public DrawView(Context context, SparseArray<TopcodeValidator> whichValidatorsList, int width, int height) {
		super(context);
		init();
		setWillNotDraw(false);
		
		mValidatorsList = whichValidatorsList;
		
		mValidTopcodesList = null;
		
		mWidth  = width;
		mHeight = height;
	}	
	
	
	
	private void init(){
		
		mPaintA = new Paint();
		
		mPaintA.setColor(PaperclickersScanner.COLOR_A);
		mPaintA.setStyle(Style.STROKE);
		
		DisplayMetrics display = getResources().getDisplayMetrics();
		
		if (display.densityDpi > DisplayMetrics.DENSITY_XHIGH) {
			strokeWidth = 5.0f;
		} else if (display.densityDpi > DisplayMetrics.DENSITY_HIGH) {
			strokeWidth = 3.5f;
		} else {
			strokeWidth = 2.0f;
		}
		
		mPaintA.setStrokeWidth(strokeWidth);			

		
		mPaintA.setAntiAlias(true);
		mPaintA.setDither(true);
		
		mPaintB = new Paint(mPaintA);
		mPaintC = new Paint(mPaintA);
		mPaintD = new Paint(mPaintA);
		
		mPaintB.setColor(PaperclickersScanner.COLOR_B);
		mPaintC.setColor(PaperclickersScanner.COLOR_C);
		mPaintD.setColor(PaperclickersScanner.COLOR_D);
	}
	
	
		
	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (mValidTopcodesList != null) {
				
			for (TopCode topCode : mValidTopcodesList) {

				TopcodeValidator whichValidator = mValidatorsList.get(topCode.getCode());
				
				int bestAnswer = whichValidator.getBestValidAnswer();
								
				if (bestAnswer != PaperclickersScanner.ID_NO_ANSWER) {
				
					float codeX = topCode.getCenterX();
					float codeY = topCode.getCenterY();
					
					float codeDiameter = topCode.getDiameter();
					
					codeX = codeX * canvas.getWidth() / mWidth;
		            codeY = codeY * canvas.getHeight() / mHeight;
		            
		            codeDiameter = codeDiameter * canvas.getWidth() / mWidth * 0.5f;
		            
		            switch(bestAnswer) {
		            case PaperclickersScanner.ID_ANSWER_A:
		            	drawTriangle(canvas, codeX, codeY, codeDiameter, mPaintA);
		            	
		            	break;
		            	
		            case PaperclickersScanner.ID_ANSWER_B:
		            	drawRectangle(canvas, codeX, codeY, codeDiameter * 0.8f, codeDiameter, false, mPaintB);
		            	
		            	break;
		            	
		            case PaperclickersScanner.ID_ANSWER_C:
		            	canvas.drawCircle(codeX, codeY, codeDiameter, mPaintC);
		            	
		            	break;
		            	
		            case PaperclickersScanner.ID_ANSWER_D:
		            	drawRectangle(canvas, codeX, codeY, codeDiameter, codeDiameter, true, mPaintD);
		            	
		            	break;
		            }
				}
			}
		}
	}
	
	
	
	public void updateScreenSize(int width, int height) {
		
		mWidth  = width;
		mHeight = height;	
	}
	
	
	
	public void updateValidTopcodesList(List<TopCode> whichValidTopcodesList) {
		mValidTopcodesList = whichValidTopcodesList;
	}
}
