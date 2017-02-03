package com.tssg.eventboss2;

//import EB2MainActivity;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class SavedListDisplayActivity extends Activity {

	String TAG = "SavedListDisplayAct";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.listdisplay_activity_action);
        setContentView(R.layout.activity_saved_list_display);
        if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.fragment_container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.saved_list_display, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int optionSelected = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
        switch (optionSelected) {
            case R.id.itemPrefs:
                Log.v(TAG, "Settings ");
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.idDeleteSelected:
                Log.v(TAG, "optionSelected - Delete");
                Toast.makeText(this, "EBMain - Delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.idCalendar:
                Log.v(TAG, "optionSelected - Calendar");
                Toast.makeText(this, "EBMain - Calendar", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_share:
                Log.v(TAG, "optionSelected - idShare pressed");
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                ProcessShare(item);
                break;
            default:
                Log.d(TAG,"optionSelected "+ " " + "xxxxx" + " "
                        + Integer.toHexString(optionSelected) + " " + "yyy");
                Toast.makeText(this, " " + Integer.toHexString(optionSelected)
                        + " " + "yyy", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

	void ProcessShare(MenuItem item) {

		ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();

		//Toast.makeText(this, mResources.getString(R.string.doTheShare) + " ", Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "doTheShare" + " ", Toast.LENGTH_SHORT).show();

		// collect data for sharing - this sends an MMS  ?????
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, "enters text to sent!");
		shareIntent.setType("text/plain");

		shareIntent.putExtra(Intent.EXTRA_EMAIL, "this is an extra string");
		String str[] = {"qwerty", "asdfgh"};
		shareIntent.putExtra(Intent.EXTRA_EMAIL, str);

		mShareActionProvider.setShareIntent(shareIntent);

		Log.d("ProcessShare", " shareIntent " + shareIntent);

		// send off shared data
		startActivity(Intent.createChooser(shareIntent, "Events List"));
		Log.d("ProcessShare", " after chooser " + shareIntent);

	}    // end --- ProcessShare



		/**
		 * A placeholder fragment containing a simple view.
		 */
	//public static class PlaceholderFragment extends Fragment {
      public static class PlaceholderFragment extends Fragment {
                public PlaceholderFragment() {
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                         Bundle savedInstanceState) {
                    View rootView = inflater.inflate(
                            R.layout.fragment_saved_list_display, container, false);
                    return rootView;
                }
            }

}
