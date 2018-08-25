package hakka.jimbowhy.remote;

import android.app.*;
import android.os.*;
import android.content.*;
import java.util.*;
import android.net.*;

public class RemoteService extends Service 
{
	
	private Random random = new Random();
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context ctx, Intent it)
		{
			if( it==null) {
				sendNotify("Service receive an empty intent");
				return;
			}
			String data = it.getStringExtra("data");
			String inf = String.format("\n%s\n",data);
			sendNotify("onReceive:"+inf);
			
		if(RemoteHelper.C_INQUIRE.equals(data)){
			Intent i = new Intent(RemoteHelper.ACTION_BROADCAST);
			i.putExtra("data",RemoteHelper.C_RUNING);
			sendBroadcast(i);
			sendNotify("onReceive sendBroadcast");
		}
			
		}

	};

	@Override
	public void onCreate()
	{
		UncaughtAgent.installHandler(this);
		
		IntentFilter filter ;
		filter = new IntentFilter(RemoteHelper.ACTION_INCOME);
		registerReceiver(receiver,filter);
		filter = new IntentFilter(Intent.ACTION_VIEW);
		registerReceiver(receiver,filter);
		
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent i)
	{
		ServiceStub s = new ServiceStub();
		s.sendNotify("onBind pid:"+
			android.os.Process.myPid()+i.getStringExtra("data"));
		return s;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		sendNotify("onStartCommand "+intent.getStringExtra("data"));
		return START_STICKY;//start an Ongoing service
		//return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		unregisterReceiver(receiver);
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

		//Intent i = new Intent(RemoteHelper.ACTION_INCOME);
		Intent i = new Intent(RemoteHelper.ACTION_BROADCAST);
		i.putExtra("data",RemoteHelper.C_NOTATION_DEL);
		PendingIntent pi = PendingIntent.getBroadcast(
			RemoteService.this, id, i, PendingIntent.FLAG_ONE_SHOT);
		nb.setContentIntent(pi); // click notification to trigger
		//nb.setDeleteIntent(pi); // click clear Notification to trigger

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
