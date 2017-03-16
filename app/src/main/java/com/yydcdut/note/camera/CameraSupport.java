package com.yydcdut.note.camera;

/**
 * Created by shanhong on 3/16/17.
 */

public interface CameraSupport {
    CameraSupport open(int cameraId);
    int getOrientation(int cameraId);
}