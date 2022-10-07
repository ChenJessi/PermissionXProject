package com.jessi.permissionx.request

import android.util.Log
import com.jessi.permissionx.PermissionBuilder


/**
 * 后台身体传感器权限权限
 *
 * Android T 引入
 */
private const val TAG = "RequestBodySensorsBackg"
internal class RequestBodySensorsBackgroundPermission  internal constructor(permissionBuilder: PermissionBuilder)
    : BaseTask(permissionBuilder){

    companion object{
        /**
         *
         */
        const val BODY_SENSORS_BACKGROUND = "android.permission.BODY_SENSORS_BACKGROUND"
    }

    override fun request() {
        Log.e(TAG, "request: ")
    }

    override fun requestAgain(permissions: List<String>) {

    }

    override fun finish() {

    }
}