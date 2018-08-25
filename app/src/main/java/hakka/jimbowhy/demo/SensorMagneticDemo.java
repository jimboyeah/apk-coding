package hakka.jimbowhy.demo;
import android.app.*;
import android.hardware.*;
import android.widget.*;
import java.util.*;

public class SensorMagneticDemo extends EventAgent
implements SensorEventListener
{
	private SensorManager sm;
	private Sensor s;
	private String si = "";
	private float[] peaks;
	private float[] nadir;

	private TextView box;
	private EditText editor;

	public SensorMagneticDemo(Activity ctx)
	{
		this(ctx, false);
	}
	public SensorMagneticDemo(Activity ctx, Boolean add)
	{
		super(ctx, R.layout.standard, add);

		sm = (SensorManager) ctx.getSystemService(ctx.SENSOR_SERVICE);
		s = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		box = (TextView) view.findViewById(R.id.standardTextView);
		editor = (EditText) view.findViewById(R.id.standardEditText);

		List<Sensor> ss = sm.getSensorList(Sensor.TYPE_ALL);
		si = "\nSupported Sensor Info\n";
		for( Sensor i : ss ){
			si += i.getStringType() +"\n";
		}

		if( s==null ){
			box.setText(si);
			editor.setText("Sensor not found " + Sensor.STRING_TYPE_MAGNETIC_FIELD);
		}else{
			sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
			//sm.unregisterListener(this, s);
			String[] m = {"CONTINUES","ONE_CHANGE","ONE_SHOT","SPECIAL_TRIGGER"};
			String buf = "\nCurrent Sensor info:";
			buf += "\n     name: " +s.getName();
			buf += "\n minDelay: " +s.getMinDelay();
			buf += "\n maxDelay: " +s.getMaxDelay();
			buf += "\n maxRange: " +s.getMaximumRange();
			buf += "\n    power: " +s.getPower();
			buf += "\n reportMode: " +m[s.getReportingMode()];
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
		String[] a = { "NO_CONTACT","UNRELIABLE","LOW","MIDIUME","HIGH"};
		
		String buf = "Geomagnetic field (x,y,z) in μT:";
		buf += "\nAll values(l: " +
			String.format("%+8.3f,%+8.3f,%+8.3f",
						  se.values[0],se.values[1],se.values[2] );
		buf += "\nPeak values : " +
			String.format("%+8.3f,%+8.3f,%+8.3f",
						  peaks[0],peaks[1],peaks[2] );
		buf += "\nNadir values : " +
			String.format("%+8.3f,%+8.3f,%+8.3f",
						  nadir[0],nadir[1],nadir[2] );
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
