package com.jessi.permissionx.request

import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.jessi.permissionx.PermissionBuilder

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
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ grantResults ->
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


    private fun postForResult(callback: ()->Unit){
        handler.post {
            callback()
        }
    }


    /**
     * 处理普通权限请求的结果
     * @param grantResults Map<String, Boolean>
     */
    private fun onRequestNormalPermissionsResult(grantResults: Map<String, Boolean>) {


    }
}