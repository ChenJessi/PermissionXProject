package com.chencc.permissionx

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object PermissionX {
    /**
     * 初始化工作
     */
    fun init(activity: FragmentActivity) = PermissionCollection(activity)

    /**
     * 检查权限是否已经申请
     */
    fun isGranted(context: Context, permission: String) = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

/**
 * 用于传递权限数据
 */
class PermissionCollection internal constructor(private val activity: FragmentActivity) {

    fun permissions(vararg permissions: String) = PermissionBuilder(activity, permissions.toList())
}