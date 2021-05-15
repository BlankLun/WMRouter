package com.lkl.dynamicfeature;

import android.net.Uri;
import android.util.Log;

import com.sankuai.waimai.router.common.UriParamInterceptor;
import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriInterceptor;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.utils.RouterUtils;

import java.util.Map;

import androidx.annotation.NonNull;

/**
 * 给uri添加参数。{@link UriRequest} 可设置 {@link UriParamInterceptor#FIELD_URI_APPEND_PARAMS}, 会被自动添加到uri中。
 * Created by jzj on 2018/1/29.
 */
public class TestUriParamInterceptor implements UriInterceptor {
    private static final String TAG = "TestUriParamInterceptor";
    @Override
    public void intercept(@NonNull UriRequest request, @NonNull UriCallback callback) {
        appendParams(request);
        Log.e(TAG, "intercept: " + request);
        callback.onNext();
    }

    @SuppressWarnings("unchecked")
    protected void appendParams(@NonNull UriRequest request) {
        final Map<String, String> extra = request.getField(
                Map.class, UriParamInterceptor.FIELD_URI_APPEND_PARAMS);
        if (extra != null) {
            Uri uri = RouterUtils.appendParams(request.getUri(), extra);
            request.setUri(uri);
        }
    }
}
