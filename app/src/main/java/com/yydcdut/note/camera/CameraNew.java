package com.yydcdut.note.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.yydcdut.note.camera.CameraSupport;

public class CameraNew implements CameraSupport {

    private CameraDevice camera;
    private CameraManager manager;

    public CameraNew(final Context context) {
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public CameraSupport open(final int cameraId) {
        try {
            String[] cameraIds = manager.getCameraIdList();


            manager.openCamera(cameraIds[cameraId], new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    CameraNew.this.camera = camera;
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    CameraNew.this.camera = camera;
                    // TODO handle
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    CameraNew.this.camera = camera;
                    // TODO handle
                }
            }, null);
        } catch (Exception e) {
            // TODO handle
        }
        return this;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int getOrientation(final int cameraId) {
        try {
            String[] cameraIds = manager.getCameraIdList();
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIds[cameraId]);
            return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            // TODO handle
            return 0;
        }
    }
}