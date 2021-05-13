package com.lkl.dynamicfeature;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sankuai.waimai.router.annotation.RouterPage;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;

@RouterPage(path = DemoConstant.TEST_DYNAMIC_FEATURE1)
public class DynamicFeatureActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_feature1);
    }
}