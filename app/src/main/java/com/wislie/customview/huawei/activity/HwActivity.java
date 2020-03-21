package com.wislie.customview.huawei.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wislie.customview.huawei.HWOptimizeLayout;
import com.wislie.customview.huawei.OnOptimizeListener;
import com.wislie.hwoptimizelayout.R;

/**
 * 仿华为的自定义view
 */
public class HwActivity extends AppCompatActivity {

    private HWOptimizeLayout hwOptimizeLayout;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hw);
        hwOptimizeLayout = findViewById(R.id.hw_optimize_layout);
        mButton = findViewById(R.id.btn_start);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hwOptimizeLayout.start();
            }
        });
        hwOptimizeLayout.setOnOptimizeListener(new OnOptimizeListener() {
            @Override
            public void startOptimize() {
                mButton.setText("暂停");
                hwOptimizeLayout.startAnim();
            }

            @Override
            public void pauseOptimize() {
                mButton.setText("继续");
                hwOptimizeLayout.pauseAnim();
            }

            @Override
            public void completeTask() {
                mButton.setText("开始");
            }
        });
    }

}
