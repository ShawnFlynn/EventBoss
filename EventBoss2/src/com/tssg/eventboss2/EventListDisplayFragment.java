package com.tssg.eventboss2;

//import com.tssg.eventboss2.Ipsum;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tssg.eventsource.BELEvent;
//import com.example.android.fragments.HeadlinesFragment.OnHeadlineSelectedListener;

//this fragment implements functions that were in the EventDisplayActivity
//maybe the code in currentSection list should be implemented here !!!!

public class EventListDisplayFragment extends ListFragment {

    public static final String TAG = "EventListFrag";   // log's tag

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;


    OnListItemSelectedListener mCallback;
//	Context context = getBaseContext();

	public static List<BELEvent> m_webEventsList = new ArrayList<BELEvent>();

	public TextView m_statusView = null;

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// inflate the layout for this list fragment
//		return inflater.inflate(R.layout.listfragment, container, false);
//	}
				
	public interface OnListItemSelectedListener {
		public void onListItemSelected( int position );
		
	}


    //////// selecting list item
    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    //////// end selectingListItem
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

        setListAdapter(new ArrayAdapter<BELEvent>(getActivity(), layout, EventListDisplayActivity.m_webEventsList));
    }

    @Override
    public void onStart() {
        super.onStart();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        if (getFragmentManager().findFragmentById(R.id.itemfragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
//            mCallback = (OnHeadlineSelectedListener) activity;
            mCallback = (OnListItemSelectedListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnListItemSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
		Log.i(TAG, " onListItemSelected "+position);
        mCallback.onListItemSelected(position);
        
        // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
    }
 
    
	static void showWebEventsList () {
		// questionable if saving to database should be done here

		m_webEventsList = EB2MainActivity.m_webEventsList;
		Log.i(TAG, "->WebEventsList -> q = " + m_webEventsList.size() );
	} // -------  end:   showWebEventsList()
	
	/**
	 * Write a message to the status window. 
	 * Does nothing if there is no status window.
	 */
	void showStatus(String statusMessage) {
		if (m_statusView != null) {
			m_statusView.setText(statusMessage);
		}
	}

}	// end: ListDisplayFragment