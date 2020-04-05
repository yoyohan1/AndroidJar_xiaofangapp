# AndroidJar_xiaofangapp

#### 介绍
消防app的微信回调jar 和 自用的获取Notch适配AndroidP和AndroidO的jar
开启状态栏黑边填充 可以不用适配刘海屏。

1. AndroidQ（Android10.0）的设备上 状态栏的黑边背景可以调出来(需要设置为自定义的暗色的style/Theme)。刘海屏和非刘海屏手机都开启状态栏黑边模式。

2. AndroidP（Android9.0）及以下的设备上 状态栏的黑边背景可以调出来。刘海屏和非刘海屏手机都开启状态栏黑边模式。

3. 另外，需要注意删除aar下的BuildConfig.class该文件（压缩软件删除） 不然Unity打包会jar包冲突。参考：https://blog.csdn.net/cl6348/article/details/89187939

   解决办法为：makeJar排除BuildConfig.class

4. 因为导航栏不能完全隐藏（隐藏后点击一下就出来了）所以状态栏开启导航栏也开启