package com.chencc.permissionx

import android.app.Activity
import android.app.AlertDialog
import android.os.PersistableBundle
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

    /**
     * 跳转设置界面回调
     */
    private var forwardToSettingsCallback: ForwardToSettingsCallback? = null
    /**
     * 权限申请说明原因 scope
     */
    internal val explainReasonScope = ExplainReasonScope(this)
    /**
     * 跳转设置页面 scope
     */
    internal val forwardToSettingsScope = ForwardToSettingsScope(this)

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
     * 是否显示了对话框
     * [ExplainReasonScope.showRequestReasonDialog] or [ForwardToSettingsScope.showForwardToSettingsDialog]
     * 如果未显示对话框则会直接回调 requestCallback
     */
    var showDialogCalled = false

    /**
     * 在请求权限之前说明原因
     */
    fun explainReasonBeforeRequest() : PermissionBuilder{
        explainReasonBeforeRequest = true
        return this
    }

    fun explainReasonCallback(callback: ExplainReasonCallback) : PermissionBuilder{
        this.explainReasonCallback = callback
        return this
    }

    fun explainReasonCallback(callback: ExplainReasonCallback2) : PermissionBuilder{
        this.explainReasonCallback2 = callback
        return this
    }

    fun onForwardToSettings(callback: ForwardToSettingsCallback) : PermissionBuilder{
        this.forwardToSettingsCallback = callback
        return this
    }

    /**
     *showHandlePermissionDialog 显示对话框  / 权限申请说明原因对话框 or 跳转设置页面对话框
     * @param showReasonOrGoSettings 是重新请求 or 跳转至设置页面
     * @param permissions 权限集合
     * @param message 弹窗显示的信息
     * @param confirmText 确认按钮文字
     * @param cancelText 取消按钮文字
     */
    internal fun showHandlePermissionDialog(showReasonOrGoSettings : Boolean, permissions: List<String>, message : String, confirmText : String , cancelText : String? = null){
        showDialogCalled = true
        val filterPermission = permissions.filter {
            //未授权 并且 申请授权了的权限
            // 只要 未授权 并且 申请授权的权限 发起新的授权申请才有意义
            !grantedPermissions.contains(it) && allPermissions.contains(it)
        }
        if (filterPermission.isEmpty()){
            onPermissionDialogCancel()
            return
        }
        AlertDialog.Builder(activity).apply {
            setMessage(message)
            setCancelable(cancelText.isNullOrBlank())
            setPositiveButton(confirmText) {_,_-> Unit
                if (showReasonOrGoSettings){
                    //重新请求
                    requestAgain(filterPermission)
                }else{  //跳转设置页
                    forwardToSettings()
                }
            }
            cancelText?.let {
                setNegativeButton(cancelText){_,_ -> Unit
                    onPermissionDialogCancel()
                }
            }
            show()
        }
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
            explainReasonBeforeRequest = false
            deniedPermissions.addAll(requestList)
            explainReasonCallback2?.let {
                explainReasonScope.it(requestList, true)
            } ?:
            explainReasonCallback?.let { explainReasonScope.it(requestList) }
        } else {
            requestNow(allPermissions, callback)
        }
    }

    /**
     * 请求被拒绝之后，说明原因重新发起请求
     */
    private fun requestAgain(permissions: List<String>){
        if (permissions.isEmpty()){
            onPermissionDialogCancel()
            return
        }
        requestCallback?.let {
            val permissionSet = HashSet(grantedPermissions)
            permissionSet.addAll(permissions)
            requestNow(permissionSet.toList(), it)
        }
    }


    /**
     * 立即发起请求
     */
    private fun requestNow(permissions: List<String>, callback: RequestCallback) {
        getInvisibleFragment().requestNow(this, explainReasonCallback, explainReasonCallback2, forwardToSettingsCallback, callback, *permissions.toTypedArray())
    }


    /**
     * 跳转至设置页面
     * 权限被永久拒绝之后，需跳转至设置页打开
     */
    private fun forwardToSettings(){

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

    /**
     * 如果用户拒绝了权限, 并且 调用了 [ExplainReasonScope.showRequestReasonDialog] 或者 [ForwardToSettingsScope.showForwardToSettingsDialog]
     * 用户点击了弹窗取消按钮需要调用这个方法
     */
    private fun onPermissionDialogCancel(){
        val deniedList = ArrayList<String>()
        deniedList.addAll(deniedPermissions)
        deniedList.addAll(permanentDeniedPermissions)
        requestCallback?.let {
            it(deniedList.isEmpty(), grantedPermissions.toList(), deniedList)
        }
    }
}