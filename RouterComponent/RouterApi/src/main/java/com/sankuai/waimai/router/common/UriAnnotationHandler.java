package com.sankuai.waimai.router.common;

import android.text.TextUtils;

import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.components.RouterComponents;
import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriInterceptor;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.utils.LazyInitHelper;
import com.sankuai.waimai.router.utils.RouterUtils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * URI跳转，通过注解 {@link RouterUri} 配置，可处理多个Scheme+Host。
 * <p>
 * 接收到 {@link UriRequest} 时， {@link UriAnnotationHandler} 根据scheme+host产生的key，
 * 分发给对应的 {@link PathHandler}，{@link PathHandler} 再根据path分发给每个子节点。
 * <p>
 * Created by jzj on 2018/3/23.
 */
public class UriAnnotationHandler extends UriHandler {

    private static boolean sAddNotFoundHandler = true;

    /**
     * 设置是否在每个PathHandler上添加一个NotFoundHandler，默认为true。
     * 如果添加了NotFoundHandler，则只要scheme+host匹配上，所有的URI都会被PathHandler拦截掉，
     * 即使path不能匹配，也会分发到NotFoundHandler终止分发。
     */
    public static void setAddNotFoundHandler(boolean addNotFoundHandler) {
        sAddNotFoundHandler = addNotFoundHandler;
    }

    /**
     * key是由scheme+host生成的字符串，value是PathHandler。
     */
    private final Map<String, PathHandler> mMap = new HashMap<>();
    /**
     * {@link RouterUri} 没有指定scheme时，则使用这里设置的defaultScheme
     */
    private final String mDefaultScheme;
    /**
     * {@link RouterUri} 没有指定host时，则使用这里设置的defaultHost
     */
    private final String mDefaultHost;

    private final Map<String, LazyInitHelper> mInitHelpers = new HashMap<>();

    private class UriAnnotationLazyInitHelper extends LazyInitHelper {
        private final String mModuleName;

        public UriAnnotationLazyInitHelper(@NonNull String moduleName) {
            super("UriAnnotationHandler:" + moduleName);
            this.mModuleName = moduleName;
        }

        @Override
        protected void doInit() throws Throwable {
            initAnnotationConfig(mModuleName);
        }
    }

    @NonNull
    private LazyInitHelper getLazyInitHelper(@NonNull String moduleName) {
        LazyInitHelper lazyInitHelper = mInitHelpers.get(moduleName);
        if (lazyInitHelper == null) {
            synchronized (mInitHelpers) {
                lazyInitHelper = mInitHelpers.get(moduleName);
                if (lazyInitHelper == null) {
                    lazyInitHelper = new UriAnnotationLazyInitHelper(moduleName);
                    mInitHelpers.put(moduleName, lazyInitHelper);
                }
            }
        }
        return lazyInitHelper;
    }

    /**
     * @param defaultScheme {@link RouterUri} 没有指定scheme时，则使用这里设置的defaultScheme
     * @param defaultHost {@link RouterUri} 没有指定host时，则使用这里设置的defaultHost
     */
    public UriAnnotationHandler(@Nullable String defaultScheme, @Nullable String defaultHost) {
        mDefaultScheme = RouterUtils.toNonNullString(defaultScheme);
        mDefaultHost = RouterUtils.toNonNullString(defaultHost);
    }

    /**
     * @see LazyInitHelper#lazyInit()
     */
    public void lazyInit(@NonNull String moduleName) {
        getLazyInitHelper(moduleName).lazyInit();
    }

    protected void initAnnotationConfig(@NonNull String moduleName) throws ClassNotFoundException {
        RouterComponents.loadAnnotation(moduleName, this, IUriAnnotationInit.class);
    }

    public PathHandler getPathHandler(String scheme, String host) {
        return mMap.get(RouterUtils.schemeHost(scheme, host));
    }

    /**
     * 给指定scheme和host的节点设置path前缀
     */
    public void setPathPrefix(String scheme, String host, String prefix) {
        PathHandler pathHandler = getPathHandler(scheme, host);
        if (pathHandler != null) {
            pathHandler.setPathPrefix(prefix);
        }
    }

    /**
     * 给所有节点设置path前缀
     */
    public void setPathPrefix(String prefix) {
        for (PathHandler pathHandler : mMap.values()) {
            pathHandler.setPathPrefix(prefix);
        }
    }

    public void register(String scheme, String host, String path,
            Object handler, boolean exported, UriInterceptor... interceptors) {
        // 没配的scheme和host使用默认值
        if (TextUtils.isEmpty(scheme)) {
            scheme = mDefaultScheme;
        }
        if (TextUtils.isEmpty(host)) {
            host = mDefaultHost;
        }
        String schemeHost = RouterUtils.schemeHost(scheme, host);
        PathHandler pathHandler = mMap.get(schemeHost);
        if (pathHandler == null) {
            pathHandler = createPathHandler();
            mMap.put(schemeHost, pathHandler);
        }
        pathHandler.register(path, handler, exported, interceptors);
    }

    @NonNull
    protected PathHandler createPathHandler() {
        PathHandler pathHandler = new PathHandler();
        if (sAddNotFoundHandler) {
            pathHandler.setDefaultChildHandler(NotFoundHandler.INSTANCE);
        }
        return pathHandler;
    }

    /**
     * 通过scheme+host找对应的PathHandler，找到了才会处理
     */
    private PathHandler getChild(@NonNull UriRequest request) {
        return mMap.get(request.schemeHost());
    }

    @Override
    public void handle(@NonNull String moduleName, @NonNull UriRequest request, @NonNull UriCallback callback) {
        getLazyInitHelper(moduleName).ensureInit();
        super.handle(moduleName, request, callback);
    }

    @Override
    protected boolean shouldHandle(@NonNull String moduleName, @NonNull UriRequest request) {
        getLazyInitHelper(moduleName).ensureInit();
        return getChild(request) != null;
    }

    @Override
    protected void handleInternal(@NonNull String moduleName, @NonNull UriRequest request, @NonNull UriCallback callback) {
        PathHandler pathHandler = getChild(request);
        if (pathHandler != null) {
            pathHandler.handle(moduleName, request, callback);
        } else {
            // 没找到的继续分发
            callback.onNext();
        }
    }

    @Override
    public String toString() {
        return "UriAnnotationHandler";
    }
}
