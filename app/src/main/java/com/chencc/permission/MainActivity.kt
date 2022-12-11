package com.chencc.permission

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.jessi.permissionx.PermissionBuilder
import com.jessi.permissionx.PermissionX

class MainActivity : AppCompatActivity() {
    private  val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.textView)
        textView.setOnClickListener {
//            PermissionX.init(this)
//                .permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
//                .explainReasonBeforeRequest()
//                .explainReasonCallback{deniedlist ->
//                    Log.e(TAG, "explainReasonCallback:  ${deniedlist}" )
//                    showRequestReasonDialog(deniedlist, "申请的权限为应用必须的权限。", "确定", "取消")
//                }
//                .onForwardToSettings { deniedlist ->
//                    Log.e(TAG, "onForwardToSettings:  ${deniedlist}" )
//                    showForwardToSettingsDialog(deniedlist, "申请的权限为应用必须的权限。", "确定", "取消")
//                }
//                .request { allGranted, grantedList, deniedList ->
//                    if (allGranted){
//                        Toast.makeText(this@MainActivity, "allGranted", Toast.LENGTH_LONG).show()
//                    } else {
//                        Toast.makeText(this@MainActivity, "denied :  $deniedList", Toast.LENGTH_LONG).show()
//                    }
//                }
             PermissionX.init(this)
                 .permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                 .explainReasonBeforeRequest()
                 .onExplainRequestReason { explainScope, deniedlist, b ->
                     Log.e(TAG, "explainReasonCallback:  ${deniedlist}" )
                     explainScope.showRequestReasonDialog(deniedlist, "请求权限的原因", "确定")
                 }
                 .onForwardToSettings { forwardScope, deniedList ->
                     Log.e(TAG, "explainReasonCallback:  ${deniedList}" )
                     forwardScope.showForwardToSettingsDialog(deniedList, "跳转到设置打开权限", "确定")
                 }
                 .request { allGranted, grantedList, deniedList ->
                     Log.e(TAG, "request:  ${allGranted}  ${grantedList} ${deniedList}" )
                 }
        }
    }
}