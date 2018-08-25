package hakka.jimbowhy.demo;
import android.app.*;
import android.os.*;
import android.content.*;
import android.widget.*;

class Alert extends DialogFragment
{
	private String title;
	private String message;
	
	static public void toast(Context ctx, String message)
	{
		Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
	}
	static public void show(Activity ctx, String title, String message)
	{
		Alert alert = new Alert();
		alert.title = title;
		alert.message = message;
		alert.show(ctx.getFragmentManager(),"alertdialog");
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
				}
			});
		
		//builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener (){

		return builder.create();
	}

}
