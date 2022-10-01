package com.chencc.permissionx.request

import android.util.Log
import com.chencc.permissionx.PermissionBuilder


/**
 *  特殊权限处理
 *  Implementation for request android.permission.MANAGE_EXTERNAL_STORAGE.
 *  外部存储权限
 *  Android R 引入
 */
private const val TAG = "RequestManageExternalSt"
internal class RequestManageExternalStoragePermission internal constructor(permissionBuilder: PermissionBuilder)
    : BaseTask(permissionBuilder){


    companion object{

        const val MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE"
    }

    override fun request() {
        Log.e(TAG, "request: ")
    }

    override fun requestAgain(permissions: List<String>) {

    }

    override fun finish() {

    }
}