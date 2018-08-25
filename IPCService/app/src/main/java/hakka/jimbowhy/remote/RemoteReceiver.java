package hakka.jimbowhy.remote;

import android.content.*;
import android.app.*;

public class RemoteReceiver extends BroadcastReceiver
{
	private String name;
	private MainTest context;
	
	public RemoteReceiver()
	{
		name = "default";
	}
	public RemoteReceiver(MainTest ctx)
	{
		name = "Cient";
		context = ctx;
	}

	@Override
	public void onReceive(Context ctx, Intent it)
	{
		String inf = it.getStringExtra("data");
		
		if(context!=null)
			context.sendNotify(name+" onReceive:" + inf);
	}

	
}
