/**
 * 
 */
package com.tssg.find;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tssg.eventboss2.Constants;
import com.tssg.eventboss2.MakeToast;
import com.tssg.eventboss2.R;
//import android.app.Application;

/**
 * @author rjd
 * 
 *         This activity collects criteria for use in the Find functionality of
 *         Event Boss.
 * 
 */
//public class FilterCriteria extends Activity implements OnTouchListener {
public class FilterCriteria extends Activity {

	public static final String TAG = "FilterCriteria";

	/*
	 * Inner class to hold Find key and Find value These represent user inputs,
	 * and will be used to matched against events.
	 */
	private static class KeyValue {

		static String findKey = "";
		static String findValue = "";
		static long dateValue = 0;

		static void setFindKey(String key) {
			findKey = key;
		}

		static void setFindValue(String val) {
			findValue = val;
		}

		static void setDateValue(long date) {
			dateValue = date;
		}

		static long getDateValue() {
			return dateValue;
		}

		static String getFindKey() {
			return findKey;
		}

		static String getFindValue() {
			return findValue;
		}

	} // end KeyValue

	// logging tag
	// static final int DATE_DIALOG_ID = 0;

	// private DatePicker mDatePicker;
	// private Button mSetDate;
	private Calendar criteriaDate = null;
	DatePickerDialog datePicker= null;

	Calendar c = null;
	private int cYear;
	private int cMonth;
	private int cDay;
	private long lDateValue; 

	private static LayoutInflater mLayoutInflater = null;
	private ListView lv = null;

	private ArrayList<String> types = null;

	private final int EVENT_TYPE = 0;
	private final int EVENT_DATE = 1;
	private final int EVENT_TITLE = 2;
	private final int EVENT_SHORT_DESC = 3;
	private final int EVENT_LOCATION = 4;
	private final int EVENT_ORGANIZATION = 5;
	private final int EVENT_UNKNOWN = 0x01824;

	int event_key_code = EVENT_UNKNOWN;

