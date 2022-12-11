package com.jessi.permissionx.request

import android.Manifest
import android.os.Build
import android.os.Environment
import android.provider.Settings
import com.jessi.permissionx.PermissionBuilder
import com.jessi.permissionx.PermissionX

/**
 *  一些请求的公共代码逻辑
 */
internal abstract class BaseTask(@JvmField var pb : PermissionBuilder) : ChainTask {

    /**
     * 下一个任务
     * 当前任务完成之后要执行的下个任务，如果没有下个任务，则请求过程结束
     */
    @JvmField
    var next : ChainTask? = null

    /**
     * Provide specific scopes for explainReasonCallback for specific functions to call.
     */
    private var explainReasonScope = ExplainScope(pb, this)

    override fun getExplainScope() = explainReasonScope

    /**
     * Provide specific scopes for forwardToSettingsCallback for specific functions to call.
     */
    private var forwardToSettingsScope = ForwardScope(pb, this)

    override fun getForwardScope() = forwardToSettingsScope

    override fun finish() {
        next?.request() ?: let {
            val deniedList = mutableListOf<String>()
            deniedList.addAll(pb.deniedPermissions)
            deniedList.addAll(pb.permanentDeniedPermissions)
            deniedList.addAll(pb.permissionsWontRequest)


            if(pb.shouldRequestNotificationPermission()){
                if(PermissionX.areNotificationsEnabled(pb.activity)){
                    pb.grantedPermissions.add(PermissionX.POST_NOTIFICATIONS)
                }else{
                    deniedList.add(PermissionX.POST_NOTIFICATIONS)
                }
            }

            if(pb.shouldRequestBackgroundLocationPermission()){
                if (PermissionX.isGranted(pb.activity, RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION)) {
                    pb.grantedPermissions.add(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    deniedList.add(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION)
                }
            }

            if(pb.shouldRequestBodySensorsBackgroundPermission()){
                if (PermissionX.isGranted(pb.activity, RequestBodySensorsBackgroundPermission.BODY_SENSORS_BACKGROUND)) {
                    pb.grantedPermissions.add(RequestBodySensorsBackgroundPermission.BODY_SENSORS_BACKGROUND)
                } else {
                    deniedList.add(RequestBodySensorsBackgroundPermission.BODY_SENSORS_BACKGROUND)
                }
            }

            if(pb.shouldRequestInstallPackagesPermission()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && pb.targetSdkVersion >= Build.VERSION_CODES.O) {
                    if (pb.activity.packageManager.canRequestPackageInstalls()) {
                        pb.grantedPermissions.add(RequestInstallPackagesPermission.REQUEST_INSTALL_PACKAGES)
                    } else {
                        deniedList.add(RequestInstallPackagesPermission.REQUEST_INSTALL_PACKAGES)
                    }
                } else {
                    deniedList.add(RequestInstallPackagesPermission.REQUEST_INSTALL_PACKAGES)
                }
            }

            if (pb.shouldRequestManageExternalStoragePermission()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                        Environment.isExternalStorageManager()) {
                    pb.grantedPermissions.add(RequestManageExternalStoragePermission.MANAGE_EXTERNAL_STORAGE)
                } else {
                    deniedList.add(RequestManageExternalStoragePermission.MANAGE_EXTERNAL_STORAGE)
                }
            }

            if (pb.shouldRequestSystemAlertWindowPermission()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && pb.targetSdkVersion >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(pb.activity)) {
                    pb.grantedPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
                } else {
                    deniedList.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
                }
            }

            if (pb.shouldRequestWriteSettingsPermission()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && pb.targetSdkVersion >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(pb.activity)) {
                    pb.grantedPermissions.add(Manifest.permission.WRITE_SETTINGS)
                } else {
                    deniedList.add(Manifest.permission.WRITE_SETTINGS)
                }
            }


            if(pb.requestCallback != null){
                pb.requestCallback?.invoke(deniedList.isEmpty(), ArrayList(pb.grantedPermissions), deniedList)
            }
            pb.endRequest()
        }
    }
}