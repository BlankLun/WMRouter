package com.sankuai.waimai.router.common;


import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.core.UriResult;

import androidx.annotation.NonNull;

/**
 * 不支持的跳转链接，返回 {@link UriResult#CODE_NOT_FOUND}
 * <p>
 * Created by jzj on 2018/3/22.
 */

public class NotFoundHandler extends UriHandler {

    public static final NotFoundHandler INSTANCE = new NotFoundHandler();

    @Override
    public boolean shouldHandle(@NonNull String moduleName, @NonNull UriRequest request) {
        return true;
    }

    @Override
    protected void handleInternal(@NonNull String moduleName, @NonNull UriRequest request, @NonNull UriCallback callback) {
        callback.onComplete(UriResult.CODE_NOT_FOUND);
    }

    @Override
    public String toString() {
        return "NotFoundHandler";
    }
}
