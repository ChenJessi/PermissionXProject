package com.jessi.permissionx.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View

/**
 * base dialog , dialog 用来展示请求权限的原因
 *
 * @constructor
 */
abstract class RationaleDialog(
    context: Context,
    cancelable: Boolean,
    cancelListener: DialogInterface.OnCancelListener?
) : Dialog(context, cancelable, cancelListener) {
     
    /**
     * 返回确定按钮
     * @return View
     */
    abstract fun getPositiveButton() : View

    /**
     * 返回取消按钮
     * @return View
     */
    abstract fun getNegativeButton() : View

    /**
     * 请求的权限
     * @return List<String>
     */
    abstract fun getPermissionsToRequest() : List<String>
}