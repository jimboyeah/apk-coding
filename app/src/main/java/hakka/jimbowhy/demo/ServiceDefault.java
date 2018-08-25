package hakka.jimbowhy.demo;
import android.app.*;
import android.content.*;
import android.os.*;
import android.media.*;
import android.net.*;
import java.util.*;

/*
configure AndroidManifest.xml with
<service android:name=".ServiceDefault">
before service run. working flows:
 Activity >  Service > BroadcastReceiver
 Notificaiton > Intent(flag、Action) > PendingIntent >
*/
public class ServiceDefault extends Service
{
	static public String ACTION_DEFAULT = "android.intent.action.SERVICEDEFAULT";
	
	private BinderDemo binder = new BinderDemo();
	private Random random = new Random();
	
	protected ServiceHandler handler;
	protected Looper looper;
	// multi-thread handler
	protected void onServiceHandler(Intent i)
	{
		sendServiceNotify("Service handler "+i.getDataString(),false);
	}
	
	protected class ServiceHandler extends Handler{
		public ServiceHandler(Looper l)
		{
			super(l);
		}

		@Override
		public void handleMessage(Message msg)
		{
			onServiceHandler((Intent)msg.obj);
			stopSelf(msg.arg1);
			//super.handleMessage(msg);
		}
		
	}
	
	// simulate hardwork
	protected class Hardworking implements Runnable
	{
		@Override
		public void run()
		{
			try{
				Thread.sleep(10*1000); 
			}catch(InterruptedException e){
				sendServiceNotify("onHardworking "+e.getMessage(),false);
			}
			sendServiceNotify("hardworking done!",false);
		}
	}
	
	private BroadcastReceiver receiver = new Receiver();
	
	public class Receiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context ctx, Intent i)
		{
			sendServiceNotify(String.format("ServiceDefault receives %s %s %s",
				ctx.getClass(),i.getAction(),i.getData() ),false);
		}
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		// init multi-thread service handler
		HandlerThread t = new HandlerThread(getString(R.string.app_name)+"*");
		t.start();
		looper = t.getLooper();
		handler = new ServiceHandler(looper);
		
		IntentFilter filter = new IntentFilter(ACTION_DEFAULT);
		registerReceiver(receiver, filter);
		
		sendServiceNotify("Service Ongoing",true);
		sendServiceNotify("Service onCreate", false);
		UncaughtAgent.i("Service onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		sendServiceNotify("Service onStartCommand",false);
		
		// asynchronous handler
		Message m = new Message();
		m.arg1 = startId;
		m.obj = intent;
		handler.sendMessage(m);
		
		new Thread(new Hardworking()).start();
		
		return START_STICKY; // start an Ongoing service
		//return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		sendServiceNotify("Service onStart",false);
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent i)
	{
		sendServiceNotify("Service onBind",false);
		return binder;
	}

	@Override
	public void onRebind(Intent i)
	{
		sendServiceNotify("Service onRebind",false);
		super.onRebind(i);
	}

	@Override
	public boolean onUnbind(Intent i)
	{
		sendServiceNotify("Service onUnbind",false);
		return true;// trigger onRebind
		//return super.onUnbind(i);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent)
	{
		sendServiceNotify("Service onTaskRemoved",false);
		super.onTaskRemoved(rootIntent);
	}

	@Override
	public void onDestroy()
	{
		sendServiceNotify("Service onDestory",false);
		unregisterReceiver(receiver);
		looper.quit();
		super.onDestroy();
	}

	@Override
	public void onLowMemory()
	{
		sendServiceNotify("Service onLowMemory",false);
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level)
	{
		sendServiceNotify("Service onTrimMemory " +level, false);
		super.onTrimMemory(level);
	}
	
	public void sendServiceNotify(String msg, Boolean foreground)
	{
		int id = random.nextInt();
		
		Notification.Builder nb = new Notification.Builder(this);
		nb.setDefaults(Notification.DEFAULT_SOUND);
		nb.setAutoCancel(true); // user fling to delete
		nb.setOngoing(false);
		nb.setCategory("ServiceNotify");
		
		Intent i = new Intent(
			ACTION_DEFAULT, // register this action in activity
			Uri.parse("hakka://jimbowhy.demo"), 
			this, ServiceDefault.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent
			.getBroadcast(this, id,i,PendingIntent.FLAG_ONE_SHOT);
		nb.setContentIntent(pi); // click notification to trigger
		nb.setDeleteIntent(pi); // click clear Notification to trigger

		i = new Intent(this, ServiceDemo.class);
		pi = PendingIntent.getBroadcast(this,id,i,PendingIntent.FLAG_ONE_SHOT);
		nb.setFullScreenIntent(pi, false); // fullscreen mode to trigger
		
		long[] patterm = {0,100,300,100,300,300};//[delay,vibrate,rest,vibrate,rest...]
		nb.setVibrate(patterm);
		
		AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
		if( am.getRingerMode()==am.RINGER_MODE_NORMAL )
		{
			Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			//Uri soundUri = Uri.fromFile(new File...
			nb.setSound(tone, AudioAttributes.USAGE_ALARM);
		}
		nb.setLights(0xff2311ff, 1000,1000);
		
		nb.setContentTitle( "Service Default ");
		nb.setContentText(msg);
		nb.setSmallIcon(R.drawable.box_blue);
		
		Notification n = nb.getNotification();
		if( foreground ){
			//Make this service run in the foreground(ongoing)
			startForeground( id, n );
		}else{
			Object s = getSystemService(NOTIFICATION_SERVICE);
			NotificationManager nm = (NotificationManager)s;
			nm.notify(id, n);
		}
	}
	
	public void demo_service(String message)
	{
		sendServiceNotify("demo service pid:"+android.os.Process.myPid()+message,false);
	}
	
	
	public class BinderDemo extends Binder
	{

		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
		throws RemoteException
		{
			sendServiceNotify("Binder onTransact",false);
			return super.onTransact(code, data, reply, flags);
		}
		
		public ServiceDefault getServiceInstance()
		{
			return ServiceDefault.this;
		}
 
	}
}
