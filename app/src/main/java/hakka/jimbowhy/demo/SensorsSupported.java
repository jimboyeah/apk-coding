package hakka.jimbowhy.demo;

import android.app.*;
import android.widget.*;
import android.hardware.*;
import java.util.*;

public class SensorsSupported extends EventAgent
{
	public SensorsSupported(Activity ctx){
		this(ctx, false);
	}
	public SensorsSupported(Activity ctx, Boolean add)
	{
		super(ctx, R.layout.standard, add);


		SensorManager sm = (SensorManager) ctx.getSystemService(ctx.SENSOR_SERVICE);
		
		TextView box = (TextView) view.findViewById(R.id.standardTextView);
		//EditText editor = (EditText) view.findViewById(R.id.standardEditText);

		List<Sensor> ss = sm.getSensorList(Sensor.TYPE_ALL);
		String[] m = {"CONTINUES","ONE_CHANGE","ONE_SHOT","SPECIAL_TRIGGER"};
		//String[] a = { "NO_CONTACT","UNRELIABLE","LOW","MIDIUME","HIGH"};
		String si = "\nSupported Sensor Info:\n";
		for( Sensor i : ss ){
			si += String.format("\n%s (%d) wakeup_%s mode_%s \n ver_%s maxDely_%s range_%s resolition_%s power_%s\n",
			i.getStringType(), i.getType(), i.isWakeUpSensor(),
			m[i.getReportingMode()],i.getVendor(), i.getVersion(),
			i.getMaxDelay(),i.getMaximumRange(),i.getResolution(),i.getPower());
		}
		si += "讲个笑话：\n"
		+ "A:手机的加速度传感器的精度有多高？做两次积分。能不能在一公里内较为精确地测量距离？\n"
		+ "B:经典的惯性导航问题，误差是时间的平方或立方，1000米的误差有几百米或几千米很正常。";
		box.setText(si);
		
	}
}
