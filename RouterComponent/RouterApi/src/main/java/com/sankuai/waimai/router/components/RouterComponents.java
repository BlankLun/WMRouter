package com.sankuai.waimai.router.components;

import android.content.Intent;

import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.service.DefaultFactory;
import com.sankuai.waimai.router.service.IFactory;

import androidx.annotation.NonNull;

/**
 * 用于配置组件
 * <p>
 * Created by jzj on 2018/4/28.
 */
public class RouterComponents {

    @NonNull
    private static AnnotationLoader sAnnotationLoader = DefaultAnnotationLoader.INSTANCE;

    @NonNull
    private static ActivityLauncher sActivityLauncher = DefaultActivityLauncher.INSTANCE;

    @NonNull
    private static IFactory sDefaultFactory = DefaultFactory.INSTANCE;

    public static void setAnnotationLoader(AnnotationLoader loader) {
        sAnnotationLoader = loader == null ? DefaultAnnotationLoader.INSTANCE : loader;
    }

    public static void setActivityLauncher(ActivityLauncher launcher) {
        sActivityLauncher = launcher == null ? DefaultActivityLauncher.INSTANCE : launcher;
    }

    public static void setDefaultFactory(IFactory factory) {
        sDefaultFactory = factory == null ? DefaultFactory.INSTANCE : factory;
    }

    @NonNull
    public static IFactory getDefaultFactory() {
        return sDefaultFactory;
    }

    /**
     * @see AnnotationLoader#load(String, UriHandler, Class)
     */
    public static <T extends UriHandler> void loadAnnotation(@NonNull String moduleName, T handler,
            Class<? extends AnnotationInit<T>> initClass) throws ClassNotFoundException {
        sAnnotationLoader.load(moduleName, handler, initClass);
    }

    /**
     * @see ActivityLauncher#startActivity(UriRequest, Intent)
     */
    public static int startActivity(@NonNull UriRequest request, @NonNull Intent intent) {
        return sActivityLauncher.startActivity(request, intent);
    }
}