	/* Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

Log.e(TAG, "in: " + "FilterCriteria");
MakeToast.makeToast(this, "in: " + "*** FilterCriteria ***", MakeToast.LEVEL_USER);
		
		this.setContentView(R.layout.filter_criteria);
		// Set up the list view
		// this.lv = (ListView) findViewById(R.id.criteriakey_list);

		types = getIntent().getStringArrayListExtra("TypesKey");

		// inflate the entire view
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		mLayoutInflater = LayoutInflater.from(this.getBaseContext());
		View layout = mLayoutInflater.inflate(R.layout.filter_criteria, null);

		View actionbar = layout.findViewById(R.id.find_banner);
		actionbar.setBackgroundResource(R.color.darkblue);

		this.lv = (ListView) layout.findViewById(R.id.list);

		final String[] keys = getResources().getStringArray(
				R.array.criteriakeys);
		lv.setAdapter(new ArrayAdapter<String>(this, R.layout.row, keys));
		lv.setTextFilterEnabled(true); // RD: why filter?
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//		lv.setFocusable(false);
		lv.setItemsCanFocus(false);
//		lv.setCacheColorHint(R.color.white);
		lv.setBackgroundResource(R.color.white);
		lv.setSelector(getResources().getDrawable(R.drawable.highlight));
		lv.setDivider(getResources().getDrawable(R.drawable.divider));
		lv.setDividerHeight(20);
		
		lv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				MakeToast.makeToast(getApplicationContext(), ((TextView) view).getText().toString(), MakeToast.LEVEL_DEBUG);	
				
				String choice = ((String) ((TextView) view).getText()).trim();
				// RD: Question: Should these strings below be replaced with
				// static final strings?
				if (choice.equalsIgnoreCase("Title")) {
					event_key_code = EVENT_TITLE;
					KeyValue.setFindKey(Constants.EVENT_TITLE);
				} else if (choice.equalsIgnoreCase("Location")) {
					event_key_code = EVENT_LOCATION;
					KeyValue.setFindKey(Constants.EVENT_LOC);
				} else if (choice.equalsIgnoreCase("Date")) {
					event_key_code = EVENT_DATE;
					KeyValue.setFindKey(Constants.EVENT_DATE);
				} else if (choice.equalsIgnoreCase("Type")) {
					event_key_code = EVENT_TYPE;
					KeyValue.setFindKey(Constants.EVENT_TYPE);
				} else if (choice.equalsIgnoreCase("Organizer")) {
					event_key_code = EVENT_ORGANIZATION;
					KeyValue.setFindKey(Constants.EVENT_ORG);
				} else if (choice.equalsIgnoreCase("Description")) {
					event_key_code = EVENT_SHORT_DESC;
					KeyValue.setFindKey(Constants.EVENT_DESC);
				}

				else
					event_key_code = EVENT_UNKNOWN;

				promptValueUI(event_key_code);

			}
		});

		this.setContentView(layout);
	}	// ----- end onCreate  -----

	private void promptValueUI(int code) {

		String title = ""; 
		String msg = ""; 
		AlertDialog valDia = null;
		
		
		switch (code) {
		case EVENT_TYPE:
			// Display a list of the existing types in a new activity.
			Intent type_intent = new Intent(this, EventTypeList.class);
			// Pass the array of event types to the next activity
			type_intent.putStringArrayListExtra("TypeValues", types);
			startActivityForResult(type_intent, Constants.FIND_TYPE_REQ);
			break;

		case EVENT_DATE:
			title = "Date";
			msg = "Select earliest date of interest:"; // 

			//get the current date
	        c = Calendar.getInstance();
	        cYear = c.get(Calendar.YEAR);
	        cMonth = c.get(Calendar.MONTH);
	        cDay = c.get(Calendar.DATE);

			datePicker = 
				new DatePickerDialog(this,  mDateSetListener,  cYear, cMonth, cDay);

			datePicker.setMessage(msg);
			datePicker.show();
			break;
 
		case EVENT_TITLE:
			
			title = "Title Search";
			msg = "Enter Search String";
			valDia = genValueDialogBox(title, msg);
			valDia.show();
			break;
			
		case EVENT_SHORT_DESC:
			
			title = "Description";
			msg = "Enter Search String";
			valDia = genValueDialogBox(title, msg);
			valDia.show();
			break;
			
		case EVENT_LOCATION:

			title = "Location";
			msg = "Enter Search String";
			valDia = genValueDialogBox(title, msg);
			valDia.show();
			break;
			
		case EVENT_ORGANIZATION:
			
			title = "Organization";
			msg = "Enter Search String";
			valDia = genValueDialogBox(title, msg);
			valDia.show();
			break;
		
//			Intent dia_intent = new Intent(this, FindValueDialog.class);
//			dia_intent.putExtra("CriteriaName", code);
//			startActivityForResult(dia_intent, Constants.FIND_TEXT_REQ);
//			break;

		case EVENT_UNKNOWN:
			break;
		}
	}

	
	private AlertDialog genValueDialogBox(String title, String message){
		// Set an EditText view to get user input 
		AlertDialog alert = null;
		final EditText input = new EditText(this);
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setTitle(title)
		.setMessage(message)
		.setCancelable(false)
		.setView(input)
		.setIcon(R.drawable.ic_search)	// OLD ICON FORMAT
		.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
			  Editable value = input.getText();
			  KeyValue.setFindValue(value.toString().trim());
				forwardKeyValue();
		}
		});

		alt_bld.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  KeyValue.setFindValue("");
			  KeyValue.setFindKey(Constants.EVENT_UNKNOWN);
		  dialog.cancel();
		  }
		});
		
		alert = alt_bld.create();
		return alert;
		
	}
	
	
	protected DatePickerDialog.OnDateSetListener mDateSetListener = 
		new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			cYear = year;
			cMonth = monthOfYear;
			cDay = dayOfMonth;
			displayToast();
			c.set(cYear, cMonth, cDay);
			lDateValue = c.getTimeInMillis();
			KeyValue.setDateValue(lDateValue);
			forwardKeyValue();
			
		}
	};
	
	
	/*
	 * Process Key and Value (or Date)
	 */
	private void forwardKeyValue() {
		String str = "forwardKeyValue: "+KeyValue.getFindKey()+", "+KeyValue.getFindValue();
		Log.e("Filter Criteria: ", str);

		// Pass the selected criteria back to the calling activity.
		Intent retIntent = new Intent();
//		Intent retIntent = new Intent(this, SearchDlgActivity.class);

		retIntent.putExtra(Constants.FIND_KEY, KeyValue.getFindKey());
		retIntent.putExtra(Constants.FIND_VALUE, KeyValue.getFindValue());
		retIntent.putExtra("long_date", KeyValue.getDateValue());
		setResult(RESULT_OK, retIntent);

		str = "startActivityForResult(intent,Constants.FIND_REQ)";
		Log.e("in FilterCriteria: ", str);

		finish();
	}

