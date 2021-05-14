package com.sankuai.waimai.router.demo.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.AsyncTask
import com.sankuai.waimai.router.Router
import com.sankuai.waimai.router.annotation.RouterProvider
import com.sankuai.waimai.router.annotation.RouterService
import com.sankuai.waimai.router.common.DefaultRootUriHandler
import com.sankuai.waimai.router.components.DefaultLogger
import com.sankuai.waimai.router.components.DefaultOnCompleteListener
import com.sankuai.waimai.router.core.Debugger

/**
 * Created by jzj on 2018/3/19.
 */
@RouterService(interfaces = [Context::class], key = ["/application"], singleton = true)
class DemoApplication : Application() {
    override fun onCreate() {
        sApplication = this
        super.onCreate()
        initRouter(this)
    }

    @SuppressLint("StaticFieldLeak")
    private fun initRouter(context: Context) {
        // 自定义Logger
        val logger: DefaultLogger = object : DefaultLogger() {
            override fun handleError(t: Throwable) {
                super.handleError(t)
                // 此处上报Fatal级别的异常
            }
        }

        // 设置Logger
        Debugger.setLogger(logger)

        // Log开关，建议测试环境下开启，方便排查问题。
        Debugger.setEnableLog(true)

        // 调试开关，建议测试环境下开启。调试模式下，严重问题直接抛异常，及时暴漏出来。
        Debugger.setEnableDebug(true)

        // 创建RootHandler
        val rootHandler = DefaultRootUriHandler(context)

        // 设置全局跳转完成监听器，可用于跳转失败时统一弹Toast提示，做埋点统计等。
        rootHandler.globalOnCompleteListener = DefaultOnCompleteListener.INSTANCE

        // 初始化
        Router.init(rootHandler)

        // 懒加载后台初始化（可选）
        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                Router.lazyInit()
                return null
            }
        }.execute()
    }

    companion object {
        @RouterProvider
        fun provideApplication(): DemoApplication? {
            return sApplication
        }

        @SuppressLint("StaticFieldLeak")
        private var sApplication: DemoApplication? = null
        @JvmStatic
        val context: Context?
            get() = sApplication
    }
}