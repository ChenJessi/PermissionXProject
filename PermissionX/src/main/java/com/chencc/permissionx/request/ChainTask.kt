package com.chencc.permissionx.request

/**
 * 请求权限的任务的接口
 * 我们无法一次请求所有权限，有些特殊权限需要单独请求的处理
 * 所有的权限请求都实现该接口
 */
interface ChainTask {

    /**
     * 执行请求的逻辑
     */
    fun request()

    /**
     * 当用户拒绝时，再次请求该权限
     * @param permissions List<String>
     */
    fun requestAgain(permissions : List<String>)

    /**
     * 权限请求的任务已经完成
     */
    fun finish()
}