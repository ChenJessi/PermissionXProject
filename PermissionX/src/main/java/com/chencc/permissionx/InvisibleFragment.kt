package com.chencc.permissionx

import androidx.fragment.app.Fragment

/**
 * [PermissionBuilder.request] callback
 * @param allGranted 是否全部授予
 * @param grantedList 授予权限列表
 * @param deniedList  拒绝权限列表
 */

typealias RequestCallback = (allGranted : Boolean, grantedList : List<String>, deniedList : List<String>) -> Unit

/**
 * 请求权限原因
 * [PermissionBuilder.onExplainRequestReason]
 * @param deniedList 拒绝权限列表 用来重新发起请求
 */
typealias ExplainReasonCallback = (deniedList : MutableList<String>) -> Unit

/**
 * 请求权限原因
 * [PermissionBuilder.onExplainRequestReason]
 * @param deniedList 拒绝权限列表 用来重新发起请求
 * @param beforeRequest 标记是请求之前还是之后
 */
typealias ExplainReasonCallback2 = (deniedList : MutableList<String>, beforeRequest : Boolean) -> Unit


const val TAG = "InvisibleFragment"

class InvisibleFragment : Fragment(){

    /**
     * 立即发起请求
     */
    fun requestNow(builder: PermissionBuilder, callback: ExplainReasonCallback?, callback2: ExplainReasonCallback2?, vararg permission: String){

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}