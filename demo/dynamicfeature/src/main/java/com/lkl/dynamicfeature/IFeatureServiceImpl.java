package com.lkl.dynamicfeature;

import android.content.Context;

import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sankuai.waimai.router.demo.feature.IFeatureService;

@RouterService(interfaces = IFeatureService.class, key = "/factory")
public class IFeatureServiceImpl implements IFeatureService {

    private final String mName;

    @RouterProvider
    public static IFeatureServiceImpl provideService() {
        return new IFeatureServiceImpl("CreateByProvider");
    }

    public IFeatureServiceImpl() {
        mName = "CreateWithEmptyArgs";
    }

    public IFeatureServiceImpl(Context context) {
        mName = "CreateWithContext";
    }

    public IFeatureServiceImpl(String name) {
        mName = name;
    }

    @Override
    public String name() {
        return mName;
    }
}
