# AIDL IPC Service

[本文示例代码打包下载含apk](https://pan.baidu.com/s/1jsz3IMFPqqn_zFidlzJd2Q)
[github resource code project](https://github.com/jimboyeah/apk-coding/tree/sensor)
[IPC Service apk download](https://github.com/jimboyeah/apk-coding/tree/sensor/IPCService/app/build/bin)
[IPC Cloent apk download](https://github.com/jimboyeah/apk-coding/tree/sensor/IPCClient/app/build/bin)

#一、基本概念

Service 服务是 Android 系统最常用的四大部件之一，Android 支持 Service 服务的原因主要目的有两个，一是简化后台任务的实现，二是实现在同一台设备当中跨进程的远程信息通信。

Service 服务主要分为 Local Service 本地服务与 Remote Service 远程服务两种，本地服务只支持同一进程内的应用程序进行访问，远程服务可通过AIDL（Android Interface Definition Language）技术支持跨进程访问。服务可以通过Context.startService()和Context.bindService()进行启动，一般Local Service本地服务可使用其中一种方法启动，但Remote Service远程服务只能使用Context.bindService()启动，而两种调用方式在使用场景与活动流程中都存在差异。还有通过多线程技术处理 Service 服务的延时操作等技术，下文将针对Android 系统的 Service 服务的一系列操作进行深入探讨。

本文要结合android通告notification和服务来开发一个区有实用功能的应用。通告是android系统的重要ui功能，是用户经常参与又非常便利的互动环节，应用可以通过向通知栏发送通告信息，来向用户传达程序信息，通过定制通告视图还可以方便用户对应用的操控，如在通知栏展示播放器的功能键，后面结合代码解说。

#2、Service 服务的类型

2.1 按照 Service 的生命周期模型一共分为两种类型

第一类是直接通过Context.startService()启动，通过Context.stopService() 结束Service，其特点在于调用简单，方便控制。缺点在于一旦启动了 Service 服务，除了再次调用或结束服务外就再无法对服务内部状态进行操控，缺乏灵活性。

第二类是通过Context.bindService()启动，通过Context.unbindService() 结束，相对其特点在运用灵活，可以通过 IBinder 接口中获取 Service 的句柄，对 Service 状态进行检测。

从 Android 系统设计的架构上看，startService() 是用于启动本地服务，bindService() 更多是用于对远程服务进行绑定。当然，也可以结合两者进行混合式应用，先通过startService()启动服务，然后通过 bindService() 、unbindService()方法进行多次绑定，以获取 Service 服务在不同状态下的信息，最后通过stopService()方法结束Service运行。

2.2 按照 Service 的寄存方式分为两种类型

本地服务 （Local Service） 寄存于当前的进程当中，当前进程结束后 Service 也会随之结束，Service 可以随时与 Activity 等多个部件进行信息交换。Service服务不会自动启动线程，如果没有人工调用多线程方式进行启动，Service将寄存于主线程当中。

远程服务 （Remote Service ） 独立寄存于另一进程中， 通过 AIDL （Android Interface Definition Language）接口定义语言，实现Android设备上的两个进程间通信(IPC)。AIDL 的 IPC 机制是基于 RPC (Remote Proceduce Call) 远程过程调用协议建立的，用于约束两个进程间的通讯规则，供编译器生成代码。进程之间的通信信息，首先会被转换成AIDL协议消息，然后发送给对方，对方收到AIDL协议消息后再转换成相应的对象。

在代码的实现上，remote 服务和本地服务有相似的地方，而差异主要表现在AIDL的应用上，其次在启动方式上。

#3、remore服务端代码

本实例通过 Android AIDE编写代码，使用AIDE可以在android平台开发app，免去了Android  studio的一系列配置，可惜的是AIDE目前集成android api level 21。

新建服务端工程，androidmanifest.xml中将入口类禁用，android:enabled="false"，因为服务端不需要交互界面，它运行后台。注意服务类的注册信息service，android:process=":remote"表示服务将独立于客户端运行。注意过滤器的值hakka.jimbowhy.remote.ITimerService，客户端要想连接上服务端，绑定时的Intent必须指定一样的Action值，这样安卓系统才会匹配到服务并启动他。


```xml

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="hakka.jimbowhy.remote" >

    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme" >
		
		<activity
			android:enabled="false"
            android:name=".MainTest"
            android:label="@string/app_main" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
		
		<service
			android:label="@string/app_name" 
			android:name=".RemoteService"
			android:process=":remote">
            <intent-filter>
                <action android:name="hakka.jimbowhy.remote.ITimerService"/>
            </intent-filter>
        </service>
		
    </application>

</manifest>
```
使用包名 hakka.jimbowhy.remote，在代码目录新建aidl接口，IRemoteService.aidl ，接口定义两个方法，sendNotify用来发送系统通告，getDemoService作为演示的服务功能，内容如下。


```java
package hakka.jimbowhy.remote;

interface IRemoteService
{
String getDemoService(int id);
void sendNotify(String msg);
}

```

有了这个接口定义以后，预编译一次，让aidl编译器生成中间文件，找到gen目录，可以发现IRemoteService.java中间文件，它定义了我们的接口IRemoteService并继承了android.os.IInterface。结果有个内部类Stub，他才是实现我们需要的remote服务类的存根。现在我们需要新建一个RemoteService.java来实现我们的服务类功能。注意，RemoteService好像本地服务那样实现，但是多了一个内部类ServiceStub extends IRemoteService.Stub，它实现了aidl中间文件接口定义，这样服务端就和安卓系统的底层建立联系了。当客户端绑定服务时，我们的服务类就负责与客户端交互，而内部类ServiceStub 就负责以安卓系统的底层交互。


```java

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /storage/emulated/0/Android/Lessons/Android_Demos/remote/app/src/main/java/hakka/jimbowhy/remote/IRemoteService.aidl
 */
package hakka.jimbowhy.remote;
public interface IRemoteService extends android.os.IInterface
{
	/** Local-side IPC implementation stub class. */
	public static abstract class Stub extends android.os.Binder implements hakka.jimbowhy.remote.IRemoteService
	{
		private static final java.lang.String DESCRIPTOR = "hakka.jimbowhy.remote.IRemoteService";
		/** Construct the stub at attach it to the interface. */
		public Stub()
		{
			this.attachInterface(this, DESCRIPTOR);
		}
		/**
		 * Cast an IBinder object into an hakka.jimbowhy.remote.IRemoteService interface,
		 * generating a proxy if needed.
		 */
		public static hakka.jimbowhy.remote.IRemoteService asInterface(android.os.IBinder obj)
		{
			if ((obj == null))
			{
				return null;
			}
			android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
			if (((iin != null) && (iin instanceof hakka.jimbowhy.remote.IRemoteService)))
			{
				return ((hakka.jimbowhy.remote.IRemoteService)iin);
			}
			return new hakka.jimbowhy.remote.IRemoteService.Stub.Proxy(obj);
		}
		@Override public android.os.IBinder asBinder()
		{
			return this;
		}
		@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
		{
			switch (code)
			{
				case INTERFACE_TRANSACTION:
					{
						reply.writeString(DESCRIPTOR);
						return true;
					}
				case TRANSACTION_getDemoService:
					{
						data.enforceInterface(DESCRIPTOR);
						int _arg0;
						_arg0 = data.readInt();
						java.lang.String _result = this.getDemoService(_arg0);
						reply.writeNoException();
						reply.writeString(_result);
						return true;
					}
				case TRANSACTION_sendNotify:
					{
						data.enforceInterface(DESCRIPTOR);
						java.lang.String _arg0;
						_arg0 = data.readString();
						this.sendNotify(_arg0);
						reply.writeNoException();
						return true;
					}
			}
			return super.onTransact(code, data, reply, flags);
		}
		private static class Proxy implements hakka.jimbowhy.remote.IRemoteService
		{
			private android.os.IBinder mRemote;
			Proxy(android.os.IBinder remote)
			{
				mRemote = remote;
			}
			@Override public android.os.IBinder asBinder()
			{
				return mRemote;
			}
			public java.lang.String getInterfaceDescriptor()
			{
				return DESCRIPTOR;
			}
			@Override public java.lang.String getDemoService(int id) throws android.os.RemoteException
			{
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				java.lang.String _result;
				try
				{
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeInt(id);
					mRemote.transact(Stub.TRANSACTION_getDemoService, _data, _reply, 0);
					_reply.readException();
					_result = _reply.readString();
				}
				finally
				{
					_reply.recycle();
					_data.recycle();
				}
				return _result;
			}
			@Override public void sendNotify(java.lang.String msg) throws android.os.RemoteException
			{
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try
				{
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(msg);
					mRemote.transact(Stub.TRANSACTION_sendNotify, _data, _reply, 0);
					_reply.readException();
				}
				finally
				{
					_reply.recycle();
					_data.recycle();
				}
			}
		}
		static final int TRANSACTION_getDemoService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
		static final int TRANSACTION_sendNotify = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
	}
	public java.lang.String getDemoService(int id) throws android.os.RemoteException;
	public void sendNotify(java.lang.String msg) throws android.os.RemoteException;
}
```

```java
package hakka.jimbowhy.remote;

import android.app.*;
import android.os.*;
import android.content.*;
import java.util.*;
import android.net.*;

public class RemoteService extends Service 
{
	private Random random = new Random();

	@Override
	public IBinder onBind(Intent i)
	{
		ServiceStub s = new ServiceStub();
		s.sendNotify("Remote Service onBind pid:"+
			android.os.Process.myPid()+i.getDataString());
		return s;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		sendNotify("onStartCommand "+intent.getDataString());
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		sendNotify("onDestory");
		super.onDestroy();
	}
	
	public void sendNotify(String msg)
	{
		int id = random.nextInt();

		Notification.Builder nb = new Notification.Builder(RemoteService.this);
		nb.setDefaults(Notification.DEFAULT_ALL);
		nb.setAutoCancel(true); // user fling to delete
		nb.setOngoing(false);
		nb.setCategory("ServiceNotify");

		Intent i = new Intent(
			Intent.ACTION_ANSWER, // register this action in activity
			Uri.parse("hakka://remote.jimbowhy.demo"));
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent
			.getBroadcast(RemoteService.this, id, i, PendingIntent.FLAG_ONE_SHOT);
		nb.setContentIntent(pi); // click notification to trigger
		nb.setDeleteIntent(pi); // click clear Notification to trigger

		long[] patterm = {0,100,150,100,300,300};//[delay,vibrate,rest,vibrate,rest...]
		nb.setVibrate(patterm);

		nb.setContentTitle(getString(R.string.app_name));
		nb.setContentText(msg);
		nb.setSmallIcon(R.drawable.ic_launcher);

		Notification n = nb.getNotification();

		Object s = getSystemService(NOTIFICATION_SERVICE);
		NotificationManager nm = (NotificationManager)s;
		nm.notify(id, n);
	}
	
	public class ServiceStub extends IRemoteService.Stub
	{

		@Override
		public String getDemoService(int id) throws RemoteException
		{
			String ishukan = "日・月・火・水・木・金・土の七日を一区切りとした日時の単位。";
			String[] day = {
				"日曜日 にちようび",
				"月曜日 げつようび",
				"火曜日 かようひ",
				"水曜日 すいようひ",
				"木曜日 もくようび",
				"金曜日 きんようび",
				"土曜日 どようび"};
			if (id < 0 || id >= day.length) return ishukan;
			return day[id];
		}


		public void sendNotify(String msg)
		{
			RemoteService.this.sendNotify(msg);
		}
	}
}

```

sendNotify方法主要用来发送调试信息到通知栏，注意其中一行，ongoing=true表示一个持续的任务，系统就不会因为回收内存而将服务杀死，服务通过调用startForeground就会产生ongoing服务，即前台服务。前台服务不会被回收是个很大的特点，相应他的通告也是不可以在通知栏里移除的，除非服务停止了。这里不需要这种服务，设置false，这样用户就可以在通知栏将通告抹掉。

```
nb.setOngoing(false);
```



#4、客户端代码

新建客户端工程，使用包名 hakka.jimbowhy.client，设置启动入口类为.Client。以下为androidmanifest.xml文件内容，不用设置remote服务类：



```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="hakka.jimbowhy.client" >

    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme" >
		<activity
            android:name=".Client"
            android:label="@string/app_name" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```
在布局文件main.xml中，添加三个按钮，分别用来绑定、测试、取绑，设置好id如下：


```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
	xmlns:android="http://schemas.android.com/apk/res/android"
	android:layout_width="match_parent"
	android:layout_height="match_parent"
	android:gravity="center"
	android:orientation="vertical">

	<TextView
		android:text="@string/hello_world"
		android:layout_width="wrap_content"
		android:layout_height="wrap_content"
		android:id="@+id/mainTextView"/>

	<LinearLayout
		android:layout_height="wrap_content"
		android:layout_width="match_parent"
		android:orientation="horizontal"
		android:gravity="center"
		android:layout_gravity="bottom">

		<Button
			android:layout_height="wrap_content"
			android:layout_width="wrap_content"
			android:text="Bind"
			android:id="@+id/mainButtonBind"/>

		<Button
			android:layout_height="wrap_content"
			android:layout_width="wrap_content"
			android:text="Test"
			android:id="@+id/mainButtonTest"/>

		<Button
			android:layout_height="wrap_content"
			android:layout_width="wrap_content"
			android:text="Unbind"
			android:id="@+id/mainButtonUnbind"/>

	</LinearLayout>

</LinearLayout>


```
接下来是java代码部分，在代码文件夹新建一个Client.java，注意包名 hakka.jimbowhy.client，在写代码之前，先将aidl文件拷贝过来放到代码文件夹，目录结构保持不变，先预编译一次，aidl编译器会产生相应的java中间层文件，这样编写代码的时候就有提示功能了。



```java

package hakka.jimbowhy.client;

import hakka.jimbowhy.remote.*;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.content.*;
import android.widget.*;
import java.util.*;
import android.net.*;

public class Client extends Activity implements 
View.OnClickListener, ServiceConnection
{
	public String ACTION = "hakka.jimbowhy.remote.ITimerService";
	private Boolean isBound = false;
	private IRemoteService service;
	private TextView box;

	@Override
	public void onServiceConnected(ComponentName cn, IBinder binder)
	{
		service = IRemoteService.Stub.asInterface(binder);
	}

	@Override
	public void onServiceDisconnected(ComponentName cn)
	{
		try{
			service.sendNotify("onServiceDisconnected");
		}catch(RemoteException e){
			box.setText(e.getMessage());
		}
	}


	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.mainButtonBind:
				//Uri dat = Uri.parse("hakka://ipc");
				Intent intent = new Intent(ACTION);
				intent.setPackage("hakka.jimbowhy.remote");
				//intent.setData(dat);
				isBound = bindService(intent,this,
									  BIND_WAIVE_PRIORITY|BIND_AUTO_CREATE);
				break;
			case R.id.mainButtonUnbind:
				if(isBound){
					service = null;
					unbindService(this);
					isBound = false;
				}
				break;
			case R.id.mainButtonTest:
				try{
					if( service!=null) {
						box.setText(service.getDemoService(new Random().nextInt(8)));
						service.sendNotify("MainTester pid:"+android.os.Process.myPid());
					}
				}catch(RemoteException e){
					box.setText(e.getMessage());
				}
				break;
		}
	}

	@Override
	protected void onDestroy()
	{
		service = null;
		super.onDestroy();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		findViewById(R.id.mainButtonBind).setOnClickListener(this);
		findViewById(R.id.mainButtonTest).setOnClickListener(this);
		findViewById(R.id.mainButtonUnbind).setOnClickListener(this);
		box = (TextView)findViewById(R.id.mainTextView);
	}

}


```

#6、运行测试

![图片发自简书App](http://upload-images.jianshu.io/upload_images/5509701-efc7376007e314b2.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1080/q/50)
![图片发自简书App](http://upload-images.jianshu.io/upload_images/5509701-8ee11b99bd40d272.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1080/q/50)
![图片发自简书App](http://upload-images.jianshu.io/upload_images/5509701-30e6c67d77ffb644.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1080/q/50)
![图片发自简书App](http://upload-images.jianshu.io/upload_images/5509701-f7bdb474344f1bad.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1080/q/50)
![图片发自简书App](http://upload-images.jianshu.io/upload_images/5509701-54223c93b2aaaef9.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1080/q/50)
![图片发自简书App](http://upload-images.jianshu.io/upload_images/5509701-586e13cea0030536.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1080/q/50)

上图显示，客户端、服务端process id归属两个进程，因此是进程间调用，至此ipc服务交互实战完成。

[图片发自简书App](http://upload-images.jianshu.io/upload_images/5509701-7ab16a6b201e1447.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1080/q/50)
[图片发自简书App](http://upload-images.jianshu.io/upload_images/5509701-ccc360a137a8124c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1080/q/50)
[图片发自简书App](http://upload-images.jianshu.io/upload_images/5509701-9a48aacce2342932.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1080/q/50)
[图片发自简书App](http://upload-images.jianshu.io/upload_images/5509701-b2a642df53afa9a1.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1080/q/50)



参考：[Android综合揭秘——全面剖释Service服务](https://www.cnblogs.com/leslies2/p/5401813.html)
