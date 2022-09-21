package com.chencc.permissionx

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.PersistableBundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * 提供 PermissionX 的 api
 */
class PermissionBuilder internal constructor(
    val activity: FragmentActivity? ,
    val fragment: Fragment?,
    val normalPermissionSet : MutableSet<String>,
    val specialPermissions : MutableSet<String>){


}