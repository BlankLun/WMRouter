package com.sankuai.waimai.router.demo.advanced.abtest;

import android.content.Intent;

import com.sankuai.waimai.router.activity.AbsActivityHandler;
import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;

import androidx.annotation.NonNull;

@RouterUri(path = DemoConstant.HOME_AB_TEST)
public class HomeABTestHandler extends AbsActivityHandler {

    @NonNull
    @Override
    protected Intent createIntent(@NonNull UriRequest request) {
        if (FakeABTestService.getHomeABStrategy().equals("A")) {
            return new Intent(request.getContext(), HomeActivityA.class);
        } else {
            return new Intent(request.getContext(), HomeActivityB.class);
        }
    }
}
