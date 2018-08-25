package hakka.jimbowhy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.IntentFilter;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.util.Set;
import android.content.*;


public class BluetoothDemo extends Activity 
{
	private TextView box;
	private UncaughtAgent agent;
	private BluetoothAdapter adapter;
	private ProgressDialog progressDialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		box = (TextView)findViewById(R.id.mainTextView);
		
		agent = UncaughtAgent.installHandler(this);
		progressDialog = new ProgressDialog(this);
		
		init();
    }
	
	private void init()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
		filter.addAction(BluetoothDevice.ACTION_UUID);
		filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		filter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(new BluetoothReceiver(this),filter);

		adapter = BluetoothAdapter.getDefaultAdapter();
		if(!adapter.isEnabled()) adapter.enable();
		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		for( BluetoothDevice d:devices ) 
			print(String.format("%20s:%s",""));
	}
	
	public void print(String msg)
	{
		box.setText(msg+"\n");
	}
	/*
	private void saveDeviceAddress(BluetoothDevice device) {  
		SharedPreferences.Editor editor = 
			getSharedPreferences("BlueToothDevices",MODE_PRIVATE).edit();  
		editor.putString("address", device.getAddress());  
		editor.commit();
		print(String.format("Bluetooth device saved (%s,%s)", 
			device.getName(), device.getAddress()));
    }
	
	public  void autoConnect(){  
	
		SharedPreferences pref=getSharedPreferences( "device",MODE_PRIVATE);  
		String deviceAddress=pref.getString("address","");  
		if (deviceAddress!=null){  
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter  
				.getBondedDevices();// 获取本机已配对设备  
			if (pairedDevices.size() > 0) {  
				for (BluetoothDevice device1 : pairedDevices) {  
					if (device1.getAddress().equals(deviceAddress))  
						device=device1;  
					break;  
				}  
			}  
        }  
	}  
	
	 String SPP_UUID = "00000000-0000-1000-8000-00805F9B34FB"; 
	 UUID uuid = UUID.fromString(SPP_UUID); 
	 try { 
	 // btSocket = btDev.createRfcommSocketToServiceRecord(uuid); 
	 btSocket =(BluetoothSocket) btDev.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(btDev,1);

	 Log.d("BlueToothTestActivity", "开始连接..."); 
	 btSocket.connect(); 
	 } catch (IOException e) {
		 
	private void getDeviceAndConnect(){
		final Intent intent = this.getIntent();  
		BluetoothDevice device = intent.getParcelableExtra("device");  
		if (device==null){  
			autoConnect();  
		}  
		if (device!=null){  
			progressDialog.show();  
			new ConnectThread(device).start();}  
	}  */
}

class BluetoothReceiver extends BroadcastReceiver
{
	private BluetoothDemo context;
	private BluetoothAdapter adapter;
	
	public BluetoothReceiver(BluetoothDemo ctx)
	{
		context = ctx;
		adapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	@Override
	public void onReceive(Context ctx, Intent it)
	{
		Bundle bundle = it.getExtras();
		Set<String> names = bundle.keySet();
		for( String key : names){
			context.print(String.format("%20s:%s",key,bundle.get(key)));
		}
		
		String action = it.getAction();
		context.print(action);
		
		BluetoothDevice device = 
			it.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		
		switch(it.getAction())
		{
			case BluetoothDevice.ACTION_FOUND:
				if(device.getBondState()==BluetoothDevice.BOND_NONE)
					adapter.c
					break;
			case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
				break;
			default:
				
	
		}
	}

}
