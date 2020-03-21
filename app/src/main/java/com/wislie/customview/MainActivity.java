package com.wislie.customview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.wislie.customview.huawei.activity.HwActivity;
import com.wislie.customview.vivo.activity.VivoActivity;
import com.wislie.hwoptimizelayout.R;


/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-03-12 12:56
 * desc   :
 * version: 1.0
 */
public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

   public void  onHwClick(View view){
        startActivity(new Intent(this, HwActivity.class));
   }

    public void  onVivoClick(View view){
        startActivity(new Intent(this, VivoActivity.class));
    }
}
