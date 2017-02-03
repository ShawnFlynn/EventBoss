package com.tssg.eventboss2;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tssg.datastore.DatabaseHelper;
import com.tssg.eventboss2.utils.misc.MakeToast;
import com.tssg.eventsource.BELEvent;


/** displays and handles the details of an event */
/* Notes for development:
 * 
 * */
public class EventDetailFragment extends Fragment {

    public static final String TAG = "EventDetailFragment";   // log's tag

    final public static String EVENTITEM_POS = "position";  // compiler suggested taking off 'static'
//    public static final String SAVED_KEY = "isSaved";		// ********* previous use ********
    public static final String DB_HELPER = "DBHelper";

	public static final String LIST_TYPE = null;


//    private boolean m_isSavedEvent; // Current (false)  or  Saved List (true)
    private int m_isListType; 		// Current (0), Saved List (1), Saved List (2)
    private DatabaseHelper dbh;		// = new DatabaseHelper(getActivity()) EBMainActivity;
    private String mId;             // use ItemId

    private BELEvent mEvent;        //
    private TextView mTitleText;	// title
    private TextView mStartText;	// start time
    private TextView mEndText;		// end time
    private TextView mTypeText;
    private TextView mLinkText;
    private TextView mOrganizerText;
    private TextView mLocationText;
    private TextView mDescriptionText;
 //   private Button m_forward = null;
 //   private Button m_addToCalendar;

 //   public EventDetailFragment() { m_isSavedEvent = false; } // is set in onCreate
 //   public void setListType(boolean listType ) {
    public void setListType(int listType ) {
    	  m_isListType = listType;
    	  Log.e(TAG, "onCreate: ListType = "+m_isListType);
    }

    public void setEventId(String id) {  mId = id;  }
//    public void setEventId(String id) {  mEvent = mId;  }
    public void setDBhelper(DatabaseHelper db) { dbh = db; }         // from  displayEventDetails

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Log.v(TAG, "onCreate: EventDetailFragment:");
        Log.v(TAG, "onCreate: List Type = "+m_isListType+" w/id: "+mId);
        String key = "";

        // Get a database handle
        // for tablet dbh is set when 'displayEventDetails' creates an EventDetailFragment
        // for phone  dhh is set here, because 'EventDetailActivity' creates the EventDetailFragment  
        if( dbh == null )  {
        	dbh = new DatabaseHelper(getActivity());
        }
        Log.v(TAG, " initialization EventDetailFragment:");
        Log.v(TAG, "onCreate: type: "+m_isListType+",  mId: "+mId+", db: "+dbh);

        if( m_isListType == 0 ) {    // ----- Current Section
        	// Get the current event
            Log.v(TAG, "*/current Event: "+mEvent+", id = "+mId);
        	try {mEvent = dbh.getEventById(mId);}
            catch (android.database.CursorIndexOutOfBoundsException exept ) {
                if (mEvent == null) {
                    Log.e(TAG, "we have an id, but no Event: " + mEvent);
                    Log.e(TAG, "this did happen when 'm_isSaved' should be false but is true ");
                    Log.e(TAG, "see event EventDetailActivity near line 56");
                    return;
                }
                ;
            }
            Log.v(TAG, "**/current Event: "+mEvent);
		}
        if( m_isListType == 1 ) {	// ----- Saved Section
            // Get the saved event
            Log.v(TAG, "saved Event: "+mEvent+", mId: "+mId+", dbh: "+dbh);
            
            if ( mId.isEmpty() ) 
            	Log.v(TAG, "can not delete: Event is empty");  //
            else 
            	mEvent = dbh.getSavedEventById(mId);
            
            Log.v(TAG, "saved Event: "+mEvent);
        }
        
