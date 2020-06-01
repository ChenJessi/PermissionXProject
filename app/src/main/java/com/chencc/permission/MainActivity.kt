package com.chencc.permission

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.chencc.permissionx.PermissionX
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private  val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView.setOnClickListener {
            PermissionX.init(this)
                .permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .explainReasonCallback{deniedlist ->
                    Log.e(TAG, "explainReasonCallback:  ${deniedlist}" )
                }
                .onForwardToSettings { deniedlist ->
                    Log.e(TAG, "explainReasonCallback:  ${deniedlist}" )
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted){
                        Toast.makeText(this@MainActivity, "allGranted", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@MainActivity, "denied :  $deniedList", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}