package com.sankuai.waimai.router.common;

import android.content.Intent;

import com.sankuai.waimai.router.annotation.RouterPage;
import com.sankuai.waimai.router.components.RouterComponents;
import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.core.UriResult;
import com.sankuai.waimai.router.utils.LazyInitHelper;
import com.sankuai.waimai.router.utils.RouterUtils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * 内部页面跳转，由注解 {@link RouterPage} 配置。
 * {@link PageAnnotationHandler} 处理且只处理所有格式为 wm_router://page/* 的URI，根据path匹配，
 * 匹配不到的分发给 {@link NotFoundHandler} ，返回 {@link UriResult#CODE_NOT_FOUND}
 * <p>
 * Created by jzj on 2018/3/23.
 */

public class PageAnnotationHandler extends PathHandler {

    public static final String SCHEME = "wm_router";
    public static final String HOST = "page";
    public static final String SCHEME_HOST = RouterUtils.schemeHost(SCHEME, HOST);

    public static boolean isPageJump(Intent intent) {
        return intent != null && SCHEME_HOST.equals(RouterUtils.schemeHost(intent.getData()));
    }

    private final Map<String, LazyInitHelper> mInitHelpers = new HashMap<>();

    private class PageAnnotationLazyInitHelper extends LazyInitHelper {
        private final String mModuleName;

        public PageAnnotationLazyInitHelper(@NonNull String moduleName) {
            super("PageAnnotationHandler:" + moduleName);
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
                    lazyInitHelper = new PageAnnotationLazyInitHelper(moduleName);
                    mInitHelpers.put(moduleName, lazyInitHelper);
                }
            }
        }
        return lazyInitHelper;
    }

    public PageAnnotationHandler() {
        addInterceptor(NotExportedInterceptor.INSTANCE); // exported全为false
        setDefaultChildHandler(NotFoundHandler.INSTANCE); // 找不到直接终止分发
    }

    /**
     * @see LazyInitHelper#lazyInit()
     */
    public void lazyInit(@NonNull String moduleName) {
        getLazyInitHelper(moduleName).lazyInit();
    }

    protected void initAnnotationConfig(@NonNull String moduleName) throws ClassNotFoundException {
        RouterComponents.loadAnnotation(moduleName, this, IPageAnnotationInit.class);
    }

    @Override
    public void handle(@NonNull String moduleName, @NonNull UriRequest request, @NonNull UriCallback callback) {
        getLazyInitHelper(moduleName).ensureInit();
        super.handle(moduleName, request, callback);
    }

    @Override
    protected boolean shouldHandle(@NonNull String moduleName, @NonNull UriRequest request) {
        return SCHEME_HOST.matches(request.schemeHost());
    }

    @Override
    public String toString() {
        return "PageAnnotationHandler";
    }
}