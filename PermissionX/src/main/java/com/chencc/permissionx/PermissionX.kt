package com.chencc.permissionx

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.security.PermissionCollection

object PermissionX {
    /**
     * 初始化工作
     */
    fun init(activity: FragmentActivity) = PermissionCollection(activity)

    fun init(fragment: Fragment) = PermissionCollection(fragment.requireActivity())

    /**
     * 检查权限是否已经被授予
     */
    fun isGranted(context: Context, permission: String) = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    /**
     * 检查应用是否拥有通知权限
     * @return 如果当前 Android 版本小于 19 始终返回true
     */
    fun areNotificationsEnabled(context: Context) = NotificationManagerCompat.from(context).areNotificationsEnabled()


    /**
     * 发送通知权限
     * Android T 引入
     */
    const val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"
}

/**
 * 用于传递权限数据
 */
class PermissionCollection internal constructor(private val activity: FragmentActivity) {

    //fun permissions(vararg permissions: String) = PermissionBuilder(activity, permissions.toList())
}