package com.jessi.permissionx.request

import com.jessi.permissionx.PermissionBuilder
import com.jessi.permissionx.dialog.RationaleDialog


/**
 * 某些权限和永久关闭的权限需要跳转到设置页手动打开，可能需要调用这些方法
 * 封装了 FrwardToSettingsCallback
 * 显示相关 dialog 所需要调用的方法
 * @property pb PermissionBuilder
 * @property chainTask ChainTask
 * @constructor
 */
class ForwardScope internal constructor(
    private val pb: PermissionBuilder,
    private val chainTask: ChainTask
){



    /**
     * 显示对话框，告诉用户要在设置中同意这些权限
     * @param permissions List<String> 要请求的权限
     * @param message String 提示信息内容
     * @param positiveText String 确定按钮文本 点击确定按钮，将跳转到设置页面
     * @param negativeText String? 取消按钮文本 点击取消按钮，将结束请求
     */
    @JvmOverloads
    fun showForwardToSettingsDialog(permissions:List<String>, message: String, positiveText:String, negativeText:String? = null){
        pb.showHandlePermissionDialog(chainTask, false, permissions, message, positiveText, negativeText)
    }

    /**
     * 展示请求权限的原因
     * @param dialog RationaleDialog
     */
    fun showRequestReasonDialog(dialog: RationaleDialog){
        pb.showHandlePermissionDialog(chainTask, false, dialog)
    }


}