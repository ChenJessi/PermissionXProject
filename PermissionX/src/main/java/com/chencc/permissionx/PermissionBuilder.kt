package com.chencc.permissionx

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.PersistableBundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.chencc.permissionx.request.*
import com.chencc.permissionx.request.RequestBackgroundLocationPermission
import com.chencc.permissionx.request.RequestInstallPackagesPermission
import com.chencc.permissionx.request.RequestManageExternalStoragePermission
import com.chencc.permissionx.request.RequestNormalPermissions

/**
 * 提供 PermissionX 的 api
 *
 * @property normalPermissionSet 需要申请的普通权限集
 * @property specialPermissionsSet 需要申请的特殊权限集
 */
class PermissionBuilder (
    fragmentActivity: FragmentActivity? ,
    fragment: Fragment?,
    normalPermissionSet : MutableSet<String>,
    specialPermissionsSet : MutableSet<String>){

    lateinit var activity: FragmentActivity

    private var fragment: Fragment? = null

    // 想要请求的普通权限
    var normalPermissions = mutableSetOf<String>()
    // 想要请求的特殊权限集合
    var specialPermissions = mutableSetOf<String>()

    init {
        fragmentActivity?.let {
            activity = it
        }
        if(fragmentActivity == null && fragment != null){
            activity = fragment.requireActivity()
        }
        this.fragment = fragment
        this.normalPermissions = normalPermissionSet
        this.specialPermissions = specialPermissionsSet
    }

    // 是否展示网络请求的弹窗
    var explainReasonBeforeRequest = false;

    var requestCallback : ((Boolean, List<String>, List<String>)->Unit)? = null

    /**
     * The origin request orientation of the current Activity. We need to restore it when
     * permission request finished.
      */
    private var originRequestOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    /**
     * @param allGranted 是否授予了所有权限,
     * @param grantedList 已授予的权限列表,
     * @param deniedList 已拒绝的权限列表
     * @param callback Function3<Boolean, List<String>, List<String>, Unit>
     */
    fun request(callback : (Boolean, List<String>, List<String>)->Unit){
        requestCallback = callback;
        startRequest();
    }


    private fun startRequest(){
        // Lock the orientation when requesting permissions, or callback maybe missed due to
        // activity destroyed.
        lockOrientation()

        // 创建请求任务链， 先请求 RequestNormalPermissions 普通权限，再请求 RequestBackgroundLocationPermission
        val requestChain = RequestChain();
        requestChain.addTaskToChain(RequestNormalPermissions(this))
        requestChain.addTaskToChain(RequestBackgroundLocationPermission(this))
        requestChain.addTaskToChain(RequestManageExternalStoragePermission(this))
        requestChain.addTaskToChain(RequestInstallPackagesPermission(this))
        requestChain.addTaskToChain(RequestBodySensorsBackgroundPermission(this))

        requestChain.runTask()
    }

    /**
     * Lock the screen orientation. Activity couldn't rotate with sensor.
     * Android O has bug that only full screen activity can request orientation,
     * so we need to exclude Android O.
     */
    private fun lockOrientation(){
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            originRequestOrientation = activity.requestedOrientation
            val orientation = activity.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        }
    }


    /**
     *  是否需要发送通知权限
     *  @return 如果需要发送通知权限，返回true， 否则返回false
     */
    fun shouldRequestNotificationPermission() = specialPermissions.contains(PermissionX.POST_NOTIFICATIONS)
}