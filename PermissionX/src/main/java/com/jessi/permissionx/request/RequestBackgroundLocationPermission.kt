package com.jessi.permissionx.request

import android.Manifest
import android.os.Build
import android.util.Log
import com.jessi.permissionx.PermissionBuilder
import com.jessi.permissionx.PermissionX

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
        if(pb.shouldRequestBackgroundLocationPermission()){
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                // Android Q以下没有后台程序权限
                // 直接删除该权限，加入到拒绝列表
                pb.specialPermissions.remove(ACCESS_BACKGROUND_LOCATION)
                pb.permissionsWontRequest.add(ACCESS_BACKGROUND_LOCATION)
                finish()
                return
            }
            if(PermissionX.isGranted(pb.activity, ACCESS_BACKGROUND_LOCATION)){
                finish()
                return
            }
            val accessFindLocationGranted = PermissionX.isGranted(pb.activity, Manifest.permission.ACCESS_FINE_LOCATION)
            val accessCoarseLocationGranted = PermissionX.isGranted(pb.activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            if(accessFindLocationGranted || accessCoarseLocationGranted){
                if (pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null){
                    val requestList = mutableListOf(ACCESS_BACKGROUND_LOCATION)
                    if(pb.explainReasonCallbackWithBeforeParam != null){
                        pb.explainReasonCallbackWithBeforeParam?.invoke(getExplainScope(), requestList, true)
                    }
                    else {
                        pb.explainReasonCallback?.invoke(getExplainScope(), requestList)
                    }
                }else{
                    requestAgain(emptyList())
                }
                return
            }
        }
        finish()
    }

    override fun requestAgain(permissions: List<String>) {
        pb.requestAccessBackgroundLocationPermissionNow(this)
    }

    override fun finish() {

    }
}