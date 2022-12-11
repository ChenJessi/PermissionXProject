package com.jessi.permissionx.request

import com.jessi.permissionx.PermissionBuilder
import com.jessi.permissionx.dialog.RationaleDialog

/**
 * 封装了  ExplainReasonCallback 和 ExplainReasonCallbackWithBeforeParam
 * 显示相关 dialog 所需要调用的方法
 *
 * @property pb PermissionBuilder
 * @property chainTask ChainTask
 * @constructor
 */
class ExplainScope internal constructor(
    private val pb: PermissionBuilder,
    private val chainTask: ChainTask
    ){

    /**
     * 弹出对话框，显示请求权限的原因
     * @param permissions List<String>
     * @param message String 内容
     * @param positiveText String 确定按钮，当用户点击时，会再次请求权限
     * @param negativeText String? 取消按钮，当用户点击时，将结束请求
     */
    @JvmOverloads
    fun showRequestReasonDialog(permissions: List<String>, message: String, positiveText: String, negativeText: String? = null){
        pb.showHandlePermissionDialog(chainTask ,true, permissions, message, positiveText, negativeText)
    }

    /**
     * 解释请求权限原因的弹窗
     * @param dialog RationaleDialog
     */
    fun showRequestReasonDialog(dialog: RationaleDialog){
        pb.showHandlePermissionDialog(chainTask, true, dialog)
    }


}