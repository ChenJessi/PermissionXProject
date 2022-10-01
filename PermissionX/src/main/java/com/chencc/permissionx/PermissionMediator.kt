package com.chencc.permissionx

import android.app.Activity
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.chencc.permissionx.constant.allSpecialPermissions
import com.chencc.permissionx.request.RequestBackgroundLocationPermission
import com.chencc.permissionx.request.RequestBodySensorsBackgroundPermission

class PermissionMediator {
    private var activity: FragmentActivity? = null
    private var fragment: Fragment? = null


    constructor(activity: FragmentActivity){
        this.activity = activity
    }

    constructor(fragment: Fragment){
        this.fragment = fragment
    }

    fun permissions(permissions : List<String>) : PermissionBuilder{
        // 普通权限
        val normalPermissionSet = LinkedHashSet<String>()
        // 需要特殊处理的特殊权限
        val specialPermissionSet = LinkedHashSet<String>()
        val osVersion = Build.VERSION.SDK_INT
        val targetSdkVersion = if(activity != null){
            activity!!.applicationInfo.targetSdkVersion
        } else {
            fragment!!.requireContext().applicationInfo.targetSdkVersion
        }
        // 对特殊权限和普通权限分组
        for (permission in permissions){
            if(permission in allSpecialPermissions){
                specialPermissionSet.add(permission)
            }else{
                normalPermissionSet.add(permission)
            }
        }

        if(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION in specialPermissionSet){
            if(osVersion == Build.VERSION_CODES.Q ||
                (osVersion == Build.VERSION_CODES.R && targetSdkVersion < Build.VERSION_CODES.R)){
                // 如果当前平台Android版本 为 Q 或者  为 R 并且 targetSdkVersion 低于 R，
                // 那么 后台位置权限 不需要特殊处理，正常请求即可
                specialPermissionSet.remove(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION)
                normalPermissionSet.add(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION)
            }
        }
        if(PermissionX.POST_NOTIFICATIONS in specialPermissionSet){
            if(osVersion >= Build.VERSION_CODES.TIRAMISU && targetSdkVersion >= Build.VERSION_CODES.TIRAMISU){
                // 如果当前平台Android版本 >= T  或者 targetSdkVersion >= T
                // 那么 发送通知权限 不需要特殊处理，正常请求即可
                specialPermissionSet.remove(PermissionX.POST_NOTIFICATIONS)
                normalPermissionSet.add(PermissionX.POST_NOTIFICATIONS)
            }
        }

        return PermissionBuilder(activity, fragment, normalPermissionSet, specialPermissionSet)
    }

    fun permissions(vararg permission: String){
        permissions(listOf(*permission))
    }
}