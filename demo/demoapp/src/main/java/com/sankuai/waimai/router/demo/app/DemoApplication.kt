package com.sankuai.waimai.router.demo.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.AsyncTask
import com.google.android.play.core.splitcompat.SplitCompat
import com.sankuai.waimai.router.Router
import com.sankuai.waimai.router.annotation.RouterProvider
import com.sankuai.waimai.router.annotation.RouterService
import com.sankuai.waimai.router.common.DefaultRootUriHandler
import com.sankuai.waimai.router.components.DefaultLogger
import com.sankuai.waimai.router.components.DefaultOnCompleteListener
import com.sankuai.waimai.router.core.Debugger
import java.util.*

/**
 * Created by jzj on 2018/3/19.
 */
@RouterService(interfaces = [Context::class], key = ["/application"], singleton = true)
class DemoApplication : Application() {
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

    override fun onCreate() {
        sApplication = this
        super.onCreate()
        initRouter(this)
    }

    override fun attachBaseContext(base: Context) {
        LanguageHelper.init(base)
        val ctx = LanguageHelper.getLanguageConfigurationContext(base)
        super.attachBaseContext(ctx)
        SplitCompat.install(this)
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
}

internal const val LANG_EN = "en"
internal const val LANG_PL = "pl"

private const val PREFS_LANG = "language"

/**
 * A singleton helper for storing and retrieving the user selected language in a
 * SharedPreferences instance. It is required for persisting the user language choice between
 * application restarts.
 */
object LanguageHelper {
    lateinit var prefs: SharedPreferences
    var language: String
        get() {
            return prefs.getString(PREFS_LANG, LANG_EN)!!
        }
        set(value) {
            prefs.edit().putString(PREFS_LANG, value).apply()
        }

    fun init(ctx: Context) {
        prefs = ctx.getSharedPreferences(PREFS_LANG, Context.MODE_PRIVATE)
    }

    /**
     * Get a Context that overrides the language selection in the Configuration instance used by
     * getResources() and getAssets() by one that is stored in the LanguageHelper preferences.
     *
     * @param ctx a base context to base the new context on
     */
    fun getLanguageConfigurationContext(ctx: Context): Context {
        val conf = getLanguageConfiguration()
        return ctx.createConfigurationContext(conf)
    }

    /**
     * Get an empty Configuration instance that only sets the language that is
     * stored in the LanguageHelper preferences.
     * For use with Context#createConfigurationContext or Activity#applyOverrideConfiguration().
     */
    fun getLanguageConfiguration(): Configuration {
        val conf = Configuration()
        conf.setLocale(Locale.forLanguageTag(language))
        return conf
    }
}