package com.tssg.eventboss2;


import java.util.Date;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
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
public class EventDetailFragment extends Fragment {

    public static final String TAG = "EventDetailFragment";   // log's tag

    final public static String EVENTITEM_POS = "position"; // compiler suggested taking off 'static'
    public static final String SAVED_KEY = "isSaved";
    public static final String DB_HELPER = "DBHelper";


    private boolean m_isSavedEvent; // Current (false)  or  Saved List (true)
    private DatabaseHelper dbh;		// = new DatabaseHelper(getActivity()) EBMainActivity;
    private String mId;             // use ItemId

    private BELEvent mEvent;        //
    private TextView mTitleText;
    private TextView mStartText;	// start time
    private TextView mEndText;		// end time
    private TextView mTypeText;
    private TextView mLinkText;
    private TextView mOrganizerText;
    private TextView mLocationText;
    private TextView mDescriptionText;
 //   private Button m_forward = null;
 //   private Button m_addToCalendar;

 //   public EventDetailFragment() { m_isSavedEvent = false; } // reset in onCreate
    public void setListType(boolean listType ) {
        m_isSavedEvent = listType;
        Log.e(TAG, "onCreate: is ListType = "+listType);
        Log.e(TAG, "onCreate: isSavedEvent = "+m_isSavedEvent);
    }

    public void setEventId(String id) {  mId = id;  }
    public void setDBhelper(DatabaseHelper db) { dbh = db; }         // from  displayEventDetails

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Log.e(TAG, "onCreate: isSavedEvent = "+m_isSavedEvent+" w/id: "+mId);

        // Get a database handle
        // for tablet dbh is set when 'displayEventDetails' creates an EventDetailFragment
        // for phone  dhh isset here, because 'EventDetailActivity' creates the EventDetailFragment  
        if( dbh == null )  {
        	dbh = new DatabaseHelper(getActivity());
        }
        Log.v(TAG, " initialization EventDetailFragment:");
        Log.v(TAG, "onCreate: type: "+m_isSavedEvent);
        Log.v(TAG, "onCreate:  mId: "+mId);
        Log.v(TAG, "onCreate:   db: "+dbh);


        if( !m_isSavedEvent ) {    //
        	// Get the current event
            Log.v(TAG, "1/current Event: "+mEvent+", id = "+mId);
        	try {mEvent = dbh.getEventById(mId);}
            catch (android.database.CursorIndexOutOfBoundsException exept ) {
                if (mEvent == null) {
                    Log.e(TAG, "we have an id, but no Event: " + mEvent);
                    Log.e(TAG, "this happens when 'm_isSaved' should befalse but is true ");
                    Log.e(TAG, "see event EventDetailAactivity near linr 56");
                    return;
                }
                ;
            }
             Log.v(TAG, "2/current Event: "+mEvent);
		}
        else {
            // Get the saved event
            Log.v(TAG, "3/saved Event: "+mEvent);
            mEvent = dbh.getSavedEventById(mId);
            Log.v(TAG, "4/saved Event: "+mEvent);
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
//        m_forward = (Button)view.findViewById(R.id.forward);
//        m_addToCalendar = (Button)view.findViewById(R.id.addtocalendar);

        Log.v("EventDetailFragment:", " text: " +mTitleText);   // (TextView)
        Log.v("EventDetailFragment:", " star: " +mStartText);
        Log.v("EventDetailFragment:", " end: " +mEndText);
        Log.v("EventDetailFragment:", " type: " +mTypeText);
        Log.v("EventDetailFragment:", " link: " +mLinkText);
        Log.v("EventDetailFragment:", " org : " +mOrganizerText);
        Log.v("EventDetailFragment:", " loc: " +mLocationText);
        Log.v("EventDetailFragment:", " desc: " +mDescriptionText);

        Log.v("EventDetailFragment:","exit");
        return view;

    }   //  end --- onCreateView()

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "-> refreshView");
        refreshView();
        // note EGL_Emulation  .. eglSurfaceAttribute not Implemented
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


   public void refreshView() {
       if(mEvent==null ) {
           mTitleText.setText("have no Event");
           return;
       }
    /*
         // This will reload the data in the view
         if ((mId != null) && (!mId.equals(""))) {
            if (!m_isSavedEvent){
                mEvent = dbh.getEventById(mId);
                Log.i("TAG","Refresh current view: mEvent= "+mEvent);
            } else {
                mEvent = dbh.getSavedEventById(mId);
                Log.i("TAG","Refresh saved view: mEvent= "+mEvent);
            }
    */
            Log.i("TAG","Refresh view: mEvent= "+mEvent);

            mTitleText.setText(mEvent.getTitle());
            mStartText.setText(mEvent.getStartTime());
            mEndText.setText(mEvent.getEndTime());
            mTypeText.setText(mEvent.getEventType());
            mLinkText.setText(mEvent.getLinkToGroup());
            mLocationText.setText(mEvent.getLocation());
            mDescriptionText.setText(mEvent.getLongDescription());  //  <- is full of
//            mDescriptionText.setText("substitute for LongDescription");

            Log.v("TAG", "in refreshView: ");
            Log.v("TAG", " text: " +mTitleText.getText());   // (TextView)
            Log.v("TAG", " star: " +mStartText.getText());
            Log.v("TAG", " end : " +mEndText.getText());
            Log.v("TAG", " type: " +mTypeText.getText());
            Log.v("TAG", " link: " +mLinkText.getText());
            Log.v("TAG", " org : " +mOrganizerText.getText());
            Log.v("TAG", " loc:  " +mLocationText.getText());
            Log.v("TAG", " desc: " +mDescriptionText.getText());

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


} // ------- class EventDetailFragment
// ====== EventDetailFragment
