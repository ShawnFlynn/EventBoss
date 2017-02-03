package com.tssg.eventboss2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tssg.eventboss2.utils.misc.MakeToast;
import com.tssg.eventsource.BELEvent;
//----------- this is the old version ----------------------------------------
/** The SaveList is the list of saved events seen on the Saved tab */
public class SaveListDisplayActivity extends Activity { // 

	
		public static MainAppScreen m_mainAppScreen = null;
		public View m_mainAppView = null;			//	view for find results	

		public static final String INTENT_LOG = "com.tssg.eventboss2.bLOGGING";
		public static TextView m_statusView = null;
		public static Context context = null;
		public boolean bLOGGING = true;				// do log
		public static final String TAG = "SLDAct";	// log's tag

		/* 
		 *   Set up the View containing the source list
		 *   Bring up the search routine, collect the parameters
		 *   Execute the search  (in the end show the results
		 */
		
		public static List<BELEvent> m_SavedEventsList = new ArrayList<BELEvent>();

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

	MakeToast.makeToast(this, "Show Saved Events.", MakeToast.LEVEL_USER);
	Log.e(TAG, "Show Saved Events.");
	
			// Second make a copy of the current list of events
			Iterator<BELEvent> iterator = EventListDisplayActivity.m_webEventsList.iterator();
			int i = 0;
			while (iterator.hasNext() & i < 5) {
					// save Event into saved events list
					// until "save" is implemented
					m_SavedEventsList.add( iterator.next() );
					i = i + 1;
			}
			
			 String str;
			 str = "Show " + m_SavedEventsList.size() + "Saved Events.";
	Log.e(TAG, str);
	MakeToast.makeToast(this, str, MakeToast.LEVEL_USER);

			// get optional parameters from the intent
			Intent intent = this.getIntent();
			bLOGGING = intent.getBooleanExtra(INTENT_LOG, bLOGGING);

			m_mainAppScreen = new MainAppScreenImpl();
			m_mainAppScreen.setupLogging(bLOGGING, TAG);
			m_mainAppScreen.setUp(this, bLOGGING, TAG);
			m_mainAppView = m_mainAppScreen.getView();
			this.setContentView(m_mainAppView);
			m_statusView = (TextView) (m_mainAppView.findViewById(R.id.status_line));

			// **** here is the ListView code:
			ListView m_listView = (ListView) m_mainAppView.findViewById(R.id.main_window_listview);
			context = this;

			m_listView.setCacheColorHint(R.color.white);
			m_listView.setBackgroundResource(R.color.white);
			m_mainAppScreen.setEventList(m_SavedEventsList, "list of Saved Events");

	str = "Show" + m_SavedEventsList.size() + " Saved Events.";
	MakeToast.makeToast(this, str, MakeToast.LEVEL_USER);
			m_statusView.setText(str);
			
}		// end OnCreate( ... )		

//add events (from save)
//delete event (or events - if multiple selections)

		// Called when an options item is clicked
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			final int itemId = item.getItemId();
			switch (itemId) { 

			case R.id.idDeleteSelected:  /// only delete item
				// remove one or more items from saved list
				break;
		    default:
				Log.i("Prefs ", "SaveListDisplayActivity - unimplemented " + Integer.toHexString(itemId) + " pressed");
				Toast.makeText(context,  "SaveListDisplayActivity - unimplemented " + Integer.toHexString(itemId) + " pressed", Toast.LENGTH_SHORT).show();
				break;
			}
			return true;  
		}
				

		@Override
		protected void onResume() {
			// TODO implement onResume
			super.onResume();

			MakeToast.makeToast(this, "SaveList onResume", MakeToast.LEVEL_DEBUG);
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			// only items : delete and quit (-> go to eventlist ?P)
//			getMenuInflater().inflate(R.menu.idDeleteSelected, menu);
			return true;
		}
	
}