        if( m_isListType == 2 ) {	// ----- Search section
            
            if ( mId.isEmpty() ) 
            	Log.v(TAG, "can not display: Event is empty");  //
            else 
            	Log.v(TAG, "EventDetailFragment(120) read id: " + mId);  //
/*
// - mId - should be the Id from CurrentEventList
//     also see --> SearchSectionFragment line 97  
            	try {
            		mEvent = dbh.getEventById(mId);
            	} catch ( SQLException exp ) {
            		Log.e( TAG, "caught SQLException: reading this id: "+mId ,exp );
            	}
            Log.v(TAG, "search Event: "+mEvent);
*/
        }

    }   // end --- onCreate()
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle SavedInstanceState) {

        Log.v(TAG, " inflate: -> fragment_event_detail, container:" + container);
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        Log.v(TAG, " view: " + view);

        mTitleText = (TextView) view.findViewById(R.id.titleText);
        mStartText = (TextView) view.findViewById(R.id.startText);
        mEndText = (TextView) view.findViewById(R.id.endText);
        mTypeText = (TextView) view.findViewById(R.id.typeText);
        mLinkText = (TextView) view.findViewById(R.id.linkText);
        mOrganizerText = (TextView) view.findViewById(R.id.organizerText);
        mLocationText = (TextView) view.findViewById(R.id.locationText);
        mDescriptionText = (TextView) view.findViewById(R.id.descriptionText);
// these buttons were on the page, but should now be on the Actionbar
//        m_forward = (Button)view.findViewById(R.id.forward);
//        m_addToCalendar = (Button)view.findViewById(R.id.addtocalendar);

        Log.v(TAG, " text: " +mTitleText);   // (TextView)
        Log.v(TAG, "................");
//        Log.v(TAG, " star: " +mStartText);
//        Log.v(TAG, " end: " +mEndText);
//        Log.v(TAG, " type: " +mTypeText);
//        Log.v(TAG, " link: " +mLinkText);
//        Log.v(TAG, " org : " +mOrganizerText);
//        Log.v(TAG, " loc: " +mLocationText);
//        Log.v(TAG, " desc: " +mDescriptionText);

        Log.v(TAG,"exit");
        return view;

    }   //  end --- onCreateView()

    @Override
    public void onStart() {
        super.onStart();
        refreshView();
        // note EGL_Emulation  .. eglSurfaceAttribute not Implemented
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


   public void refreshView() {
       Log.i(TAG,"->refresh view: mEvent= "+mEvent);
       if(mEvent==null ) {
           mTitleText.setText(" **** no Event ****");   // 
       }    else   {
       mTitleText.setText(mEvent.getTitle());
       mStartText.setText(mEvent.getStartTime());
       mEndText.setText(mEvent.getEndTime());
       mTypeText.setText(mEvent.getEventType());
       mLinkText.setText(mEvent.getLinkToGroup());
       mLocationText.setText(mEvent.getLocation());
       mDescriptionText.setText(mEvent.getLongDescription());  //  <- is full of CSS and HTML codes
       					// mDescriptionText.setText("substitute for LongDescription");
       }
            Log.v(TAG, "in refreshView: ");
            Log.v(TAG, " text: " +mTitleText.getText());   // (TextView)
//            Log.v(TAG, " star: " +mStartText.getText());
//            Log.v(TAG, " end : " +mEndText.getText());
//            Log.v(TAG, " type: " +mTypeText.getText());
//            Log.v(TAG, " link: " +mLinkText.getText());
//            Log.v(TAG, " org : " +mOrganizerText.getText());
//            Log.v(TAG, " loc : " +mLocationText.getText());
//            Log.v(TAG, " desc: " +mDescriptionText.getText());

/*            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {	// Intents might not work earlier
                final EventDetailFragment outerThis = this;
                m_addToCalendar.setOnClickListener(
                            new View.OnClickListener() {
                            public void onClick(View v) {
                                Log.i("EventListDisp.Fragment", "want to make an appointment for '" + mEvent.getTitle() + "' " + mEvent.getStartDate());
                                    outerThis.makeAppointment(mEvent.getTitle(), mEvent.getLocation(), mEvent.getStartDate(), mEvent.getEndDate());
                                }
                            } });

                m_forward.setOnClickListener(
                            new View.OnClickListener() {
                            public void onClick(View v) {
                                Log.d("EventListDisp.Fragment", "forward button unimplemented");
                                MakeToast.makeToast(v.getContext(), "onCreate, start trace", MakeToast.LEVEL_DEBUG);
                            } });
            }   // finished with OnClickListeners
*/
 //       }   // end else clause (we have valid mId)

        Log.v(TAG, "exit refreshView");
   }    //  end --- refreshView()



    /** make calendar appointment. This should be a controller method.
     */
    void makeAppointment(String title, String location, Date start, Date end ) {
        	Intent intent =  new Intent(Intent.ACTION_INSERT, Events.CONTENT_URI);

        long startL, endL;
        if (null != start) {
            startL = start.getTime();
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startL);
        }
        if (null != end) {
            endL = end.getTime();
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endL);
        }

        intent.putExtra(Events.TITLE, title);
        intent.putExtra(Events.ALL_DAY, false);
        intent.putExtra(Events.EVENT_LOCATION, location);

        startActivity(intent);
    }

	public int get_isEventType() {
		return m_isListType;
	}

	public void set_isEventType(int m_isEventType) {
		this.m_isListType = m_isEventType;
	}

//   public static void updateList(){} // seen from 
    /*
     * setListAdapter
     * mListHeader
     * m_eventListItemCount
     */
/*
	public  void updateList() {		//public static void updateList()
		// xxxxxx
		Log.v(TAG, "update saved list");
		Cursor mCursor;
		SimpleCursorAdapter mAdapter;
		
		dbh = new DatabaseHelper(getActivity());
		mCursor.close();
		mCursor = dbh.getCursorSavedEvents();
		mAdapter.swapCursor(mCursor);
		setListAdapter(mAdapter);

        updateListHeader("Saved List");
	}

  

static  void updateListHeader( String extraText )  {
        // Create a list-header (TextView) and add it to the list like this:
        // mListHeader = (TextView) mLayoutInflater.inflate(R.layout.listheader, null);
        // mLV.addHeaderView(mListHeader);

        SimpleDateFormat simpFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US );
        Date m_channelDate = new Date(System.currentTimeMillis());
        String channelDate = m_channelDate == null? "--" : simpFormat.format(EB2MainActivity.m_channelDate);

        // this should be the current date or the date when data was saved into the database

        SavedSectionFragment.mListHeader.setText( extraText + "(" + channelDate + ")" +
        							SavedSectionFragment.mEventItemCount + " Events");
    }
*/

} // ------- class EventDetailFragment
// ====== EventDetailFragment
