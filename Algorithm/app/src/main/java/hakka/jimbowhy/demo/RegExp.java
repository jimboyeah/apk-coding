package hakka.jimbowhy.demo;
import android.widget.*;
import java.util.regex.*;
import android.view.*;
import android.widget.AdapterView.*;
import android.widget.PopupMenu.*;

public class RegExp extends Standard 
implements MenuItem.OnMenuItemClickListener
{
	private String pattern="";
	private Pattern regexp;

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuItem m = menu.add("Email Example");
		m.setOnMenuItemClickListener(this);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemClick(MenuItem p1)
	{
		cmd.setText("^(\\w+)@(\\w+)\\.(\\w+)$");
		return false;
	}
	
	@Override
	public void onClickEnter(Button view)
	{
		String c = cmd.getText().toString();
		if (c.isEmpty()){
			pattern = c;
			print("pattern clear now, type regexp text for a new one");
		}else if( pattern.isEmpty() ) {
			pattern = c;
			try{
				regexp = Pattern.compile(pattern);
				print("RexExp pattern ready "+regexp.pattern());
				print("for next step type text to match");
			}catch(Exception e){
				print(e.getMessage());
				pattern = "";
			}
		}else{
			print(c);
			try{
				Matcher m = regexp.matcher(c);
				m.find();
				print("matche case "+m.group());
				print("match group "+m.groupCount());
				for(int i=0; i<=m.groupCount(); i++) print(m.group(i));
			}catch(Exception e){
				print(e.getMessage());
			}
		}
		cmd.setText("");
	}

	@Override
	public void onClickRun(Button view)
	{
		// TODO: Implement this method
	}
	
}
