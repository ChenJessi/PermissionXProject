package com.jessi.permissionx.request

import com.jessi.permissionx.PermissionBuilder

/**
 *  一些请求的公共代码逻辑
 */
internal abstract class BaseTask(@JvmField var pb : PermissionBuilder) : ChainTask {

    /**
     * 下一个任务
     * 当前任务完成之后要执行的下个任务，如果没有下个任务，则请求过程结束
     */
    @JvmField
    var next : ChainTask? = null

    /**
     * Provide specific scopes for explainReasonCallback for specific functions to call.
     */
    private var explainReasonScope = ExplainScope(pb, this)

    override fun getExplainScope() = explainReasonScope

    /**
     * Provide specific scopes for forwardToSettingsCallback for specific functions to call.
     */
    private var forwardToSettingsScope = ForwardScope(pb, this)

    override fun getForwardScope() = forwardToSettingsScope

    override fun finish() {

    }
}