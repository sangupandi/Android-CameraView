package com.axe1lyze.android.widget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import java.util.Collections;
import java.util.Comparator;

import com.axe1lyze.android.hardware.SupportCamera;
import com.axe1lyze.android.hardware.SupportCamera.CameraException;
import com.axe1lyze.android.hardware.SupportCamera.CameraListener;
import com.axe1lyze.android.hardware.SupportCamera.State;



public class CameraSurfaceView extends SurfaceView implements Callback, CameraListener {
	
	private SupportCamera supportCamera;
	public SupportCamera getSupportCamera() {return supportCamera;}
	
	public CameraSurfaceView(Context context) {super(context);init();}
	public CameraSurfaceView(Context context, AttributeSet attrs){super(context, attrs);init();}
	public CameraSurfaceView(Context context, AttributeSet attrs,int defStyle){super(context, attrs,defStyle);init();}
	
	private void init(){
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		try {
			supportCamera=new SupportCamera(SupportCamera.FACING_BACK,this);
		} catch (CameraException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			supportCamera.camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(supportCamera==null)return;
		supportCamera.release();
		
	}
	
	@Override
	public void onStateChanged(SupportCamera camera, State state) {
		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		supportCamera.stopPreview();
		supportCamera.setMaxPreviewSize();
		supportCamera.startPreview();
	}
	
}
