package hakka.jimbowhy.demo;
import android.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;
import android.util.*;
import android.content.pm.*;
import android.graphics.drawable.*;

public class Main extends ActivityGroup
{
	private int lastMenuClick = -1;
	private Map<String,Boolean> menuState;
	private TextView box;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		UncaughtAgent.installHandler(this);
		Log.i(getPackageName(), "I'm runing");
		
		box = (TextView)findViewById(R.id.mainTextView);
		EventAgent.setTouch(this, "onTouch", box, Gesture.disabled);
		EventAgent.setTouch(this, "onTouch", 
			findViewById(R.id.mainToggleButton), Gesture.disabled);
	}

	@Override
	protected void onStart()
	{
		UncaughtAgent.i("Activity onStart");
		super.onStart();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		UncaughtAgent.i("Activity onNewIntent "+
			intent.getAction()+intent.getData());
		super.onNewIntent(intent);
	}

	@Override
	public void onActionModeStarted(ActionMode mode)
	{
		UncaughtAgent.i("Activity onActionModeStart "+mode.getTitle());
		super.onActionModeStarted(mode);
	} 
	
	
	public Boolean onTouch(View v, MotionEvent me)
	{
		box.setText("Touch pointer:"+me.getPointerCount()+v.toString());
		return false;
	}

	@Override
	protected void onDestroy()
	{
		SharedPreferences sp = getSharedPreferences("menuState", 0);
		SharedPreferences.Editor ed = sp.edit();
		Iterator ids= menuState.keySet().iterator();
		while( ids.hasNext() )
		{
			String id = (String)ids.next();
			ed.putBoolean(id,menuState.get(id));
		}
		//ed.apply();
		ed.commit();
		super.onDestroy();
	}


 	public boolean onCreateOptionsMenu(Menu m)
	{
		getMenuInflater().inflate(R.menu.main_menu, m);
		m.getItem(m.size() - 1).setVisible(false);

		if (menuState==null)
		{
			SharedPreferences sp = getSharedPreferences("menuState", 0);
			menuState = (HashMap<String,Boolean>)sp.getAll();
			//box.setText("Get Preferences: " + menuState.toString());
		}
		return super.onCreateOptionsMenu(m);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		
		for (int i=0; i < menu.size(); i++)
		{
			MenuItem item = menu.getItem(i);
			item.setVisible(true);
		}
		if( lastMenuClick!=-1 ){
			MenuItem m = menu.findItem(lastMenuClick);
			if (m.getItemId() != R.id.menuMainStorage2 
			&& m.getItemId() != R.id.menuMainAppIcon)
			m.setVisible(false);
		}
		Iterator ids = menuState.keySet().iterator();
		while( ids.hasNext() )
		{
			String i = (String) ids.next();
			Integer id = Integer.valueOf(i.replace("item_",""));
			MenuItem item = menu.findItem(id);
			if(item!=null)
				item.setChecked(menuState.get(i));
		}

		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menuMainStorage1:
				setContentView(getActivityView(
								   "action-demo", StorageDemo.class));
				break;
			case R.id.menuMainStorage2:
				Intent i = new Intent(this, StorageDemo.class);
				startActivity(i);
				break;
			case R.id.menuMainService:
				new ServiceDemo(this,false);
				break;
			case R.id.menuMainAppIcon:
				item.setChecked(!item.isChecked());
				menuState.put("item_"+item.getItemId(), item.isChecked());
				onMenuClickAppIcon(item);
				break;
			case R.id.menuMainListView:
				new ListViewDemo(this, false);
				break;
			case R.id.menuMainTheme:
				setContentView(getActivityView(
								   Intent.ACTION_DEFAULT, ThemeDemo.class));
				break;
			case R.id.menuMainReflection:
				setContentView(getActivityView(
								   Intent.ACTION_DEFAULT, ReflectionDemo.class));
				break;
			case R.id.menuMainDrawable:
				new AnDrawable(this);
				break;
			case R.id.menuMainLevelList:
				new LevelListDemo(this);
				break;
				
			case R.id.menuMainSensors:
				new SensorsSupported(this);
				break;
			case R.id.menuMainProximity:
				new SensorProximityDemo(this);
				break;
			case R.id.menuMainLightSensor:
				new SensorLightDemo(this);
				break;
			case R.id.menuMainAccelerator:
				new SensorAccelerDemo(this);
				break;
			case R.id.menuMainPressure:
				new SensorPressureDemo(this);
				break;
			case R.id.menuMainGyroscope:
				new SensorGyroscopeDemo(this);
				break;
			case R.id.menuMainMagnetic:
				new SensorMagneticDemo(this);
				break;
			case R.id.menuMainMagneticRaw:
				new SensorMagneticRawdata(this);
				break;
			case R.id.menuMainOrientation:
				new SensorOrientationDemo(this);
				break;
			case R.id.menuMainRotation:
				new SensorRotationDemo(this);
				break;
			case R.id.menuMainStepCounter:
				new SensorStepDemo(this);
				break;
			case R.id.menuMainStepDetector:
				new SensorStepDetectorDemo(this);
				break;
			case R.id.menuMainPedometer:
				new SensorPedometerDemo(this);
				break;
			default:
				setContentView(R.layout.main);

		}
		// it does't work (MenuItem)findViewById(R.id.menuMainReturn);

		lastMenuClick = item.getItemId();
		invalidateOptionsMenu();
		return super.onOptionsItemSelected(item);
	}

	public void onClickToggle(View v)
	{
		if (!((ToggleButton)v).isChecked())
		{
			finish();
		}

		SharedPreferences sp = getSharedPreferences("menuState", 0);
		box.setText("Preferences Setting:" + sp.getAll().toString());
	}

	// set app icon, ref: activityAlias
	public void onMenuClickAppIcon(MenuItem m)
	{
		PackageManager pm = getPackageManager();
		ComponentName e;
		//ComponentName c = getComponentName();
		ComponentName c = new ComponentName(
			getBaseContext(), getPackageName() + ".Main");
		ComponentName d = new ComponentName(
			getBaseContext(), getPackageName() + ".IconReplace");

		box.setText("Current Launcher:" + getComponentName());

		if (m.isChecked())
		{
			e = d; 
			d = c;
			c = e;
		}
		pm.setComponentEnabledSetting(c,
									  PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
									  PackageManager.DONT_KILL_APP);
		pm.setComponentEnabledSetting(d,
									  PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
									  PackageManager.DONT_KILL_APP);
	}

	public View getActivityView(String id, Class oClass)
	{
		LocalActivityManager am = getLocalActivityManager();
		Intent i = new Intent(this, oClass)
			.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return am.startActivity(id, i).getDecorView();
	}
}

