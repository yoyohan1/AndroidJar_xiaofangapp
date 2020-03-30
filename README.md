# AndroidJar_xiaofangapp

#### 介绍
消防app的微信回调jar 和 自用的获取Notch适配AndroidP和AndroidO的jar

1. AndroidQ（Android10.0）的设备上 状态栏的黑边背景调不出来。所以适配AndroidQ的非刘海屏手机 开启全屏模式。刘海屏手机开启状态栏黑边模式    
2. AndroidP（Android9.0）及以下的设备上 状态栏的黑边背景可以调出来。刘海屏和非刘海屏手机都开启状态栏黑边模式。  

3. 另外，需要注意删除aar下的BuildConfig.class该文件（压缩软件删除） 不然Unity打包会jar包冲突。参考：https://blog.csdn.net/cl6348/article/details/89187939