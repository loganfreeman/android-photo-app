package com.yydcdut.note.camera;

import android.os.Build;

import dagger.Module;
import dagger.Provides;

import static com.yydcdut.note.aspect.permission.PermissionInstance.context;

/**
 * Created by shanhong on 3/16/17.
 */

@Module
public class CameraModule {

    @Provides
    CameraSupport provideCameraSupport(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new CameraNew(context);
        } else {
            return new CameraOld();
        }
    }
}