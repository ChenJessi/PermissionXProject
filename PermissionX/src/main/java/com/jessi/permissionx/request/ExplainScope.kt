package com.jessi.permissionx.request

import com.jessi.permissionx.PermissionBuilder

/**
 * 封装了  ExplainReasonCallback 和 ExplainReasonCallbackWithBeforeParam
 * 显示相关 dialog 所需要调用的方法
 *
 * @property pb PermissionBuilder
 * @property chainTask ChainTask
 * @constructor
 */
class ExplainScope internal constructor(
    private val pb: PermissionBuilder,
    private val chainTask: ChainTask
    ){
}