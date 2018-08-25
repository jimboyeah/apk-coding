package hakka.jimbowhy.demo;
import android.app.*;
import android.hardware.*;
import android.widget.*;
import java.util.*;

public class SensorPressureDemo extends EventAgent
implements SensorEventListener
{
	private SensorManager sm;
	private Sensor s;
	private String si = "";

	private TextView box;
	private EditText editor;

	public SensorPressureDemo(Activity ctx)
	{
		this(ctx, false);
	}
	public SensorPressureDemo(Activity ctx, Boolean add)
	{
		super(ctx, R.layout.standard, add);

		sm = (SensorManager) ctx.getSystemService(ctx.SENSOR_SERVICE);
		s = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);

		box = (TextView) view.findViewById(R.id.standardTextView);
		editor = (EditText) view.findViewById(R.id.standardEditText);

		List<Sensor> ss = sm.getSensorList(Sensor.TYPE_ALL);
		si = "\nSupported Sensor Info:\n";
		for( Sensor i : ss ){
			si += i.getStringType() +"\n";
		}

		if( s==null ){
			box.setText(si);
			editor.setText("Sensor not found " + Sensor.STRING_TYPE_PRESSURE);
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
		String buf = "Atmospheric pressure values[0] in hPa (millibar):";
		buf += "\n    value: " + Arrays.toString( se.values );
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
