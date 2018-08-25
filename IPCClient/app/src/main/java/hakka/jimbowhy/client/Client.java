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
	public String ACTION = "hakka.jimbowhy.remote.IRemoteService";
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

