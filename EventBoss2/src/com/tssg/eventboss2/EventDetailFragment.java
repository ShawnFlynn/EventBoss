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

    final public static String EVENTITEM_POS = "position"; // compiler suggested taking off 'static'
    public static final String SAVED_KEY = "isSaved";

//    private DatabaseHelper dbh = new DatabaseHelper(getActivity());
    private DatabaseHelper dbh;		// = new DatabaseHelper(getActivity()) EBMainActivity;
    private String mId;             // ItemId  or position ?

    private BELEvent mEvent;
    private TextView mTitleText;
    private TextView mStartText;	// start time
    private TextView mEndText;		// end time
    private TextView mTypeText;
    private TextView mLinkText;
    private TextView mOrganizerText;
    private TextView mLocationText;
    private TextView mDescriptionText;
    private boolean m_isSavedEvent;
 //   private Button m_forward = null;
 //   private Button m_addToCalendar;

    public EventDetailFragment() {
		m_isSavedEvent = false;
	}
    
    public void setId(String id) {
        mId = id;
    }

    public void setDBhelper(DatabaseHelper db) {
        dbh = db;
    }


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        if (null != savedInstance && null != savedInstance.get(SAVED_KEY)
                    && Boolean.parseBoolean((String)savedInstance.get(SAVED_KEY))) {
            m_isSavedEvent = true;
        }

        // Check for event ID
        if (getArguments().containsKey(EVENTITEM_POS)) {
            mId = getArguments().getString(EVENTITEM_POS);
        } else {
            return;
        }

        // Get a database handle
        // for tablet dbh is set when 'displayEventDetails' creates an EventDetailFragment
        // for phone  dhh isset here, because 'EventDetailActivity' creates the EventDetailFragment  
        if( dbh == null )  {
        	dbh = new DatabaseHelper(getActivity());
        }

        Log.v("EventDetailFragment:", "onCreate: db: "+dbh);
        Log.v("EventDetailFragment:", "mId:    "+mId);          //eventItemPos ?

        if( mId != null ) {	
        	// Get the event
        	mEvent = dbh.getEventById(mId);
        	Log.v("EventDetailFragment:", "mEvent: "+mEvent);
		}
    }   // end --- onCreate()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle SavedInstanceState) {

        Log.v("EventDetailFragment:", " inflate: -> fragment_event_detail, container:"+container);
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        Log.v("EventDetailFragment:", " view: " + view);

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

    public void onStart() {
        super.onStart();
       Log.i("EventDetailFrag onStart","call refresh view");
       refreshView();
        // EGL_emulation﹕ eglSurfaceAttrib not implemented
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


   public void refreshView() {

         // This will reload the data in the view
         if ((mId != null) && (!mId.equals(""))) {
            if (!m_isSavedEvent){
                mEvent = dbh.getEventById(mId);
            }

            Log.i("Refresh view","mEvent: "+mEvent);

            mTitleText.setText(mEvent.getTitle());
            mStartText.setText(mEvent.getStartTime());
            mEndText.setText(mEvent.getEndTime());
            mTypeText.setText(mEvent.getEventType());
            mLinkText.setText(mEvent.getLinkToGroup());
            mLocationText.setText(mEvent.getLocation());
            mDescriptionText.setText(mEvent.getLongDescription());  //  <- is full of
//            mDescriptionText.setText("substitute for LongDescription");

            Log.v("EventDetailFragment:", "in refreshView: ");
            Log.v("EventDetailFragment:", " text: " +mTitleText.getText());   // (TextView)
            Log.v("EventDetailFragment:", " star: " +mStartText.getText());
            Log.v("EventDetailFragment:", " end : " +mEndText.getText());
            Log.v("EventDetailFragment:", " type: " +mTypeText.getText());
            Log.v("EventDetailFragment:", " link: " +mLinkText.getText());
            Log.v("EventDetailFragment:", " org : " +mOrganizerText.getText());
            Log.v("EventDetailFragment:", " loc: " +mLocationText.getText());
            Log.v("EventDetailFragment:", " desc: " +mDescriptionText.getText());

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
        }   // end else clause (we have valid mId)

        Log.v("EventDetailFragment:", "exit refreshView");

        //java.lang.RuntimeException: Unable to instantiate activity ComponentInfo{
        //	com.tssg.eventboss2/com.tssg.eventboss2.EventDetailFragment}:
        //	java.lang.ClassCastException:
        //	com.tssg.eventboss2.EventDetailFragment cannot be cast to android.app.Activity
        //at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2121)

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
