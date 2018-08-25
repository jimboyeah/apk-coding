package hakka.jimbowhy.demo;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.PopupMenu.*;
import android.content.*;
import android.widget.*;
import android.view.View.*;
import android.text.*;


public abstract class Standard extends MainActivity
implements OnClickListener, TextWatcher
{
	protected TextView box;
	protected EditText cmd;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.standard);

		box = (TextView)findViewById(R.id.mainTextView);
		cmd = (EditText)findViewById(R.id.mainEditText);
		findViewById(R.id.mainButtonEnter).setOnClickListener(this);
		findViewById(R.id.mainButtonRun).setOnClickListener(this);
		box.addTextChangedListener(this);

		clear();
		print(getString(R.string.hello_world));
	}
	public void clear()
	{
		box.setText("");
	}
	public void print(int i)
	{
		print(i+"");
	}
	public void print(String line)
	{
		box.append(">>" + line + "\n");
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.mainButtonEnter:
				onClickEnter((Button)v);
				break;
			case R.id.mainButtonRun:
				onClickRun((Button)v);
				break;
		}

	}

	public abstract void onClickEnter(Button view);
	public abstract void onClickRun(Button view);

	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		ScrollView sv = (ScrollView)findViewById(R.id.standardScrollView);
		//int svy = sv.getScrollY();
		int h = box.getLineHeight()*box.getLineCount();
		int sh = sv.getHeight()-sv.getPaddingTop()-sv.getPaddingBottom();
		int ch = box.getHeight()+box.getPaddingTop()+box.getPaddingBottom();
		//setTitle(String.format("svy %d sh %d ch %d", svy,sh,ch));
		sv.scrollTo(0,h);

	}

	@Override
	public void afterTextChanged(Editable p1)
	{
		// TODO: Implement this method
	}

}
