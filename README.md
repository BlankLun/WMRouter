
![](docs/images/banner.png)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://raw.githubusercontent.com/meituan/WMRouter/master/LICENSE)
[![Release Version](https://img.shields.io/badge/release-1.2.0-red.svg)](https://github.com/meituan/WMRouter/releases)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/meituan/WMRouter/pulls)

WMRouter是一款Android路由框架，基于组件化的设计思路，有功能灵活、使用简单的特点。


## 功能简介

WMRouter主要提供URI分发、ServiceLoader两大功能。

URI分发功能可用于多工程之间的页面跳转、动态下发URI链接的跳转等场景，特点如下：

1. 支持多scheme、host、path
2. 支持URI正则匹配
3. 页面配置支持Java代码动态注册，或注解配置自动注册
4. 支持配置全局和局部拦截器，可在跳转前执行同步/异步操作，例如定位、登录等
5. 支持单次跳转特殊操作：Intent设置Extra/Flags、设置跳转动画、自定义StartActivity操作等
6. 支持页面Exported控制，特定页面不允许外部跳转
7. 支持配置全局和局部降级策略
8. 支持配置单次和全局跳转监听
9. 完全组件化设计，核心组件均可扩展、按需组合，实现灵活强大的功能


基于[SPI (Service Provider Interfaces) ](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html)的设计思想，WMRouter提供了ServiceLoader模块，类似Java中的`java.util.ServiceLoader`，但功能更加完善。通过ServiceLoader可以在一个App的多个模块之间通过接口调用代码，实现模块解耦，便于实现组件化、模块间通信，以及和依赖注入类似的功能等。其特点如下：

1. 使用注解自动配置
2. 支持获取接口的所有实现，或根据Key获取特定实现
3. 支持获取Class或获取实例
4. 支持无参构造、Context构造，或自定义Factory、Provider构造
5. 支持单例管理
6. 支持方法调用


其他特性：

1. 优化的Gradle插件，对编译耗时影响较小
2. 编译期和运行时配置检查，避免配置冲突和错误
3. 完善的调试功能，帮助及时发现问题


## 适用场景

WMRouter适用但不限于以下场景：

1. Native+H5混合开发模式，需要进行页面之间的互相跳转，或进行灵活的运营跳转链接下发。可以利用WMRouter统一页面跳转逻辑，根据不同的协议（HTTP、HTTPS、用于Native页面的自定义协议）跳转对应页面，且在跳转过程中可以使用UriInterceptor对跳转链接进行修改，例如跳转H5页面时在URL中加参数。

2. 统一管理来自App外部的URI跳转。来自App外部的URI跳转，如果使用Android原生的Manifest配置，会直接启动匹配的Activity，而很多时候希望先正常启动App打开首页，完成常规初始化流程（例如登录、定位等）后再跳转目标页面。此时可以使用统一的Activity接收所有外部URI跳转，到首页时再用WMRouter启动目标页面。

3. 页面跳转有复杂判断逻辑的场景。例如多个页面都需要先登录、先定位后才允许打开，如果使用常规方案，这些页面都需要处理相同的业务逻辑；而利用WMRouter，只需要开发好UriInterceptor并配置到各个页面即可。

4. 多工程、组件化、平台化开发。多工程开发要求各个工程之间能互相通信，也可能遇到和外卖App类似的代码复用、依赖注入、编译等问题，这些问题都可以利用WMRouter的URI分发和ServiceLoader模块解决。

5. 对业务埋点需求较强的场景。页面跳转作为最常见的业务逻辑之一，常常需要埋点。给每个页面配置好URI，使用WMRouter统一进行页面跳转，并在全局的OnCompleteListener中埋点即可。

6. 对App可用性要求较高的场景。一方面，可以对页面跳转失败进行埋点监控上报，及时发现线上问题；另一方面，页面跳转时可以执行判断逻辑，发现异常（例如服务端异常、客户端崩溃等）则自动打开降级后的页面，保证关键功能的正常工作，或给用户友好的提示。

7. 页面A/B测试、动态配置等场景。在WMRouter提供的接口基础上进行少量开发配置，就可以实现：根据下发的A/B测试策略跳转不同的页面实现；根据不同的需要动态下发一组路由表，相同的URI跳转到不同的一组页面（实现方面可以自定义UriInterceptor，对匹配的URI返回301的UriResult使跳转重定向）。

8. 支持app bundle开发，可以根据动态特性模块动态的初始化路由，通过特性模块名进行路由跳转。

## 设计与使用文档

[设计与使用文档](docs/user-manual.md)


## 更新日志

[更新日志](CHANGELOG.md)


## 其他

关于WMRouter的发展背景和过程，可参考美团技术博客 [WMRouter：美团外卖Android开源路由框架](https://tech.meituan.com/meituan_waimai_android_open_source_routing_framework.html)。

## 参考文献

[Android App Bundle 简介](https://developer.android.google.cn/guide/app-bundle?hl=zh-cn)

[bundletool](https://developer.android.google.cn/studio/command-line/bundletool?hl=zh-cn)

[从命令行构建您的应用](https://developer.android.google.cn/studio/build/building-cmdline?hl=zh-cn)
