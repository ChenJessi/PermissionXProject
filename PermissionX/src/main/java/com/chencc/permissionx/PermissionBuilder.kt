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

/**
 * 提供 PermissionX 的 api
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


}