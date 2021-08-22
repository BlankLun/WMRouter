package com.sankuai.waimai.router.components;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.service.ServiceLoader;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 使用ServiceLoader加载注解配置
 * <p>
 * Created by jzj on 2018/4/28.
 */
public class DefaultAnnotationLoader implements AnnotationLoader {

    public static final AnnotationLoader INSTANCE = new DefaultAnnotationLoader();

    @Override
    public <T extends UriHandler> void load(@NonNull String moduleName, T handler,
            Class<? extends AnnotationInit<T>> initClass) throws ClassNotFoundException {
        List<? extends AnnotationInit<T>> services = Router.getAllServices(moduleName, initClass);
        if (services.isEmpty() && !ServiceLoader.isHasInit(moduleName)) {
            throw new ClassNotFoundException("DefaultAnnotationLoader load initClass " +
                    initClass.getCanonicalName() + " but not find in module " + moduleName);
        }
        for (AnnotationInit<T> service : services) {
            service.init(handler);
        }
    }
}
