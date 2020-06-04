package com.chencc.permissionx

import org.w3c.dom.Text


/**
 *  为 [InvisibleFragment.ExplainReasonCallback] 提供特定范围
 *   提供了两个函数
 *   ExplainReasonCallback  和 showRequestReasonDialog
 *   扩展函数 ExplainReasonCallback 是说明权限原因的回调
 *   fun showRequestReasonDialog 重新发起请求的弹窗
 */
class ExplainReasonScope (private val permissionBuilder : PermissionBuilder){
    fun showRequestReasonDialog(permissions : List<String>, message : String, confirmText : String , cancelText : String? = null ){
        permissionBuilder.showHandlePermissionDialog(true, permissions, message, confirmText, cancelText)
    }
}


/**
 *  [InvisibleFragment.ForwardToSettingsCallback] 限制范围
 *   提供了两个函数
 *    ForwardToSettingsCallback  和 showForwardToSettingsDialog
 *   扩展函数 ExplainReasonCallback 是永久拒绝权限之后，跳转设置页面的回调
 *   fun showForwardToSettingsDialog  跳转设置页面弹窗
 */
class ForwardToSettingsScope(private val permissionBuilder: PermissionBuilder){
    fun showForwardToSettingsDialog(permissions : List<String>, message : String, confirmText : String , cancelText : String? = null ){
        permissionBuilder.showHandlePermissionDialog(false, permissions, message, confirmText, cancelText)
    }
}