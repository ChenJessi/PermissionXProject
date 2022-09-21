package com.chencc.permissionx.constant

import android.Manifest
import com.chencc.permissionx.PermissionX
import com.chencc.permissionx.request.RequestBackgroundLocationPermission
import com.chencc.permissionx.request.RequestBodySensorsBackgroundPermission
import com.chencc.permissionx.request.RequestInstallPackagesPermission
import com.chencc.permissionx.request.RequestManageExternalStoragePermission


/**
 * 需要单独处理对特殊权限
 */
val allSpecialPermissions = setOf(
    Manifest.permission.WRITE_SETTINGS,
    RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION,
    Manifest.permission.SYSTEM_ALERT_WINDOW,
    RequestManageExternalStoragePermission.MANAGE_EXTERNAL_STORAGE,
    RequestInstallPackagesPermission.REQUEST_INSTALL_PACKAGES,
    PermissionX.POST_NOTIFICATIONS,
    RequestBodySensorsBackgroundPermission.BODY_SENSORS_BACKGROUND,
)
