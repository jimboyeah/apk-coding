#


#基本原理

Android中的广播-观察者模式基于消息的发布/订阅事件模型，通过中间机构将广播的发送者、接收者解耦，这样开发的软件系统具有良好的重构弹性。

模型中有3个角色：

消息订阅者（接受并处理BroadcastReceiver）
消息发布者（调用sendBroadcast广播信息）
消息中心（中间机构AMS，即Activity Manager Service）

﻿广播接收者需要继承BroadcastReceivre基类﻿必须复写抽象方法onReceive()方法，AMS分派广播时自动回调 onReceive() 。默认情况下，广播接收器运行在 UI 线程，因此，onReceive()方法不能执行耗时操作，否则将导致ANR。

﻿#注册方式

订阅者要接受指定的信息就要注册接受器，分为两种，一是静态注册，在AndroidManifest.xml里通过<receive>标签声明﻿属性说明，App完成首次启动系统会自动注册到系统中。二是动态注册，通过代码中调用Context.registerReceiver()方法注册，unregisterReceiver()方法注销。动态广播最好在Activity 的 onResume()注册、onPause()注销，成对操作。﻿Activity生命周期的方法是成对出现的：

﻿onCreate()  & onDestory()﻿
﻿onStart() & onStop()﻿
﻿onResume()  & onPause()

在onResume()注册、onPause()注销是因为onPause()在App死亡前一定会被执行，从而保证广播在App死亡前一定会被注销，从而防止内存泄露。当系统因为内存不足要回收Activity占用的资源时，Activity在执行完onPause()方法后就会被销毁，有些生命周期方法onStop()，onDestory()就来不及执行就销毁了。



<receiver 
    android:enabled=["true" | "false"]
//启用设置，如果有intent-filter，默认值为true
    android:exported=["true" | "false"]
    android:icon="drawable resource"
    android:label="string resource"
    android:name=".BroadcastReceiver"
//权限匹配的广播发送者发送的广播才能被接收；
    android:permission="string"
//指定运行进程
    android:process="string" >
 <intent-filter>
<action android:name="UNIQUE_ACTION_STRING" />
    </intent-filter>
</receiver>

﻿ #广播的类型

广播的类型主要分为5类：

﻿普通广播（Normal Broadcast）﻿
﻿系统广播（System Broadcast）﻿
﻿有序广播（Ordered Broadcast）﻿
﻿粘性广播（Sticky Broadcast）﻿
﻿App应用内广播（Local Broadcast）

1. 普通广播（Normal Broadcast）

即 开发者自身定义 intent的广播（最常用）。


系统广播（System Broadcast）
﻿Android中内置了多个系统广播：只要涉及到手机的基本操作（如开机、网络状态变化、拍照等等），都会发出相应的广播﻿每个广播都有特定的Intent - Filter（包括具体的action）
3. 有序广播（Ordered Broadcast）

﻿定义

发送出去的广播被广播接收者按照先后顺序接收

有序是针对广播接收者而言的


﻿
广播接受者接收广播的顺序规则（同时面向静态和动态注册的广播接受者）

﻿按照Priority属性值从大-小排序；﻿Priority属性相同者，动态注册的广播优先；
﻿
特点

﻿接收广播按顺序接收﻿先接收的广播接收者可以对广播进行截断，即后接收的广播接收者不再接收到此广播；﻿先接收的广播接收者可以对广播进行修改，那么后接收的广播接收者将接收到被修改后的广播
﻿具体使用

有序广播的使用过程与普通广播非常类似，差异仅在于广播的发送方式：

sendOrderedBroadcast(intent);

4. App应用内广播（Local Broadcast）

﻿背景

Android中的广播可以跨App直接通信（exported对于有intent-filter情况下默认值为true）
﻿
冲突

可能出现的问题：

﻿其他App针对性发出与当前App intent-filter相匹配的广播，由此导致当前App不断接收广播并处理；﻿其他App注册与当前App一致的intent-filter用于接收广播，获取广播具体信息；

即会出现安全性 & 效率性的问题。
﻿解决方案

使用App应用内广播（Local Broadcast）


﻿App应用内广播可理解为一种局部广播，广播的发送者和接收者都同属于一个App。﻿相比于全局广播（普通广播），App应用内广播优势体现在：安全性高 & 效率高

﻿
具体使用1 - 将全局广播设置成局部广播

﻿注册广播时将exported属性设置为false，使得非本App内部发出的此广播不被接收；﻿在广播发送和接收时，增设相应权限permission，用于权限验证；﻿发送广播时指定该广播接收器所在的包名，此广播将只会发送到此包中的App内与之相匹配的有效广播接收器中。

通过intent.setPackage(packageName)指定报名


﻿具体使用2 - 使用封装好的LocalBroadcastManager类

使用方式上与全局广播几乎相同，只是注册/取消注册广播接收器和发送广播时将参数的context变成了LocalBroadcastManager的单一实例


注：对于LocalBroadcastManager方式发送的应用内广播，只能通过LocalBroadcastManager动态注册，不能静态注册

#﻿特别注意

对于不同注册方式的广播接收器回调OnReceive（Context context，Intent intent）中的context返回值是不一样的：

﻿对于静态注册（全局+应用内广播），回调onReceive(context, intent)中的context返回值是：ReceiverRestrictedContext；﻿对于全局广播的动态注册，回调onReceive(context, intent)中的context返回值是：Activity Context；﻿对于应用内广播的动态注册（LocalBroadcastManager方式），回调onReceive(context, intent)中的context返回值是：Application Context。﻿对于应用内广播的动态注册（非LocalBroadcastManager方式），回调onReceive(context, intent)中的context返回值是：Activity Context；




# [Android四大组件：BroadcastReceiver史上最全面解析](https://www.jianshu.com/p/ca3d87a4cdf3)

