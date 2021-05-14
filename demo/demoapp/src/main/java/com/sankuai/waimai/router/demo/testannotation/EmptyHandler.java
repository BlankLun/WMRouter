package com.sankuai.waimai.router.demo.testannotation;


import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriRequest;

import androidx.annotation.NonNull;

/**
 * Created by jzj on 2018/3/26.
 */

public class EmptyHandler extends UriHandler {

    @Override
    protected boolean shouldHandle(@NonNull String moduleName, @NonNull UriRequest request) {
        return false;
    }

    @Override
    protected void handleInternal(@NonNull String moduleName, @NonNull UriRequest request, @NonNull UriCallback callback) {

    }
}
