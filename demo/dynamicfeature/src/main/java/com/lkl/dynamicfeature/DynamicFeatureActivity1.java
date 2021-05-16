package com.lkl.dynamicfeature;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.sankuai.waimai.router.annotation.RouterPage;
import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;

@RouterPage(path = DemoConstant.TEST_DYNAMIC_FEATURE1, interceptors = TestUriParamInterceptor.class)
@RouterUri(path = DemoConstant.TEST_DYNAMIC_FEATURE2, interceptors = TestUriParamInterceptor.class)
public class DynamicFeatureActivity1 extends AppCompatActivity {
    private TextView mTestBundleResRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_feature);

        mTestBundleResRef = findViewById(R.id.testBundleResRef);

        mTestBundleResRef.setText(com.sankuai.waimai.router.demo.lib2.R.string.bundle_module_res_ref);
    }
}