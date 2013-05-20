package com.axe1lyze.android.hardware;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;


public class SupportCamera {

	private static final CameraSizeComparator cameraSizeComparetor = new CameraSizeComparator();
	
	public static final int FACING_BACK=CameraInfo.CAMERA_FACING_BACK;
	public static final int FACING_FRONT=CameraInfo.CAMERA_FACING_FRONT;

	public static enum State{
		READY,
		PREVIEWING,
		RELEASED,
	}
	

	private Set<CameraListener> listenerList = new HashSet<CameraListener>();
	public void addCameraListener(CameraListener listener){listenerList.add(listener);}
	public void removeCameraListener(CameraListener listener){listenerList.remove(listener);}
	
	public final Camera camera;
	private CameraListener listener;
	private State state;
	
	public SupportCamera(int facing,CameraListener cameraListener) throws CameraException{
		this.listener=cameraListener;
		this.camera = getCamera(facing);
		this.state = State.READY;
		this.listenerList.add(cameraListener);
		for(CameraListener listener:listenerList){
			listener.onStateChanged(this, this.state);
		}
	}
	
	public synchronized void startPreview(){
		if(state.equals(State.PREVIEWING)||state.equals(State.RELEASED))return;
		
		this.state = State.PREVIEWING;
		this.camera.startPreview();

		for(CameraListener listener:listenerList){
			listener.onStateChanged(this, this.state);
		}
	}
	
	public synchronized void stopPreview(){
		if(!state.equals(State.PREVIEWING)||state.equals(State.RELEASED))return;
		
		this.camera.stopPreview();
		this.state = State.READY;
		for(CameraListener listener:listenerList){
			listener.onStateChanged(this, this.state);
		}
	}
	
	public synchronized void setMaxPreviewSize(){
		Parameters params = camera.getParameters();
		List<Camera.Size> previewSizeList = params.getSupportedPreviewSizes();
		Collections.sort(previewSizeList,cameraSizeComparetor);
		Camera.Size previewSize = previewSizeList.get(0);
		params.setPreviewSize(previewSize.width,previewSize.height);
		camera.setParameters(params);
	}
	
	private Camera getCamera(int facing) throws CameraException{
		for(int i=0;i<Camera.getNumberOfCameras();i++){
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i,info);
			if(info.facing==facing)return Camera.open(i);
		}
		throw new CameraException("No camera found.");
	}
	
	public synchronized void release(){
		if(state.equals(State.RELEASED)||state.equals(State.RELEASED))return;
		
		stopPreview();
		
		this.state = State.RELEASED;
		this.camera.release();

		for(CameraListener listener:listenerList){
			listener.onStateChanged(this, this.state);
		}
		
		listenerList.remove(listener);
	}
	
	
	
	public static class CameraException extends Exception{
		public CameraException(String message) {super(message);}
	}
	
	public interface CameraListener{
		public void onStateChanged(SupportCamera camera,State state);
	}
}


class CameraSizeComparator implements Comparator<Camera.Size>{
	public int compare(Size lhs, Size rhs) {
		long lsize = lhs.width*lhs.height;
		long rsize = rhs.width*lhs.height;
		if(lsize==rsize)return 0;
		else if(lsize<rsize)return 1;
		else return -1;
	}
}
