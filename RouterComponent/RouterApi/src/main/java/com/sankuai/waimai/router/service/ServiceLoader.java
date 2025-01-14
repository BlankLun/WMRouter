package com.sankuai.waimai.router.service;

import android.content.Context;

import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.components.RouterComponents;
import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.interfaces.Const;
import com.sankuai.waimai.router.utils.CommonUtils;
import com.sankuai.waimai.router.utils.LazyInitHelper;
import com.sankuai.waimai.router.utils.SingletonPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 通过接口Class获取实现类
 * <p>
 * Created by jzj on 2018/3/29.
 *
 * @param <I> 接口类型
 */
public class ServiceLoader<I> {

    private static final Map<String, Map<Class, ServiceLoader>> SERVICES = new HashMap<>();

    private static final Map<String, LazyInitHelper> sInitHelpers = new HashMap<>();

    private static class ServiceLoaderLazyInitHelper extends LazyInitHelper {
        private final String mModuleName;

        public ServiceLoaderLazyInitHelper(@NonNull String moduleName) {
            super("ServiceLoader:" + moduleName);
            this.mModuleName = moduleName;
        }

        @Override
        protected void doInit() throws Throwable {
            String className = Const.SERVICE_LOADER_INIT_PREFIX + mModuleName;
            // 反射调用Init类，避免引用的类过多，导致main dex capacity exceeded问题
            Class.forName(className)
                    .getMethod(Const.INIT_METHOD)
                    .invoke(null);
            Debugger.i("[ServiceLoader] init class(" + className + ") invoked");
        }
    }

    @NonNull
    private static LazyInitHelper getLazyInitHelper(@NonNull String moduleName) {
        LazyInitHelper lazyInitHelper = sInitHelpers.get(moduleName);
        if (lazyInitHelper == null) {
            synchronized (sInitHelpers) {
                lazyInitHelper = sInitHelpers.get(moduleName);
                if (lazyInitHelper == null) {
                    lazyInitHelper = new ServiceLoaderLazyInitHelper(CommonUtils.capitalize(moduleName));
                    sInitHelpers.put(moduleName, lazyInitHelper);
                }
            }
        }
        return lazyInitHelper;
    }

    /**
     * @see LazyInitHelper#lazyInit()
     *
     * @param moduleName 模块名
     */
    public static void lazyInit(@NonNull String moduleName) {
        getLazyInitHelper(moduleName).lazyInit();
    }

    /**
     * @see LazyInitHelper#isHasInit()
     *
     * @param moduleName 模块名
     * @return true 已经成功初始化
     */
    public static boolean isHasInit(@NonNull String moduleName) {
        return getLazyInitHelper(moduleName).isHasInit();
    }

    /**
     * 提供给InitClass使用的初始化接口
     *
     * @param interfaceClass 接口类
     * @param implementClass 实现类
     */
    public static void put(@NonNull String moduleName, Class interfaceClass, String key, Class implementClass, boolean singleton) {
        Map<Class, ServiceLoader> serviceLoaderMap = getServiceLoaderMap(moduleName);

        ServiceLoader loader = serviceLoaderMap.get(interfaceClass);
        if (loader == null) {
            loader = new ServiceLoader(interfaceClass);
            serviceLoaderMap.put(interfaceClass, loader);
        }
        loader.putImpl(key, implementClass, singleton);
    }

    /**
     * 根据接口获取 {@link ServiceLoader}
     */
    @SuppressWarnings("unchecked")
    public static <T> ServiceLoader<T> load(@NonNull String moduleName, Class<T> interfaceClass) {
        getLazyInitHelper(moduleName).ensureInit();
        if (interfaceClass == null) {
            Debugger.fatal(new NullPointerException("ServiceLoader.load的class参数不应为空"));
            return EmptyServiceLoader.INSTANCE;
        }
        Map<Class, ServiceLoader> serviceLoaderMap = getServiceLoaderMap(moduleName);
        ServiceLoader service = serviceLoaderMap.get(interfaceClass);
        if (service == null) {
            synchronized (serviceLoaderMap) {
                service = serviceLoaderMap.get(interfaceClass);
                if (service == null) {
                    service = new ServiceLoader(interfaceClass);
                    serviceLoaderMap.put(interfaceClass, service);
                }
            }
        }
        return service;
    }

