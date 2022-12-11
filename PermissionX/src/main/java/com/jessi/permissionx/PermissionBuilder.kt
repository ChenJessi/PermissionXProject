package com.jessi.permissionx

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.PersistableBundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.jessi.permissionx.dialog.DefaultDialog
import com.jessi.permissionx.dialog.RationaleDialog
import com.jessi.permissionx.request.RequestInstallPackagesPermission
import com.jessi.permissionx.request.RequestManageExternalStoragePermission
import com.jessi.permissionx.request.RequestNormalPermissions
import com.jessi.permissionx.request.*
import com.jessi.permissionx.request.RequestBackgroundLocationPermission
import com.jessi.permissionx.request.RequestBodySensorsBackgroundPermission
import kotlin.collections.LinkedHashSet

/**
 * 提供 PermissionX 的 api
 *
 * @property normalPermissionSet 需要申请的普通权限集
 * @property specialPermissionsSet 需要申请的特殊权限集
 */
class PermissionBuilder(
    fragmentActivity: FragmentActivity?,
    fragment: Fragment?,
    normalPermissionSet: MutableSet<String>,
    specialPermissionsSet: MutableSet<String>
) {

    lateinit var activity: FragmentActivity

    private var fragment: Fragment? = null

    // 想要请求的普通权限
    var normalPermissions = mutableSetOf<String>()

    // 想要请求的特殊权限集合
    var specialPermissions = mutableSetOf<String>()

    init {
        fragmentActivity?.let {
            activity = it
        }
        if (fragmentActivity == null && fragment != null) {
            activity = fragment.requireActivity()
        }
        this.fragment = fragment
        this.normalPermissions = normalPermissionSet
        this.specialPermissions = specialPermissionsSet
    }

    /**
     * 表示 [ExplainScope.showRequestReasonDialog] or [ForwardScope.showForwardToSettingsDialog]
     *  在 [.onExplainRequestReason] or [.onForwardToSettings] 回调中调用.
     * 如果没有调用，则将自动调用 [PermissionX.requestCallback]
     */
    @JvmField
    var showDialogCalled = false


    // 是否在请求之前展示请求原因都弹窗
    @JvmField
    internal var explainReasonBeforeRequest = false

    /**
     * onExplainRequestReason 的回调
     * 请求拒绝之后解释请求原因的回调
     * ExplainScope
     * List<String>)->Unit deniedList 拒绝的权限列表，应当解释要获取这些权限的原因
     */
    @JvmField
    var explainReasonCallback: ((ExplainScope, List<String>) -> Unit)? = null

    /**
     * onExplainRequestReason 的回调
     * 解释请求原因的参数
     * ExplainScope
     * List<String>)->Unit deniedList 拒绝的权限列表，应当解释要获取这些权限的原因
     * Boolean 是否是请求之前  true 请求之前， false 请求之后
     */
    @JvmField
    var explainReasonCallbackWithBeforeParam: ((ExplainScope, List<String>, Boolean) -> Unit)? =
        null

    @JvmField
    var requestCallback: ((Boolean, List<String>, List<String>) -> Unit)? = null

    /**
     * The origin request orientation of the current Activity. We need to restore it when
     * permission request finished.
     */
    private var originRequestOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    /**
     * 记录已授予的权限
     */
    val grantedPermissions: MutableSet<String> = LinkedHashSet()

    /**
     * 拒绝的权限集合
     */
    val deniedPermissions: MutableSet<String> = LinkedHashSet()

    /**
     * 永久拒绝的权限集合
     */
    val permanentDeniedPermissions: MutableSet<String> = LinkedHashSet()

    /**
     * 永久拒绝的权限
     */
    val tempPermanentDeniedPermissions: MutableSet<String> = LinkedHashSet()


    /**
     * [onForwardToSettings] 方法的回调
     * 部分权限或者永久拒绝的权限需要跳转到设置里手动打开
     */
    @JvmField
    var forwardToSettingsCallback: ((ForwardScope, deniedList: List<String>) -> Unit)? = null

    /**
     * 亮色模式[DefaultDialog]]弹窗文本颜色
     */
    private var lightColor = -1;

    /**
     * 夜间模式下 [DefaultDialog]弹窗的文本颜色
     */
    private var darkColor = -1;

    /**
     * 当前向用户展示的dialog
     * 当 [InvisibleFragment] 被销毁时，我们需要关闭对话框
     */
    private var currentDialog : Dialog? = null

    /**
     * 需要跳转到设置打开到权限
     */
    var forwardPermissions: MutableSet<String> = LinkedHashSet()


    private val fragmentManager: FragmentManager
        get() {
            return fragment?.childFragmentManager ?: activity.supportFragmentManager
        }

    /**
     * 无界面InvisibleFragment 用于请求权限
     */
    private val invisibleFragment: InvisibleFragment
        get() {
            val existedFragment = fragmentManager.findFragmentByTag(InvisibleFragment.FRAGMENT_TAG)
            return if (existedFragment != null) {
                existedFragment as InvisibleFragment
            } else {
                val invisibleFragment = InvisibleFragment()
                fragmentManager.beginTransaction()
                    .add(invisibleFragment, InvisibleFragment.FRAGMENT_TAG)
                    .commitNowAllowingStateLoss()
                invisibleFragment
            }
        }

    /**
     * @param allGranted 是否授予了所有权限,
     * @param grantedList 已授予的权限列表,
     * @param deniedList 已拒绝的权限列表
     * @param callback allGranted, grantedList, deniedList
     * @param callback Function3<Boolean, List<String>, List<String>, Unit>
     */
    fun request(callback: (allGranted :Boolean, grantedList:List<String>, deniedList:List<String>) -> Unit) {
        requestCallback = callback;
        startRequest()
    }


    /**
     * 立即请求权限
     * @param permissions Set<String>  权限集合
     * @param chainTask ChainTask 当前的请求任务
     */
    fun requestNow(permissions: Set<String>, chainTask: ChainTask) {
        invisibleFragment.requestNow(this, permissions, chainTask)
    }


    fun explainReasonBeforeRequest() : PermissionBuilder{
        explainReasonBeforeRequest = true
        return this
    }

    /**
     * 当权限需要解释请求原因时调用
     * 一般当用户拒绝请求时，都需要调用该方法
     * 如果调用了 [explainReasonBeforeRequest] 则此方法会在请求之前调用
     * @param block Function2<ExplainScope, List<String>, Unit>
     * @return PermissionBuilder
     */
    fun onExplainRequestReason(block: (ExplainScope, List<String>) -> Unit): PermissionBuilder {
        this.explainReasonCallback = block
        return this
    }

    /**
     * 当权限需要解释请求原因时调用
     * 一般当用户拒绝请求时，都需要调用该方法
     * 如果调用了 [explainReasonBeforeRequest] 则此方法会在请求之前调用
     * beforeRequest:该方法位于请求回调之前 or 之后
     * @param block Function2<ExplainScope, List<String>, Boolean ,Unit>
     *     Function2<ExplainScope, deniedList, beforeRequest>
     * @return PermissionBuilder
     */
    fun onExplainRequestReason(block: (ExplainScope, deniedlist:List<String>, beforeRequest:Boolean) -> Unit): PermissionBuilder {
        this.explainReasonCallbackWithBeforeParam = block
        return this
    }


    /**
     * 某些权限和永久拒绝的权限需要跳转到设置页面手动打开
     * @param block Function2<ForwardScope, [@kotlin.ParameterName] List<String>, Unit>
     * 注意[onExplainRequestReason] 优先级高于此方法
     * 如果调用了[onExplainRequestReason]，则不会再调用此方法
     * @return PermissionBuilder
     */
    fun onForwardToSettings(block: ((ForwardScope, deniedList: List<String>) -> Unit)): PermissionBuilder {
        forwardToSettingsCallback = block
        return this
    }


    private fun startRequest() {
        // Lock the orientation when requesting permissions, or callback maybe missed due to
        // activity destroyed.
        lockOrientation()

        // 创建请求任务链， 先请求 RequestNormalPermissions 普通权限，再请求 RequestBackgroundLocationPermission
        val requestChain = RequestChain();
        requestChain.addTaskToChain(RequestNormalPermissions(this))
        requestChain.addTaskToChain(RequestBackgroundLocationPermission(this))
        requestChain.addTaskToChain(RequestManageExternalStoragePermission(this))
        requestChain.addTaskToChain(RequestInstallPackagesPermission(this))
        requestChain.addTaskToChain(RequestBodySensorsBackgroundPermission(this))

        requestChain.runTask()
    }


    /**
     * Lock the screen orientation. Activity couldn't rotate with sensor.
     * Android O has bug that only full screen activity can request orientation,
     * so we need to exclude Android O.
     */
    private fun lockOrientation() {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            originRequestOrientation = activity.requestedOrientation
            val orientation = activity.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        }
    }


    /**
     *  是否需要发送通知权限
     *  @return 如果需要发送通知权限，返回true， 否则返回false
     */
    fun shouldRequestNotificationPermission() =
        specialPermissions.contains(PermissionX.POST_NOTIFICATIONS)

    /**
     * 设置请求弹窗的文本颜色
     * @param lightColor Int
     * @param darkColor Int
     * @return PermissionBuilder
     */
    fun setDialogTintColor(lightColor: Int, darkColor: Int): PermissionBuilder {
        this.lightColor = lightColor
        this.darkColor = darkColor
        return this
    }

    /**
     * 展示弹窗并且解释请求权限的原因
     * @param chainTask ChainTask 当前执行的请求任务
     * @param showReasonOrGoSettings Boolean  再次请求或者跳转到设置
     * @param permissions List<String> 再次请求的权限
     * @param message String 向用户解释请求的原因
     * @param positiveText String 确定按钮的文本，点击之后会再次请求
     * @param negativeText String? 取消按钮的文本，如果弹窗不允许取消，可能为 null
     */
    fun showHandlePermissionDialog(
        chainTask: ChainTask,
        showReasonOrGoSettings: Boolean,
        permissions: List<String>,
        message: String,
        positiveText: String,
        negativeText: String?
    ) {
        val defaultDialog = DefaultDialog(
            activity,
            permissions,
            message,
            positiveText,
            negativeText,
            lightColor,
            darkColor
        )
        showHandlePermissionDialog(chainTask, showReasonOrGoSettings, defaultDialog)
    }

    /**
     * 展示弹窗并且解释请求权限的原因
     * @param chainTask ChainTask 当前执行的请求任务
     * @param showReasonOrGoSettings Boolean  再次请求或者跳转到设置
     * @param dialog RationaleDialog 弹出对话框展示请求权限的原因
     */
    fun showHandlePermissionDialog(
        chainTask: ChainTask,
        showReasonOrGoSettings: Boolean,
        dialog: RationaleDialog
    ) {
        showDialogCalled = true
        val permissions = dialog.getPermissionsToRequest()
        if(permissions.isEmpty()){
            chainTask.finish()
            return
        }
        currentDialog = dialog
        dialog.show()
        if(dialog is DefaultDialog && dialog.isPermissionLayoutEmpty()){
            // dialog 没有有效的权限
            dialog.dismiss()
            chainTask.finish()
        }

        val positiveButton = dialog.getPositiveButton()
        val negativeButton = dialog.getNegativeButton()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        positiveButton.isClickable = true
        positiveButton.setOnClickListener {
            dialog.dismiss()
            if(showReasonOrGoSettings){
                chainTask.requestAgain(permissions)
            }else{
                forwardToSettings(permissions)
            }
        }

        if(negativeButton != null){
            negativeButton.isClickable = true
            negativeButton.setOnClickListener {
                dialog.dismiss()
                chainTask.finish()
            }
        }
        currentDialog?.setOnDismissListener {
            currentDialog = null
        }

    }


    /**
     * 跳转到设置页面手动打开权限
     * @param permissions List<String>
     */
    private fun forwardToSettings(permissions : List<String>){
        forwardPermissions.clear()
        forwardPermissions.addAll(permissions)
        invisibleFragment.forwardToSettings()
    }
}