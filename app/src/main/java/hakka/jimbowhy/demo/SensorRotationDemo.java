package hakka.jimbowhy.demo;

import android.app.*;
import android.hardware.*;
import android.widget.*;
import java.util.*;

public class SensorRotationDemo extends EventAgent
implements SensorEventListener
{
	private SensorManager sm;
	private Sensor s;
	private String si = "";
	private float[] peaks;

	private TextView box;
	private EditText editor;

	public SensorRotationDemo(Activity ctx)
	{
		this(ctx, false);
	}
	public SensorRotationDemo(Activity ctx, Boolean add)
	{
		super(ctx, R.layout.standard, add);

		sm = (SensorManager) ctx.getSystemService(ctx.SENSOR_SERVICE);
		s = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		box = (TextView) view.findViewById(R.id.standardTextView);
		editor = (EditText) view.findViewById(R.id.standardEditText);

		List<Sensor> ss = sm.getSensorList(Sensor.TYPE_ALL);
		si = "\nSupported Sensor Info\n";
		for( Sensor i : ss ){
			si += i.getStringType() +"\n";
		}

		if( s==null ){
			box.setText(si);
			editor.setText("Sensor not found " + Sensor.STRING_TYPE_ROTATION_VECTOR);
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
		}
		int k = 0;
		for( float i : se.values){
			if( peaks[k]<i ) peaks[k] = i;
			k++;
		}
		float rat = 57.29f; // degree / rad
		String[] a = { "NO_CONTACT","UNRELIABLE","LOW","MIDIUME","HIGH"};
		
		String buf = "Rotation vector of the three physical axes:";
		buf += "\nAll values sin(θ/2)(x,y,z) : " +
		String.format("%+8.3f,%+8.3f,%+8.3f,%+8.3f,%+8.3f",
			se.values[0],se.values[1],se.values[2],se.values[3],se.values[4] );
		buf += String.format("\nPeak values: %s",Arrays.toString(peaks) );
		buf += String.format("\nDegree: %+8.2f,%+8.2f,%+8.2f,%+8.2f,%+8.2f",
			Math.asin(se.values[0])*rat,Math.asin(se.values[1])*rat,Math.asin(se.values[2])*rat,
			Math.asin(se.values[3])*rat,Math.asin(se.values[4])*rat );
		buf += "\n accuracy: " + se.accuracy + " " + a[se.accuracy+1];
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
