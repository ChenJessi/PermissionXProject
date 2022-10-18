package com.jessi.permissionx.request

import com.jessi.permissionx.PermissionBuilder

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
    fun showForwardToSettingsDialog(permissions:List<String>, message: String, positiveText:String, negativeText:String? = null){

    }
}