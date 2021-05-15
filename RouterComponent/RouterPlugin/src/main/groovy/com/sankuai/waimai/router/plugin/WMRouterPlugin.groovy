package com.sankuai.waimai.router.plugin;

import com.android.build.gradle.BaseExtension
import com.sankuai.waimai.router.interfaces.Const
import com.sankuai.waimai.router.utils.CommonUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 插件所做工作：将注解生成器生成的初始化类汇总到ServiceLoaderInit，运行时直接调用ServiceLoaderInit
 */
class WMRouterPlugin implements Plugin<Project> {
    private static final String FEATURE_MODULE_ID = "FEATURE_MODULE_ID"

    private Project mProject
    private String mModuleName

    @Override
    void apply(Project project) {
        if (!hasAndroidPlugin(project)) {
            // 不是Android 模块不处理
            return
        }

        mProject = project

        if (hasDynamicFeaturePlugin(project)) {
            // 动态特性模块设置编译属性
            configCompileOptions()
        }

        // 自动添加路由框架模块依赖
        addDependencies()

        if (hasApplicationPlugin(project) || hasDynamicFeaturePlugin(project)) {
            // Application、DynamicFeature 模块处理ASM注入代码
            registerTransform(hasApplicationPlugin(project), mModuleName)
        }
    }

    private void registerTransform(boolean isApplication, String moduleName) {
        WMRouterExtension extension = mProject.getExtensions() \
                .create(Const.NAME, WMRouterExtension)

        WMRouterLogger.info"register transform"
        mProject.getExtensions().findByType(BaseExtension) \
                .registerTransform(new WMRouterTransform(isApplication, moduleName))

        mProject.afterEvaluate {
            p -> WMRouterLogger.setConfig(extension)
        }
    }

    private void configCompileOptions() {
        mModuleName = getModuleName()
        WMRouterLogger.info "configCompileOptions moduleName: ${mModuleName}"
        addConfigCompileOption(Const.FEATURE_MODULE_NAME_KEY, mModuleName)
    }

    private String getModuleName() {
        String artifactId = getFeatureModuleId()
        if (!CommonUtils.isEmpty(artifactId)) {
            return artifactId
        }

        // 获取 apt 需要的模块名称
        return mProject.name
    }

    private String getFeatureModuleId() {
        String featureModuleId = getProjectProperty(FEATURE_MODULE_ID)
        if (CommonUtils.isEmpty(featureModuleId)) {
            return null
        }
        return featureModuleId
    }

    private String getProjectProperty(String propertyName) {
        if (mProject.hasProperty(propertyName)) {
            return mProject.property(propertyName)
        }
        return null
    }

    /**
     * 添加编译选项，用于compiler编译时获取option
     *
     * @param key 配置的key
     * @param value 配置的值
     */
    private void addConfigCompileOption(String key, String value) {
        mProject.android.defaultConfig {
            javaCompileOptions {
                annotationProcessorOptions {
                    if (arguments == null) {
                        arguments = [(key): value]
                    } else {
                        arguments.put((key), value)
                    }
                }
            }
        }
    }

    /**
     * 是否调试 apt
     */
    private boolean isDebugApt() {
        return mProject.hasProperty(getAptDebugKey()) && Boolean.valueOf(mProject.property(getAptDebugKey()))
    }

    static boolean hasAndroidPlugin(Project project) {
        return hasApplicationPlugin(project) || hasLibraryPlugin(project) || hasDynamicFeaturePlugin(project)
    }

    static boolean hasApplicationPlugin(Project project) {
        return project.plugins.hasPlugin('com.android.application')
    }

    static boolean hasDynamicFeaturePlugin(Project project) {
        return project.plugins.hasPlugin('com.android.dynamic-feature')
    }

    static boolean hasLibraryPlugin(Project project) {
        return project.plugins.hasPlugin('com.android.library')
    }

    static boolean hasKotlinAndroidPlugin(Project project) {
        return project.plugins.hasPlugin('kotlin-android')
    }

    /**
     * 添加依赖
     */
    private void addDependencies() {
        if (isDebugApt()) {
            WMRouterLogger.info "debug router Apt"
            mProject.dependencies.add('implementation', mProject.project(':RouterApi'))
            mProject.dependencies.add('annotationProcessor', mProject.project(':RouterCompiler'))
            if (hasKotlinAndroidPlugin(mProject)) {
                mProject.dependencies.add('kapt', mProject.project(':RouterCompiler'))
            }
        } else {
            WMRouterLogger.info "not debug router  Apt"
            mProject.dependencies.add('implementation', "${getPomGroup()}:${getApiPomArtifactId()}:${getPomVersionName()}")
            mProject.dependencies.add('annotationProcessor', "${getPomGroup()}:${getCompilerPomArtifactId()}:${getPomVersionName()}")
            if (hasKotlinAndroidPlugin(mProject)) {
                mProject.dependencies.add('kapt', "${getPomGroup()}:${getCompilerPomArtifactId()}:${getPomVersionName()}")
            }
        }
    }

    private static String getAptDebugKey() {
        // gradle.properties 中添加该属性来配置是否处于调试 apt 模式
        return "DEBUG_ROUTER_APT"
    }

    private static String getApiPomArtifactId() {
        return "api"
    }

    private static String getCompilerPomArtifactId() {
        return "compiler"
    }

    private String getPomGroup() {
        return String.valueOf(mProject.property("POM_GROUP_ID"))
    }

    private String getPomVersionName() {
        return String.valueOf(mProject.property("POM_VERSION_NAME"))
    }
}
