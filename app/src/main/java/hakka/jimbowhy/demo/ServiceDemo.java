package hakka.jimbowhy.demo;

import android.app.*;
import android.view.*;
import android.view.View.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import android.net.*;
import android.media.*;

public class ServiceDemo
implements View.OnClickListener, ServiceConnection
{
	private ServiceDefault.BinderDemo binder;
	private Intent intent;
	private Boolean isBound = false;
	
	private Random random = new Random();
	
	private Activity context;
	private View view;
	private TextView box;
	
	private BroadcastReceiver receiver = new Receiver();
	public class Receiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context ctx, Intent i)
		{
			sendNotify("onReceive "+i.getAction()+i.getData());
			box.setText("onReveive "+i.toString());
		}
	};
	
	public ServiceDemo(Activity ctx, Boolean add)
	{
		//super(ctx,R.layout.standard_service,add);
		context = ctx;
		view = View.inflate(ctx,R.layout.standard_service,null);
		FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT, 
			FrameLayout.LayoutParams.MATCH_PARENT);    
		if(add){
			context.addContentView(view,paras);
		}else{
			context.setContentView(view);
		}
		
		intent = new Intent(ctx, ServiceDefault.class);
		box = (TextView)view.findViewById(R.id.standardTextView);
		
		IntentFilter f = new IntentFilter(Intent.ACTION_ANSWER);
		f.addAction(Intent.ACTION_ANSWER);
		context.registerReceiver(receiver, f);
		//context.unregisterReceiver(receiver);
		
		Activity p = context;
		Button 
		b = (Button)p.findViewById( R.id.standardserviceButtonStart);
		b.setOnClickListener(this);
		b = (Button)p.findViewById( R.id.standardserviceButtonStop);
		b.setOnClickListener(this);
		b = (Button)p.findViewById( R.id.standardserviceButtonDelay);
		b.setOnClickListener(this);
		b = (Button)p.findViewById( R.id.standardserviceButtonBind);
		b.setOnClickListener(this);
		b = (Button)p.findViewById( R.id.standardserviceButtonUnbind);
		b.setOnClickListener(this);
		
		Intent i = new Intent(
			Intent.ACTION_ANSWER, // register this action in AndroidManifest.xml
			Uri.parse("hakka://anything.srartService"));
		i.setAction(intent.ACTION_ANSWER);
		context.sendBroadcast(i);
	}
	
	public void sendNotify(String msg)
	{
		int id = random.nextInt();

		Notification.Builder nb = new Notification.Builder(context);
		//nb.setDefaults(Notification.DEFAULT_SOUND);
		nb.setAutoCancel(true); // user fling to delete
		nb.setOngoing(false);
		nb.setCategory("ServiceDemoNotify");

		Intent i = new Intent(
			Intent.ACTION_ANSWER, // register this action in activity
			Uri.parse("hakka://jimbowhy.demo"), 
			context, ServiceDemo.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent
			.getActivity(context, id,i,PendingIntent.FLAG_ONE_SHOT);
		nb.setContentIntent(pi); // click notification to trigger
		nb.setDeleteIntent(pi); // click clear Notification to trigger

		i = new Intent(context, ServiceDemo.class);
		pi = PendingIntent.getBroadcast(context,id,i,PendingIntent.FLAG_ONE_SHOT);
		nb.setFullScreenIntent(pi, false); // fullscreen mode to trigger

		long[] patterm = {0,100,300,100,300,300};//[delay,vibrate,rest,vibrate,rest...]
		nb.setVibrate(patterm);

		AudioManager am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
		if( am.getRingerMode()==am.RINGER_MODE_NORMAL )
		{
			Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			//Uri soundUri = Uri.fromFile(new File...
			nb.setSound(tone, AudioAttributes.USAGE_ALARM);
		}
		nb.setLights(0xff2311ff, 1000,1000);

		nb.setContentTitle(context.getString(R.string.app_name));
		nb.setContentText(msg);
		nb.setSmallIcon(R.drawable.box_blue);

		Notification n = nb.getNotification();

		NotificationManager nm = (NotificationManager)
			context.getSystemService(context.NOTIFICATION_SERVICE);
		nm.notify(id, n );
	}

	// interface OnClickListenner
	@Override
	public void onClick(View v)
	{
		switch( v.getId() )
		{
			case R.id.standardserviceButtonStart:
				Intent i = new Intent(
					Intent.ACTION_ANSWER, // register this action in AndroidManifest.xml
					Uri.parse("hakka://anything.srartService"),
					context, ServiceDefault.class); // Service Intent must be explicit
				context.startService(i);
				box.setText( "Start Service");
				break;
			case R.id.standardserviceButtonStop:
				context.stopService(intent);
				box.setText( "Stop Service");
				break;
			case R.id.standardserviceButtonDelay:
				new Timer().schedule( new TimerTask(){
					@Override
					public void run(){ context.startService(intent); }
				}, 3000);
				box.setText( "Delay to start Service");
				break;
				
			case R.id.standardserviceButtonBind:
				box.setText( "Binding");
				isBound = true;
				context.bindService(intent, this, 
					Activity.BIND_WAIVE_PRIORITY
					|Activity.BIND_AUTO_CREATE); // avoid onStartCommand
				break;
			case R.id.standardserviceButtonUnbind:
				if(!isBound) break;
				isBound = false;
				context.unbindService(this);
				box.setText( "Unbind service");
				break;
		}
	
	}

	// interface ServiceConnection
	@Override
	public void onServiceConnected(ComponentName cn, IBinder b)
	{
		binder = (ServiceDefault.BinderDemo)b;
		binder.getServiceInstance().demo_service("Congratulation!");
		sendNotify("Service connected Tester pid:"+android.os.Process.myPid());
		box.setText("Service connected & get service");
		UncaughtAgent.i("ServiceDemo onServiceConnected");
	}

	@Override
	public void onServiceDisconnected(ComponentName p1)
	{
		sendNotify("Service disconnected");
		box.setText("Service disconnected");
		binder = null;
		UncaughtAgent.i("ServiceDemo onServiceDisconnected");
	}
}
