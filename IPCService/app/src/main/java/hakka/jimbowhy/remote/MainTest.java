package hakka.jimbowhy.remote;
import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.content.*;
import android.widget.*;
import java.util.*;
import android.net.*;

public class MainTest extends Activity implements 
View.OnClickListener, ServiceConnection
{
	public String IPC_INQUIRE = "isServiceRuning";
	private Boolean isBound = false;
	private Intent intent;
	private IRemoteService service;
	private TextView box;
	
	private BroadcastReceiver receiver;
	
	@Override
	public void onServiceConnected(ComponentName cn, IBinder binder)
	{
		service = IRemoteService.Stub.asInterface(binder);
	}

	@Override
	public void onServiceDisconnected(ComponentName cn)
	{
		sendNotify("IPC Client onServiceDisconnected");
	}
	
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.mainButtonBind:
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
				doTest();
				break;
		}
	}

	private void doTest()
	{
		try
		{
			if (service != null)
			{
				box.setText(service.getDemoService(new Random().nextInt(8)));
				service.sendNotify("MainTester pid:" + android.os.Process.myPid());
			}
		}
		catch (RemoteException e)
		{
			box.setText(e.getMessage());
		}
		
		Intent v = new Intent(Intent.ACTION_VIEW);
		v.putExtra("data","http://www.i.com");
		sendBroadcast(v);

		v = new Intent(RemoteHelper.ACTION_INCOME);
		v.putExtra("data",RemoteHelper.C_INQUIRE);
		sendBroadcast(v);
	}
	
	public Boolean sendNotify(String m)
	{
		try{
			service.sendNotify(m);
			return true;
		}catch(Exception e){}
		return false;
	}

	
	@Override
	protected void onStop()
	{
		sendNotify("IPC Client onStop");
		super.onStop();
	}
	@Override
	protected void onPause()
	{
		sendNotify("IPC Client onPause");
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		sendNotify("IPC Client onResume");
		super.onResume();
	}
	@Override
	protected void onRestart()
	{
		sendNotify("IPC Client onRestart");
		super.onRestart();
	}
	

	@Override
	protected void onDestroy()
	{
		sendNotify("IPC Client onDestroy");
		if(isBound) unbindService(this);
		stopService(intent);
		service = null;
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		UncaughtAgent.installHandler(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		receiver = new RemoteReceiver(this);
		registerReceiver(receiver,
			new IntentFilter(RemoteHelper.ACTION_BROADCAST));
			
		intent = new Intent(RemoteHelper.IPC_SERVICE);
		intent.setPackage("hakka.jimbowhy.remote");
		intent.putExtra("data","somedata");
		//Uri uri = Uri.parse("ipc://hakka.jimbowhy/here/data");
		//intent.setData(uri); //this unworking or intent-filter configured
		startService(intent);
		isBound = bindService(intent,this,
				BIND_WAIVE_PRIORITY|BIND_AUTO_CREATE);
		
		
		
		sendNotify("IPC Client onCreate");
		findViewById(R.id.mainButtonBind).setOnClickListener(this);
		findViewById(R.id.mainButtonTest).setOnClickListener(this);
		findViewById(R.id.mainButtonUnbind).setOnClickListener(this);
		box = (TextView)findViewById(R.id.mainTextView);
	}
	
}
