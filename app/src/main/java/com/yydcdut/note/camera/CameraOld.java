package com.yydcdut.note.camera;

import android.hardware.Camera;

/**
 * Created by shanhong on 3/16/17.
 */

@SuppressWarnings("deprecation")
public class CameraOld implements CameraSupport {

    private Camera camera;

    @Override
    public CameraSupport open(final int cameraId) {
        this.camera = Camera.open(cameraId);
        return this;
    }

    @Override
    public int getOrientation(final int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        return info.orientation;
    }
}