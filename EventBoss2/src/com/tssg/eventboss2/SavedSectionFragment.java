package com.tssg.eventboss2;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tssg.datastore.DatabaseHelper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fragment which contains the saved-list activity.
 * ========================================================
 */
public class SavedSectionFragment extends EventBossListFragment {

    static final String TAG = "SavedSectionFragment";  	// log's tag
//	public static boolean mListType = true;		   		// the saved list
	public static int mListType = 1;		   			// -> saved list
	public static TextView mListHeader;
	public static int mPosition = -1;
	public static long mId;

	private static DatabaseHelper dbh;	

	Cursor mCursor;
	SimpleCursorAdapter mAdapter;
	static int mEventItemCount;
	LayoutInflater mLayoutInflater;
	ViewGroup mViewGroup;

	ListView mLV;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "OnCreate: --- SavedSectionFragment");
		super.onCreate(savedInstanceState);
		if ( dbh == null ) {
			dbh = new DatabaseHelper(getActivity());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mViewGroup = container;
		mLayoutInflater = inflater;
		Log.v(TAG, "onCreateView: container" +container);
		return super.onCreateView(inflater, container, savedInstanceState);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.v(TAG, "( CurrentSectionFragment.onActivityCreated: "+"before cursor, dbh:"+dbh);
		mCursor = dbh.getCursorSavedEvents();
		Log.v(TAG, "( CurrentSectionFragment.onActivityCreated: "+"after cursor");
		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DatabaseHelper.KEY_TITLE,
			    				 DatabaseHelper.KEY_STARTTIME,
			    				 DatabaseHelper.KEY_ENDTIME,
			    				 DatabaseHelper.KEY_LOCATION};
		int[] toViews = { R.id.title, R.id.time, R.id.endtime, R.id.location };

		mEventItemCount = mCursor.getCount();
		mLV = getListView();
		mLV.setHeaderDividersEnabled(true); 
		mLV.setDividerHeight(5);				// = divider between list items
		
		// Create an empty adapter we will use to display the loaded data.		// <------------
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.eventlist_row, mCursor, fromColumns, toViews, 0);
		setListAdapter(mAdapter);  //???? what does this do exactly

		
		mListHeader = (TextView) mLayoutInflater.inflate(R.layout.listheader, null); 
		mLV.addHeaderView(mListHeader);

        updateList();   // can not implement in SavedSectionFragment
	}
	
	@Override
	public void onLoadFinished() {
		// there is a constructor in EventBossListFragment
		setListAdapter(mAdapter);  //???? what does this do exactly
        updateListHeader("have " + mEventItemCount + " saved Events");	//<from
	}

	@Override
	public void onResume() {
		super.onResume();
		// update the list  in case there an addition/deletion
		updateList();
		Log.v(TAG, "------->  onResume: after update list");

	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.v(TAG, "onListItemClick: Listview=" + l
                + ":View=" + v + ":Position=" + position + ":Id=" + id);
/*		eventFragmentCoordinator.displayEventDetails(Long.toString(id), true);

		Log.v(TAG, "(75)---> eventFragmentCoordinator ->displayEventDetails (false)");
		Log.v(TAG, "onListItemClick: Position=" + mPosition + ":mId=" + id);
		if( position > 0 ) {
			mPosition = position;
			mId = id;
			Log.v(TAG, "onListItemClick: Position=" + mPosition + ":mId=" + mId);

			eventFragmentCoordinator.displayEventDetails(Long.toString(mId), false);
		}
*/
        if( position > 0 ) {	// it's using only mId
            mPosition = position;
            mId = id;
            Log.v(TAG, "onListItemClick: Position=" + mPosition + ":mId=" + mId);

            eventFragmentCoordinator.displayEventDetails(Long.toString(mId), 1);
        }
    }

	@Override
	public void onDestroyView() {
		mCursor.close();
		super.onDestroyView();
	}

	/*
	/ *  	getActivity()
	/ *	setListAdapter
	/ *	error on updateListHeader	*/
//	public static void updateList() {		// static is bad to call from eventdetail
	/*
	 * 
	 */
	public void updateList() {
		/*
		 * the SavedSectionFragment should reload the list when an 'add' or 'delete' has been done 
		 */
		Log.v(TAG, "update saved list");
		
		dbh = new DatabaseHelper(getActivity());
		mCursor.close();
		mCursor = dbh.getCursorSavedEvents();
		mAdapter.swapCursor(mCursor);
		setListAdapter(mAdapter);

		Log.v(TAG, "update saved list-header");
        updateListHeader("update saved list-header");
	}

     void updateListHeader( String extraText )  {
        // Create a list-header (TextView) and add it to the list like this:
        // mListHeader = (TextView) mLayoutInflater.inflate(R.layout.listheader, null);
        // mLV.addHeaderView(mListHeader);

        SimpleDateFormat simpFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US );
        Date m_channelDate = new Date(System.currentTimeMillis());
        String channelDate = m_channelDate == null? "--" : simpFormat.format(EB2MainActivity.m_channelDate);

        // this should be the current date or the date when data was saved into the database
        mListHeader.setText( extraText + "(" + channelDate + ") "+ mEventItemCount + " Events");
		Log.v(TAG, extraText +" "+ channelDate +", "+  mEventItemCount + " Events");

    }

    
} // ------- end SavedSectionFragment
