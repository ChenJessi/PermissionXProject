package com.chencc.permissionx.request

import android.util.Log
import com.chencc.permissionx.PermissionBuilder

/**
 * 普通权限请求的实现
 */
private const val TAG = "RequestNormalPermission"
internal class RequestNormalPermissions internal constructor(permissionBuilder: PermissionBuilder)
    : BaseTask(permissionBuilder){
    override fun request() {
        Log.e(TAG, "request: ")
    }

    override fun requestAgain(permissions: List<String>) {

    }

    override fun finish() {

    }
}