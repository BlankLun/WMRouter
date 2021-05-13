package com.sankuai.waimai.router.plugin;

import com.android.build.gradle.BaseExtension
import com.sankuai.waimai.router.interfaces.Const

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 插件所做工作：将注解生成器生成的初始化类汇总到ServiceLoaderInit，运行时直接调用ServiceLoaderInit
 */
class WMRouterPlugin implements Plugin<Project> {
    Project mProject

    @Override
    void apply(Project project) {
        if (!hasAndroidPlugin(project)) {
            // 不是Android 模块不处理
            return
        }

        mProject = project
        // 自动添加路由框架模块依赖
        addDependencies()

        if (hasApplicationPlugin(project)) {
            // Application模块处理ASM注入代码
            registerTransform()
        }
    }

    private void registerTransform() {
        WMRouterExtension extension = mProject.getExtensions() \
                .create(Const.NAME, WMRouterExtension)

        WMRouterLogger.info"register transform"
        mProject.getExtensions().findByType(BaseExtension) \
                .registerTransform(new WMRouterTransform())

        mProject.afterEvaluate {
            p -> WMRouterLogger.setConfig(extension)
        }
    }

    /**
     * 是否调试 apt
     */
    private boolean isDebugApt() {
        return mProject.hasProperty(getAptDebugKey()) && Boolean.valueOf(mProject.property(getAptDebugKey()))
    }

    static boolean hasAndroidPlugin(Project project) {
        return hasApplicationPlugin(project) || hasLibraryPlugin(project)
    }

    static boolean hasApplicationPlugin(Project project) {
        return project.plugins.hasPlugin('com.android.application')
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
        return String.valueOf(mProject.property("GROUP"))
    }

    private String getPomVersionName() {
        return String.valueOf(mProject.property("VERSION_NAME"))
    }
}
