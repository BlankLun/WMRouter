package com.sankuai.waimai.router.common;


import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriRequest;

import androidx.annotation.NonNull;

/**
 * Created by jzj on 2018/3/30.
 */

public class WrapperHandler extends UriHandler {

    private final UriHandler mDelegate;

    public WrapperHandler(UriHandler delegate) {
        mDelegate = delegate;
    }

    public UriHandler getDelegate() {
        return mDelegate;
    }

    @Override
    protected boolean shouldHandle(@NonNull String moduleName, @NonNull UriRequest request) {
        return true;
    }

    @Override
    protected void handleInternal(@NonNull String moduleName, @NonNull UriRequest request, @NonNull UriCallback callback) {
        mDelegate.handle(moduleName, request, callback);
    }

    @Override
    public String toString() {
        return "Delegate(" + mDelegate.toString() + ")";
    }
}
