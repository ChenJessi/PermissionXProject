package com.jessi.permissionx.dialog

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.chencc.permissionx.R
import com.chencc.permissionx.databinding.PermissionxDefaultDialogLayoutBinding
import com.chencc.permissionx.databinding.PermissionxPermissionItemBinding
import com.jessi.permissionx.constant.*

/**
 * 默认的信息提示框
 */
class DefaultDialog(
    context: Context,
    private val permissions : List<String>,
    private val message : String,
    private val positiveText: String,
    private val negativeText: String?,
    private val lightColor: Int,
    private val darkColor: Int
) : RationaleDialog(context, R.style.PermissionXDefaultDialog) {


    private lateinit var binding: PermissionxDefaultDialogLayoutBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PermissionxDefaultDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupText()
        buildPermissionsLayout()
        setupWindow()
    }

    override fun getPositiveButton(): View {
        return binding.positiveBtn
    }

    /**
     * negativeText 为null 时返回null，此时请求的权限是必须的
     * @return View?
     */
    override fun getNegativeButton(): View? {
        return negativeText?.let {
            binding.negativeBtn
        }
    }

    override fun getPermissionsToRequest(): List<String> {
        return permissions
    }

    /**
     * 检查权限布局是否为null
     * @return Boolean
     */
    internal fun isPermissionLayoutEmpty() = binding.permissionsLayout.childCount == 0


    /**
     * 设置对话框的文本和样式
     */
    private fun setupText() {
        binding.messageText.text = message
        binding.positiveBtn.text = positiveText
        if(negativeText != null){
            binding.negativeLayout.visibility = View.VISIBLE
            binding.negativeBtn.text = negativeText
        }else {
            binding.negativeLayout.visibility = View.GONE
        }
        if(isDarkTheme()){
            if(darkColor != -1){
                binding.positiveBtn.setTextColor(darkColor)
                binding.negativeBtn.setTextColor(darkColor)
            }
        }else{
            if (lightColor != -1) {
                binding.positiveBtn.setTextColor(lightColor)
                binding.negativeBtn.setTextColor(lightColor)
            }
        }
    }

    /**
     * 将需要解释请求原因的权限添加到dialog
     */
    private fun buildPermissionsLayout() {
        val tempSet = HashSet<String>()
        val currentVersion = Build.VERSION.SDK_INT
        for (permission in permissions){
            /**
             * 判断权限属于哪个权限组
             */
            val permissionGroup = when{
                currentVersion < Build.VERSION_CODES.Q -> {
                    try {
                        val permissionInfo = context.packageManager.getPermissionInfo(permission, 0)
                        permissionInfo.group
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                        null
                    }
                }
                currentVersion == Build.VERSION_CODES.Q -> permissionMapOnQ[permission]
                currentVersion == Build.VERSION_CODES.R -> permissionMapOnR[permission]
                currentVersion == Build.VERSION_CODES.S -> permissionMapOnS[permission]
                currentVersion == Build.VERSION_CODES.TIRAMISU -> permissionMapOnT[permission]
                else -> {
                    /**
                     * 如果版本高于当前的最新版本，那么使用当前的最新版本权限组判断
                     * 新版本需要升级适配
                     */
                    permissionMapOnT[permission]
                }
            }
            /**
             * 特殊权限或权限组没有添加到临时map
             * 根据权限，显示不同的提示文案
             */
            if((permission in allSpecialPermissions && !tempSet.contains(permission))
                || (permissionGroup != null && !tempSet.contains(permissionGroup))){
                val itemBinding = PermissionxPermissionItemBinding.inflate(layoutInflater, binding.permissionsLayout, false)

                when{
                    permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                        itemBinding.permissionText.text = context.getString(R.string.permissionx_access_background_location)
                        itemBinding.permissionIcon.setImageResource(context.packageManager.getPermissionGroupInfo(permissionGroup!!, 0).icon)
                    }
                    permission == Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                        itemBinding.permissionText.text = context.getString(R.string.permissionx_system_alert_window)
                        itemBinding.permissionIcon.setImageResource(R.drawable.permissionx_ic_alert)
                    }
                    permission == Manifest.permission.WRITE_SETTINGS -> {
                        itemBinding.permissionText.text = context.getString(R.string.permissionx_write_settings)
                        itemBinding.permissionIcon.setImageResource(R.drawable.permissionx_ic_setting)
                    }
                    permission == Manifest.permission.MANAGE_EXTERNAL_STORAGE -> {
                        itemBinding.permissionText.text = context.getString(R.string.permissionx_manage_external_storage)
                        itemBinding.permissionIcon.setImageResource(context.packageManager.getPermissionGroupInfo(permissionGroup!!, 0).icon)
                    }
                    permission == Manifest.permission.REQUEST_INSTALL_PACKAGES -> {
                        itemBinding.permissionText.text = context.getString(R.string.permissionx_request_install_packages)
                        itemBinding.permissionIcon.setImageResource(R.drawable.permissionx_ic_install)
                    }
                    permission == Manifest.permission.POST_NOTIFICATIONS
                            && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                        // When OS version is lower than Android 13, there isn't a notification icon or labelRes for us to get.
                        // So we need to handle it as special permission's way.
                        itemBinding.permissionText.text = context.getString(R.string.permissionx_post_notification)
                        itemBinding.permissionIcon.setImageResource(R.drawable.permissionx_ic_notification)
                    }
                    permission == Manifest.permission.BODY_SENSORS_BACKGROUND -> {
                        itemBinding.permissionText.text = context.getString(R.string.permissionx_body_sensor_background)
                        itemBinding.permissionIcon.setImageResource(context.packageManager.getPermissionGroupInfo(permissionGroup!!, 0).icon)
                    }
                    else -> {
                        itemBinding.permissionText.text = context.getString(context.packageManager.getPermissionGroupInfo(permissionGroup!!, 0).labelRes)
                        itemBinding.permissionIcon.setImageResource(context.packageManager.getPermissionGroupInfo(permissionGroup, 0).icon)
                    }
                }
                if (isDarkTheme()){
                    if (darkColor != -1) {
                        itemBinding.permissionIcon.setColorFilter(darkColor, PorterDuff.Mode.SRC_ATOP)
                    }
                } else {
                    if (lightColor != -1) {
                        itemBinding.permissionIcon.setColorFilter(lightColor, PorterDuff.Mode.SRC_ATOP)
                    }
                }
                binding.permissionsLayout.addView(itemBinding.root)
                tempSet.add(permissionGroup ?: permission)
            }
        }
    }


    /**
     * 设置窗口大小
     */
    private fun setupWindow() {
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        if(width < height){
            // 竖向
            window?.let {
                val param = it.attributes
                it.setGravity(Gravity.CENTER)
                param.width = (width * 0.86).toInt()
                it.attributes = param
            }
        }
        else {
            // 横向
            window?.let {
                val param = it.attributes
                it.setGravity(Gravity.CENTER)
                param.width = (width * 0.6).toInt()
                it.attributes = param
            }
        }
    }



    /**
     *  当前是否处于夜间主题
     */
    private fun isDarkTheme(): Boolean {
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

}