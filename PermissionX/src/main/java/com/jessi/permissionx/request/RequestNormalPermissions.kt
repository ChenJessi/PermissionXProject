package com.jessi.permissionx.request

import android.util.Log
import com.jessi.permissionx.PermissionBuilder
import com.jessi.permissionx.PermissionX

/**
 * 普通权限请求的实现
 */
private const val TAG = "RequestNormalPermission"
internal class RequestNormalPermissions internal constructor(permissionBuilder: PermissionBuilder)
    : BaseTask(permissionBuilder){
    override fun request() {
        Log.e(TAG, "request: ")
        // 需要请求到权限列表
        val requestList = ArrayList<String>()
        for (permission in pb.normalPermissions){
            if(PermissionX.isGranted(pb.activity, permission)){
                // 如果该权限已经授予，直接加入到已授予权限到集合
                pb.grantedPermissions.add(permission)
            }else {
                requestList.add(permission)
            }
        }
        // 为空表示所有权限都已授权
        if(requestList.isEmpty()){
            finish()
        }
        if(pb.explainReasonBeforeRequest &&
            (pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null)){
                // 需要先显示 请求权限说明 的弹窗
            pb.explainReasonBeforeRequest = false
            pb.deniedPermissions.addAll(requestList)
            if(pb.explainReasonCallbackWithBeforeParam != null){
                pb.explainReasonCallbackWithBeforeParam?.invoke(getExplainScope(), requestList, true)
            }else{
                pb.explainReasonCallback?.invoke(getExplainScope(), requestList)
            }
        }
        else{
            // 不需要则立即开始请求所有权限
            pb.requestNow(pb.normalPermissions, this)
        }
    }

    /**
     * 如果用户拒绝权限 并且调用了[ExplainScope.showRequestReasonDialog]
     * 或者 [ForwardScope.shawForwardToSettingsDialog]
     * 当用户点击了确定按钮时，将调用此方法
     * @param permissions List<String>
     */
    override fun requestAgain(permissions: List<String>) {
        val permissionsToRequestAgain: MutableSet<String> = HashSet(pb.grantedPermissions)

    }

}