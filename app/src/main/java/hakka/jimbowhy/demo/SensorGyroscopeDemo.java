package hakka.jimbowhy.demo;

import android.app.*;
import android.hardware.*;
import android.widget.*;
import java.util.*;

public class SensorGyroscopeDemo extends EventAgent
implements SensorEventListener
{
	private SensorManager sm;
	private Sensor s;
	private String si = "";
	private float[] peaks;
	private float[] nadir;

	private TextView box;
	private EditText editor;

	public SensorGyroscopeDemo(Activity ctx)
	{
		this(ctx, false);
	}
	public SensorGyroscopeDemo(Activity ctx, Boolean add)
	{
		super(ctx, R.layout.standard, add);

		sm = (SensorManager) ctx.getSystemService(ctx.SENSOR_SERVICE);
		s = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		box = (TextView) view.findViewById(R.id.standardTextView);
		editor = (EditText) view.findViewById(R.id.standardEditText);

		List<Sensor> ss = sm.getSensorList(Sensor.TYPE_ALL);
		si = "\nSupported Sensor Info\n";
		for( Sensor i : ss ){
			si += i.getStringType() +"\n";
		}

		if( s==null ){
			box.setText(si);
			editor.setText("Sensor not found " + Sensor.STRING_TYPE_GYROSCOPE);
		}else{
			sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
			//sm.unregisterListener(this, s);

			String buf = "\nCurrent Sensor info:";
			buf += "\n     name: " +s.getName();
			buf += "\n minDelay: " +s.getMinDelay();
			buf += "\n maxDelay: " +s.getMaxDelay();
			buf += "\n maxRange: " +s.getMaximumRange();
			buf += "\n    power: " +s.getPower();
			buf += "\n reportMode: " +s.getReportingMode();
			buf += "\n resolution: " +s.getResolution();
			buf += "\n stringType: " +s.getStringType();
			buf += "\n     type: " +s.getType();
			buf += "\n   vendor: " +s.getVendor();
			buf += "\n  version: " +s.getVersion();
			si = buf +si;
		}
	}

	@Override
	public void onSensorChanged(SensorEvent se)
	{
		if( peaks==null ){
			peaks = new float[se.values.length];
			nadir = new float[se.values.length];
		}
		int k = 0;
		for( float i : se.values){
			if( peaks[k]<i ) peaks[k] = i;
			if( nadir[k]>i ) nadir[k] = i;
			k++;
		}
		
		float rat = 57.29f; // degree/rad
		String buf = "Rotation in rad/s of the three physical axes:";
		buf += "\nAll values(x,y,z) : " +
			String.format("%+8.3f,%+8.3f,%+8.3f",
						  se.values[0],se.values[1],se.values[2] );
		buf += "\nPeak values(x,y,z) : " +
			String.format("%+8.3f,%+8.3f,%+8.3f",
						  peaks[0],peaks[1],peaks[2] );
		buf += "\nAll values in degree(x,y,z) : " +
			String.format("%+8.3f,%+8.3f,%+8.3f",
						  se.values[0]*rat, se.values[1]*rat, se.values[2]*rat );
		buf += "\nPeak values(x,y,z) : " +
			String.format("%+8.3f,%+8.3f,%+8.3f",
						  peaks[0]*rat ,peaks[1]*rat ,peaks[2]*rat );
		buf += "\nNadir values : " +
			String.format("%+8.3f,%+8.3f,%+8.3f",
						  nadir[0]*rat,nadir[1]*rat,nadir[2]*rat );
		buf += "\n accuracy: " + se.accuracy;
		buf += "\ntimestamp: " + se.timestamp;

		box.setText(buf+si);
	}

	@Override
	public void onAccuracyChanged(Sensor s, int a)
	{
		editor.setText("accuracy changed: "+a);
	}



	//@Override 
}
