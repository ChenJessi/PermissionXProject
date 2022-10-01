package com.chencc.permissionx.request


/**
 * 请求权限逻辑的任务链
 */
class RequestChain {

    /**
     * 第一个请求的任务
     */
    private var headTask : BaseTask? = null

    /**
     *  最后一个请求的任务
     */
    private var tailTask : BaseTask? = null

    /**
     * 添加一个请求任务
     * @param task BaseTask
     */
    internal fun addTaskToChain(task: BaseTask){
        if(headTask == null){
            headTask = task
        }
        else {
            task.next = tailTask
            tailTask = task
        }
//        tailTask?.next = task
//        tailTask = task
    }

    /**
     * 开始请求任务
     */
    internal fun runTask(){
        headTask?.request()
    }
}