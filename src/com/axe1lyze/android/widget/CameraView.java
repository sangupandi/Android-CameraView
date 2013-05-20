package com.axe1lyze.android.widget;

import com.axe1lyze.android.hardware.SupportCamera;
import com.axe1lyze.android.hardware.SupportCamera.CameraListener;
import com.axe1lyze.android.hardware.SupportCamera.State;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class CameraView extends FrameLayout implements CameraListener {
	
	private CameraSurfaceView cameraSurfaceView;
	
	private SupportCamera supportCamera;
	public SupportCamera getSupoprtCamera(){return supportCamera;}
	
	private ScaleType scaleType = ScaleType.CENTER_INSIDE;
	public ScaleType getScaleType(){return scaleType;}
	public void setScaleType(ScaleType scaleType){this.scaleType=scaleType;}
	
	public CameraView(Context context) {super(context);init();}
	public CameraView(Context context, AttributeSet attrs){super(context, attrs);init();}
	public CameraView(Context context, AttributeSet attrs,int defStyle){super(context, attrs,defStyle);init();}
	
	private void init(){
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		params.gravity=Gravity.CENTER;
		
		cameraSurfaceView = new CameraSurfaceView(getContext());
		cameraSurfaceView.setLayoutParams(params);

		this.supportCamera=cameraSurfaceView.getSupportCamera();
		this.supportCamera.addCameraListener(this);
		
		addView(cameraSurfaceView);
	}
	
	@Override
	protected synchronized void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		aplyScale(w,h);
	}
	
	private synchronized void aplyScale(int width,int height){
		if(cameraSurfaceView.getSupportCamera()==null)return;
		
		ViewGroup.LayoutParams params = cameraSurfaceView.getLayoutParams(); 
		Camera.Size previewSize = cameraSurfaceView.getSupportCamera().camera.getParameters().getPreviewSize();
		
		float scale = isPortrait()?
				(float)previewSize.height/(float)previewSize.width:
				(float)previewSize.width/(float)previewSize.height;
				
		params.width=width;
		params.height=(int) (width/scale);

		cameraSurfaceView.setLayoutParams(params);
	}
	
	public boolean isPortrait(){
		return getContext().getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT;
	}
	
	@Override
	public void onStateChanged(SupportCamera supportCamera, State state) {
		switch(state){
		case READY:
			this.supportCamera.camera.setDisplayOrientation(isPortrait()?90:0);
			break;
		case PREVIEWING:
			aplyScale(getWidth(),getHeight());
			break;
		default:break;
		}
	}
	
}
