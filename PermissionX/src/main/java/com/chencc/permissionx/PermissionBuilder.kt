package com.chencc.permissionx

import android.app.Activity
import androidx.fragment.app.FragmentActivity

/**
 * 提供 PermissionX 的 api
 */
class PermissionBuilder internal constructor(private val activity: FragmentActivity, internal val allPermissions : List<String>){


    /**
     * 被拒绝权限
     */
    internal val deniedPermissions = HashSet<String>()

    /**
     * 被永久拒绝权限
     */
    internal val permanentDeniedPermissions = HashSet<String>()
    /**
     * 请求权限说明原因
     */
    private var explainReasonCallback : ExplainReasonCallback? = null

    private var explainReasonCallback2 : ExplainReasonCallback2? = null



    private var requestCallback : RequestCallback? = null

    /**
     * 已请求的权限中的授予权限
     */
    internal val grantedPermissions = HashSet<String>()

    /**
     * 是否在请求之前说明原因
     */
    internal var explainReasonBeforeRequest : Boolean = false

    /**
     * 在请求权限之前说明原因
     */
    fun explainReasonBeforeRequest() : PermissionBuilder{
        explainReasonBeforeRequest = true
        return this
    }


    /**
     * 请求权限方法
     *
     * callback
     * [InvisibleFragment.RequestCallback]
     */
    fun request(callback: RequestCallback){
        requestCallback = callback
        var requestList = ArrayList<String>()
        for (permissions in allPermissions){
            if (PermissionX.isGranted(activity, permissions)){
                grantedPermissions.add(permissions)
            } else {
                requestList.add(permissions)
            }
        }
        // 所有权限已经授予
        if (requestList.isEmpty()){
            callback(true, allPermissions, listOf())
            return
        }
        // 在请求权限之前说明原因  & 否则立即就发起请求
        if (explainReasonBeforeRequest && (explainReasonCallback != null || explainReasonCallback2 != null)){

        } else {
            requestNow(allPermissions, callback)
        }

    }

    /**
     * 立即发起请求
     */
    private fun requestNow(permissions: List<String>, callback: RequestCallback) {
        getInvisibleFragment().requestNow(this, explainReasonCallback, explainReasonCallback2, callback, *permissions.toTypedArray())
    }

    fun test(a : String){

    }
    private fun getInvisibleFragment() : InvisibleFragment{
        val fragmentManager = activity.supportFragmentManager
        val existedFragment = fragmentManager.findFragmentByTag(TAG)
        return if (existedFragment != null) {
            existedFragment as InvisibleFragment
        } else {
            val invisibleFragment = InvisibleFragment()
            fragmentManager.beginTransaction().add(invisibleFragment, TAG).commitNow()
            invisibleFragment
        }
    }
}