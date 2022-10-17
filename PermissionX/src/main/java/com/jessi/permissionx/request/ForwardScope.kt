package com.jessi.permissionx.request

import com.jessi.permissionx.PermissionBuilder

class ForwardScope internal constructor(
    private val pb: PermissionBuilder,
    private val chainTask: ChainTask
){
}