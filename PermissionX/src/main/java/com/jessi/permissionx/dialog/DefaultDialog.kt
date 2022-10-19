package com.jessi.permissionx.dialog

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import com.chencc.permissionx.R
import com.chencc.permissionx.databinding.PermissionxDefaultDialogLayoutBinding

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


    private fun buildPermissionsLayout() {
        TODO("Not yet implemented")
    }



    private fun setupWindow() {
        TODO("Not yet implemented")
    }



    /**
     *  当前是否处于夜间主题
     */
    private fun isDarkTheme(): Boolean {
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

}