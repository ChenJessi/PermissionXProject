package com.chencc.permissionx.request

import android.util.Log
import com.chencc.permissionx.PermissionBuilder

/**
 * 后台位置权限
 * Android Q 引入
 */
private const val TAG = "RequestBackgroundLocati"
internal class RequestBackgroundLocationPermission  internal constructor(permissionBuilder: PermissionBuilder)
    : BaseTask(permissionBuilder){

    companion object{

        const val ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION"
    }

    override fun request() {
        Log.e(TAG, "request: ")
    }

    override fun requestAgain(permissions: List<String>) {

    }

    override fun finish() {

    }
}