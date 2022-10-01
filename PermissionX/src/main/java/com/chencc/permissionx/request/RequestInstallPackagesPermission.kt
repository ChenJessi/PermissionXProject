package com.chencc.permissionx.request

import android.util.Log
import com.chencc.permissionx.PermissionBuilder

/**
 * 安装未知来源权限
 * Android M 引入
 */
private const val TAG = "RequestInstallPackagesP"
internal class RequestInstallPackagesPermission  internal constructor(permissionBuilder: PermissionBuilder)
    : BaseTask(permissionBuilder){


    companion object{

        const val REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES"
    }

    override fun request() {
        Log.e(TAG, "request: ")
    }

    override fun requestAgain(permissions: List<String>) {

    }

    override fun finish() {

    }
}