	/*
	 * This method retrieves criteria from an upstream activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		String str = "req"+requestCode+"res"+resultCode;
		Log.e("Filter Criteria - onActivityResult: ", str);

		String findValue = null;
		// Values returned from the text input dialog box.
		if (requestCode == Constants.FIND_TEXT_REQ) {
			if (resultCode == RESULT_OK) {
				findValue = data.getStringExtra(Constants.FIND_VALUE);
				KeyValue.setFindValue(findValue);
				MakeToast.makeToast(getApplicationContext(), "FC; findValue: " + findValue, MakeToast.LEVEL_DEBUG);			
			}
		}
		
		// Values returned from the "Type" selection list.
		if (requestCode == Constants.FIND_TYPE_REQ) {
			if (resultCode == RESULT_OK) {
				findValue = data.getStringExtra(Constants.FIND_VALUE);
				KeyValue.setFindValue(findValue);
				MakeToast.makeToast(getApplicationContext(), "FC; findValue: " + findValue, MakeToast.LEVEL_DEBUG);	
			} else {  // user made no selection from event type
				setResult(RESULT_CANCELED);
				finish();	
			}
		}

		// Pass the result back down the line.
		Intent retIntent = new Intent();
		retIntent.putExtra(Constants.FIND_KEY, KeyValue.getFindKey());
		retIntent.putExtra(Constants.FIND_VALUE, KeyValue.getFindValue());
		retIntent.putExtra("long_date", KeyValue.getDateValue());

		setResult(RESULT_OK, retIntent);
		finish();
	}	// end onActivityResult

	
	/*
	 * public enum FindKeyEnum { TYPE("type"), TITLE("title"),
	 * ORGANIZER("organizer"), LOCATION( "location"),
	 * DESCRIPTION("description"), LONGDESCRIPTION( "longDescription");
	 */
	static final String KEY_TITLE = "title";
	static final String KEY_STARTTIME = "startTime";
	static final String KEY_ENDTIME = "endTime";
	static final String KEY_TYPE = "type";
	static final String KEY_LINK = "link";
	static final String KEY_ORGANIZER = "organizer";
	static final String KEY_LOCATION = "location";
	static final String KEY_DESCRIPTION = "description";
	static final String KEY_LONGDESCRIPTION = "longDescription";




	protected void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		cYear = year;
		cMonth = monthOfYear;
		cDay = dayOfMonth;
		displayToast();
		setResultDate(year, monthOfYear, dayOfMonth);

	}

	private void setResultDate(int year, int monthOfYear, int dayOfMonth) {

		criteriaDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
	}

//	private String getDateString(Calendar cal) {
//
//		SimpleDateFormat sdf = new SimpleDateFormat("DATE_FORMAT");
//
//		Date date = cal.getTime();
//		String dateStr = sdf.format(date);
//		return dateStr;
//	}

	// updates the date in a toast.
	private void displayToast() {

		String display = new String(new StringBuilder()
				// Month is 0 based so add 1
				.append(cMonth + 1).append("-").append(cDay).append("-")
				.append(cYear).append(" "));

		MakeToast.makeToast(getApplicationContext(), display, MakeToast.LEVEL_DEBUG);
	}

//	private void handleGetResults() {
//		if (!validate()) {
//			return;
//		}
//
//		// Use Application Object for state (global values)
//		// Application application = (Application) this.getApplication();
//	}

//	private boolean validate() {
//		boolean valid = true;
//		// TODO this function needs fleshing out.
//		return valid;
//
//	}

	//@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}	//  end FilterCriteria