    private static Map<Class, ServiceLoader> getServiceLoaderMap(@NonNull String moduleName) {
        Map<Class, ServiceLoader> serviceLoaderMap = SERVICES.get(moduleName);
        if (serviceLoaderMap == null) {
            synchronized (SERVICES) {
                serviceLoaderMap = SERVICES.get(moduleName);
                if (serviceLoaderMap == null) {
                    serviceLoaderMap = new HashMap<>();
                    SERVICES.put(moduleName, serviceLoaderMap);
                }
            }
        }
        return serviceLoaderMap;
    }

    /**
     * key --> class name
     */
    private HashMap<String, ServiceImpl> mMap = new HashMap<>();

    private final String mInterfaceName;

    private ServiceLoader(Class interfaceClass) {
        if (interfaceClass == null) {
            mInterfaceName = "";
        } else {
            mInterfaceName = interfaceClass.getName();
        }
    }

    private void putImpl(String key, Class implementClass, boolean singleton) {
        if (key != null && implementClass != null) {
            mMap.put(key, new ServiceImpl(key, implementClass, singleton));
        }
    }

    /**
     * 创建指定key的实现类实例，使用 {@link RouterProvider} 方法或无参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回null
     */
    public <T extends I> T get(String key) {
        return createInstance(mMap.get(key), null);
    }

    /**
     * 创建指定key的实现类实例，使用Context参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回null
     */
    public <T extends I> T get(String key, Context context) {
        return createInstance(mMap.get(key), new ContextFactory(context));
    }

    /**
     * 创建指定key的实现类实例，使用指定的Factory构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回null
     */
    public <T extends I> T get(String key, IFactory factory) {
        return createInstance(mMap.get(key), factory);
    }

    /**
     * 创建所有实现类的实例，使用 {@link RouterProvider} 方法或无参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    @NonNull
    public <T extends I> List<T> getAll() {
        return getAll((IFactory) null);
    }

    /**
     * 创建所有实现类的实例，使用Context参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    @NonNull
    public <T extends I> List<T> getAll(Context context) {
        return getAll(new ContextFactory(context));
    }

    /**
     * 创建所有实现类的实例，使用指定Factory构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    @NonNull
    public <T extends I> List<T> getAll(IFactory factory) {
        Collection<ServiceImpl> services = mMap.values();
        if (services.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(services.size());
        for (ServiceImpl impl : services) {
            T instance = createInstance(impl, factory);
            if (instance != null) {
                list.add(instance);
            }
        }
        return list;
    }

    /**
     * 获取指定key的实现类。注意，对于声明了singleton的实现类，获取Class后还是可以创建新的实例。
     *
     * @return 可能返回null
     */
    @SuppressWarnings("unchecked")
    public <T extends I> Class<T> getClass(String key) {
        return (Class<T>) mMap.get(key).getImplementationClazz();
    }

    /**
     * 获取所有实现类的Class。注意，对于声明了singleton的实现类，获取Class后还是可以创建新的实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public <T extends I> List<Class<T>> getAllClasses() {
        List<Class<T>> list = new ArrayList<>(mMap.size());
        for (ServiceImpl impl : mMap.values()) {
            Class<T> clazz = (Class<T>) impl.getImplementationClazz();
            if (clazz != null) {
                list.add(clazz);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <T extends I> T createInstance(@Nullable ServiceImpl impl, @Nullable IFactory factory) {
        if (impl == null) {
            return null;
        }
        Class<T> clazz = (Class<T>) impl.getImplementationClazz();
        if (impl.isSingleton()) {
            try {
                return SingletonPool.get(clazz, factory);
            } catch (Exception e) {
                Debugger.fatal(e);
            }
        } else {
            try {
                if (factory == null) {
                    factory = RouterComponents.getDefaultFactory();
                }
                T t = factory.create(clazz);
                Debugger.i("[ServiceLoader] create instance: %s, result = %s", clazz, t);
                return t;
            } catch (Exception e) {
                Debugger.fatal(e);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ServiceLoader (" + mInterfaceName + ")";
    }

    public static class EmptyServiceLoader extends ServiceLoader {

        public static final ServiceLoader INSTANCE = new EmptyServiceLoader();

        public EmptyServiceLoader() {
            super(null);
        }

        @NonNull
        @Override
        public List<Class> getAllClasses() {
            return Collections.emptyList();
        }

        @NonNull
        @Override
        public List getAll() {
            return Collections.emptyList();
        }

        @NonNull
        @Override
        public List getAll(IFactory factory) {
            return Collections.emptyList();
        }

        @Override
        public String toString() {
            return "EmptyServiceLoader";
        }
    }
}
