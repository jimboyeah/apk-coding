package hakka.jimbowhy.demo;

import android.app.*;
import android.hardware.*;
import android.widget.*;
import java.util.*;

public class SensorProximityDemo extends EventAgent
implements SensorEventListener
{
	private SensorManager sm;
	private Sensor s;
	private String si = "";
	private float[] peaks;

	private TextView box;
	private EditText editor;

	public SensorProximityDemo(Activity ctx)
	{
		this(ctx, false);
	}
	public SensorProximityDemo(Activity ctx, Boolean add)
	{
		super(ctx, R.layout.standard, add);

		sm = (SensorManager) ctx.getSystemService(ctx.SENSOR_SERVICE);
		s = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		box = (TextView) view.findViewById(R.id.standardTextView);
		editor = (EditText) view.findViewById(R.id.standardEditText);

		List<Sensor> ss = sm.getSensorList(Sensor.TYPE_ALL);
		si = "\nSupported Sensor Info\n";
		for( Sensor i : ss ){
			si += i.getStringType() +"\n";
		}

		if( s==null ){
			box.setText(si);
			editor.setText("Sensor not found " + Sensor.STRING_TYPE_PROXIMITY);
		}else{
			sm.registerListener(this, s, SensorManager.SENSOR_DELAY_FASTEST);
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
		}
		int k = 0;
		for( float i : se.values){
			if( peaks[k]<i ) peaks[k] = i;
			k++;
		}
		String buf = "Measures the proximity of an object in cm:";
		buf += "\nAll values(x,y,z) : " +
			String.format("%+8.3f,%+8.3f,%+8.3f",
						  se.values[0],se.values[1],se.values[2] );
		buf += "\nPeak values(x,y,z) : " +
			String.format("%+8.3f,%+8.3f,%+8.3f",
						  peaks[0],peaks[1],peaks[2] );
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
