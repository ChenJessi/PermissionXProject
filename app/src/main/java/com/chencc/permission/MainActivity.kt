package com.chencc.permission

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.chencc.permissionx.PermissionX
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView.setOnClickListener {
            PermissionX.init(this)
                .permissions(Manifest.permission.CAMERA)
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