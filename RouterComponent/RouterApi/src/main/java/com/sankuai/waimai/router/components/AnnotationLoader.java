package com.sankuai.waimai.router.components;

import com.sankuai.waimai.router.core.UriHandler;

import androidx.annotation.NonNull;

/**
 * 用于加载注解配置
 * <p>
 * Created by jzj on 2018/4/28.
 */
public interface AnnotationLoader {

    <T extends UriHandler> void load(@NonNull String moduleName, T handler, Class<? extends AnnotationInit<T>> initClass);
}
