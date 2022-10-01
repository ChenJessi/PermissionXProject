//package com.chencc.permissionx
//
//import android.content.Intent
//import android.content.pm.PackageManager
//import androidx.fragment.app.Fragment
//import kotlin.collections.withIndex as withIndex
//
///**
// * [PermissionBuilder.request] callback
// * @param allGranted 是否全部授予
// * @param grantedList 授予权限列表
// * @param deniedList  拒绝权限列表
// */
//
//typealias RequestCallback = (allGranted : Boolean, grantedList : List<String>, deniedList : List<String>) -> Unit
//
///**
// * 请求权限原因
// * [PermissionBuilder.onExplainRequestReason]
// * @param deniedList  回调拒绝权限列表  并重新发起请求
// */
//typealias ExplainReasonCallback = ExplainReasonScope.(deniedlist : MutableList<String>) -> Unit
//
///**
// * 请求权限原因
// * [PermissionBuilder.onExplainRequestReason]
// * @param deniedList 拒绝权限列表 用来重新发起请求
// * @param beforeRequest 标记是请求之前还是之后
// */
//typealias ExplainReasonCallback2 = ExplainReasonScope.(deniedList : MutableList<String>, beforeRequest : Boolean) -> Unit
//
///**
// * 跳转至设置页面的回调
// * 用户永久拒绝了之后，需要跳转至设置页面打开权限
// * @param deniedlist  用户永久拒绝的权限，此时代码层面无法操作，只能引导用户跳转去设置页面打开
// */
//typealias ForwardToSettingsCallback = ForwardToSettingsScope.(deniedlist : MutableList<String>) -> Unit
//
//
//const val TAG = "InvisibleFragment"
//
//const val PERMISSION_CODE = 1
//
//const val SETTINGS_CODE = 2
//
//class InvisibleFragment : Fragment(){
//
//    private lateinit var permissionBuilder: PermissionBuilder
//    // 权限请求结果回调
//    private lateinit var requestCallback: RequestCallback
//
//    /**
//     * 说明请求原因
//     */
//    private  var explainReasonCallback: ExplainReasonCallback? = null
//    private  var explainReasonCallback2: ExplainReasonCallback2? = null
//
//    private  var forwardToSettingsCallback : ForwardToSettingsCallback? = null
//    /**
//     * 立即发起请求
//     */
//    fun requestNow(builder: PermissionBuilder, explainReasonCallback: ExplainReasonCallback?, explainReasonCallback2: ExplainReasonCallback2?, forwardToSettingsCallback : ForwardToSettingsCallback? , requestCallback: RequestCallback , vararg permission: String){
//        this.permissionBuilder = builder
//        this.explainReasonCallback = explainReasonCallback
//        this.explainReasonCallback2 = explainReasonCallback2
//        this.forwardToSettingsCallback = forwardToSettingsCallback
//        this.requestCallback = requestCallback
//        requestPermissions(permission, PERMISSION_CODE)
//    }
//
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        if (requestCode == PERMISSION_CODE){
//            // 本次请求 权限申请成功的列表
//            val grantedList = ArrayList<String>()
//            // 本次请求 需要显示原因的权限，被拒绝后可以显示原因重新发起请求
//            val showReasonList = ArrayList<String>()
//            // 本次请求 永久拒绝的权限，此时只能提示用户去设置打开
//            val forwardList = ArrayList<String>()
//            //权限请求结果
//            for ((index, result) in grantResults.withIndex()){
//                if (result == PackageManager.PERMISSION_GRANTED){
//                    grantedList.add(permissions[index])
//                    //删除保存的拒绝权限和永久拒绝权限
//                    permissionBuilder.deniedPermissions.remove(permissions[index])
//                    permissionBuilder.permanentDeniedPermissions.remove(permissions[index])
//                } else {
//                    //是否需要说明原因
//                    /**
//                     * shouldShowRequestPermissionRationale
//                     * 引用网络：
//                     * 1，在允许询问时返回true ；
//                     * 2，在权限通过 或者权限被拒绝并且禁止询问时返回false
//                     * 但是有一个例外，就是从来没有询问过的时候，也是返回的false 所以单纯的使用shouldShowRequestPermissionRationale去做什么判断，是没用的，
//                     * 只能在请求权限回调后再使用。
//                     * Google的原意是：
//                     * 1，没有申请过权限，申请就是了，所以返回false；
//                     * 2，申请了用户拒绝了，那你就要提示用户了，所以返回true；
//                     * 3，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
//                     * 4，已经允许了，不需要申请也不需要提示，所以返回false
//                     */
//                    /**
//                     * 申请了用户拒绝了，那你就要提示用户了，所以返回true；
//                     * 此时可以重新申请 或者 提示
//                     */
//                    val shouldShowReason = shouldShowRequestPermissionRationale(permissions[index])
//                    if (shouldShowReason){
//                        // 被拒绝
//                        showReasonList.add(permissions[index])
//                        permissionBuilder.deniedPermissions.add(permissions[index])
//                    } else {
//                        // 被永久拒绝
//                        forwardList.add(permissions[index])
//                        permissionBuilder.permanentDeniedPermissions.add(permissions[index])
//                        permissionBuilder.deniedPermissions.remove(permissions[index])
//                    }
//                }
//            }
//            // 每次请求后 刷新 权限集
//            // 因为用户随时可能更改权限
//            permissionBuilder.grantedPermissions.clear()
//            permissionBuilder.grantedPermissions.addAll(grantedList)
//            val allGranted = permissionBuilder.grantedPermissions.size == permissionBuilder.allPermissions.size
//            if (allGranted){  // 所有权限都请求成功
//                requestCallback(true, permissionBuilder.allPermissions, listOf() )
//            } else {
//                // 用户拒绝了全部或者部分权限
//                /**
//                 * goesToRequestCallback  是否立即回调到最终 requestCallback
//                 * true : 立即回调到 requestCallback 显示最终结果
//                 * false : 将回调交给 explainReasonCallback 或者  forwardToSettingsCallback 处理，用户可能还需要其他操作
//                 */
//                var goesToRequestCallback = true
//                // 判断是否需要说明原因
//                if ((explainReasonCallback != null || explainReasonCallback2 != null) && showReasonList.isNotEmpty()){
//                    // 这时候会走请求原因的回调，就不走请求结果回调了
//                    goesToRequestCallback = false
//                    explainReasonCallback2?.let {
//                        permissionBuilder.explainReasonScope.it(showReasonList, false)
//                    }?:
//                    explainReasonCallback?.let {
//                        permissionBuilder.explainReasonScope.it(showReasonList)
//                    }
//                }
//                // forwardToSettingsCallback and forwardList 不为空的话，需要跳转至设置页面
//                else if (forwardToSettingsCallback != null && forwardList.isNotEmpty()){
//                    goesToRequestCallback = false
//                    forwardToSettingsCallback?.let {
//                        permissionBuilder.forwardToSettingsScope.it(forwardList)
//                    }
//                }
//                // 需要 回调最终结果给开发者
//                // 如果没有设置 explainReasonCallback2 or explainReasonCallback 的情况下全都回调 requestCallback
//                // 如果设置了  explainReasonCallback2 or explainReasonCallback  但是没有设置 showRequestReasonDialog 或 showForwardToSettingsDialog 的时候也回调到 requestCallback
//                if (goesToRequestCallback || !permissionBuilder.showDialogCalled){
//                    val deniedList = ArrayList<String>()
//                    deniedList.addAll(permissionBuilder.deniedPermissions)
//                    deniedList.addAll(permissionBuilder.permanentDeniedPermissions)
//                    requestCallback(false, permissionBuilder.grantedPermissions.toList(), deniedList)
//                }
//
//            }
//        }
//    }
//
//    /**
//     * 在设置页更改权限之后返回结果
//     */
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == SETTINGS_CODE){
//            if (::permissionBuilder.isInitialized){
//                // 如果 permissionBuilder 已初始化 直接重新请求
//                // 某些手机 从设置切回来 permissionBuilder 可能未初始化
//                permissionBuilder.requestAgain(permissionBuilder.forwardPermissions)
//            }
//        }
//    }
//}