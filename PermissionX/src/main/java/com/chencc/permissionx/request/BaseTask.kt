package com.chencc.permissionx.request

import com.chencc.permissionx.PermissionBuilder

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
}