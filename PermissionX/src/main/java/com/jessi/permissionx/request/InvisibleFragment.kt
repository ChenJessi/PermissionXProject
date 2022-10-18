package com.jessi.permissionx.request

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.jessi.permissionx.PermissionBuilder
import com.jessi.permissionx.PermissionX

class InvisibleFragment : Fragment() {
    companion object {
        /**
         * InvisibleFragment tag 用于创建和查找 InvisibleFragment
         */
        const val FRAGMENT_TAG = "InvisibleFragment"
    }

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var pb: PermissionBuilder

    /**
     * 当前正在请求的任务
     */
    private lateinit var task: ChainTask

    /**
     * 请求多个权限接收结果
     */
    private val requestNormalPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
            postForResult {
                onRequestNormalPermissionsResult(grantResults);
            }
        }


    /**
     *
     * @param permissionBuilder PermissionBuilder
     * @param permissions Set<String>
     * @param chainTask ChainTask
     */
    fun requestNow(
        permissionBuilder: PermissionBuilder,
        permissions: Set<String>,
        chainTask: ChainTask
    ) {
        pb = permissionBuilder
        task = chainTask
        requestNormalPermissionLauncher.launch(permissions.toTypedArray())
    }


    private fun postForResult(callback: () -> Unit) {
        handler.post {
            callback()
        }
    }


    /**
     * 处理普通权限请求的结果
     * @param grantResults Map<String, Boolean>
     */
    private fun onRequestNormalPermissionsResult(grantResults: Map<String, Boolean>) {
        if (checkForGC()) {
            // 清空已授予的权限
            // 安全起见，我们不能保留已授予的权限，用户有可能在设置中关闭这些权限
            // 每次请求时，都必须再次请求已授予的权限，并刷新已授予的权限集合
            pb.grantedPermissions.clear()

            // 需要展示请求原因的权限集合
            val showReasonList = mutableListOf<String>()
            // 已永久拒绝的权限集合
            val forwardList = mutableListOf<String>()
            for ((permission, granted) in grantResults) {
                if (granted) {
                    // 已授予的权限
                    pb.grantedPermissions.add(permission)
                    // 在 PermissionBuilder 的 deniedPermissions 和 permanentDeniedPermissions 集合中删除已授予的权限
                    pb.deniedPermissions.remove(permission)
                    pb.permanentDeniedPermissions.remove(permission)
                } else {
                    // 拒绝的权限
                    // 被拒绝的权限有可能变成永久拒绝的权限， 但永久拒绝但权限不能转为拒绝但权限
                    val shouldShowRationale = shouldShowRequestPermissionRationale(permission)
                    // 是否需要显示请求原因
                    if (shouldShowRationale) {
                        showReasonList.add(permission)
                        pb.deniedPermissions.add(permission)
                    } else {
                        // 权限被拒绝并且不需要 解释拒绝原因，说明该权限被永久拒绝
                        forwardList.add(permission)
                        pb.permanentDeniedPermissions.add(permission)
                        pb.deniedPermissions.remove(permission)
                    }
                }
            }
            // 对拒绝对权限再次校验
            // deniedPermissions 和 permanentDeniedPermissions
            val deniedPermissions = mutableListOf<String>()
            deniedPermissions.addAll(pb.deniedPermissions)
            deniedPermissions.addAll(pb.permanentDeniedPermissions)
            for (permission in deniedPermissions) {
                if (PermissionX.isGranted(requireContext(), permission)) {
                    pb.deniedPermissions.remove(permission)
                    pb.permanentDeniedPermissions.remove(permission)
                    pb.grantedPermissions.add(permission)
                }
            }

            val allGranted = pb.grantedPermissions.size == pb.normalPermissions.size
            if (allGranted) {
                // 所有权限都已经授权，结束当前任务
                task.finish()
            } else {
                // 当前任务是否要完成
                var shouldFinishTheTask = true

                if ((pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null) && showReasonList.isNotEmpty()) {
                    // 有 请求原因回调 并且 存在 需要展示请求原因的权限
                    shouldFinishTheTask = false;
                    if (pb.explainReasonCallbackWithBeforeParam != null) {
                        pb.explainReasonCallbackWithBeforeParam?.invoke(
                            task.getExplainScope(),
                            pb.deniedPermissions.toMutableList(),
                            false
                        )
                    } else {
                        pb.explainReasonCallback?.invoke(
                            task.getExplainScope(),
                            pb.deniedPermissions.toMutableList()
                        )
                    }

                    //todo
                    pb.tempPermanentDeniedPermissions.addAll(forwardList)
                } else if(pb.forwardToSettingsCallback != null && (forwardList.isNotEmpty() || pb.tempPermanentDeniedPermissions.isNotEmpty())){
                    shouldFinishTheTask = false
                    pb.tempPermanentDeniedPermissions.clear()
                    pb.forwardToSettingsCallback?.invoke(task.getForwardScope(), pb.permanentDeniedPermissions.toMutableList())

                }
                /**
                 * 如果没有调用 showRequestReasonDialog 或者 showForwardToSettingsDialog，直接结束当前任务
                 * 特殊情况:如果调用了[PermissionBuilder.onExplainRequestReason] 或 [PermissionBuilder.forwardToSettingsCallback],
                 * 但是没有调用 showRequestReasonDialog or showForwardToSettingsDialog，也结束当前任务
                 */
                if(shouldFinishTheTask || !pb.showDialogCalled){
                    task.finish()
                }
                //showDialogCalled 每次请求后都重置 showDialogCalled
                //
                pb.showDialogCalled = false
            }

        }

    }

    /**
     *  检测 PermissionBuilder ChainTask 是否有效，防止某些意外情况发生
     * @return Boolean
     */
    private fun checkForGC(): Boolean {

        if (!::pb.isInitialized || !::task.isInitialized) {
            Log.w(
                "PermissionX",
                "PermissionBuilder and ChainTask should not be null at this time, so we can do nothing in this case."
            )
            return false
        }
        return true
    }

}