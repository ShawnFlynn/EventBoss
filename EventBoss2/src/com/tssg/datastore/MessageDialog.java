
package com.tssg.datastore;

import com.tssg.eventboss2.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;


/*
 * Pops up a informational message with an OK button to close it.
 * Uses an AlertDialog and AlertDialogBuilder to create it.  Does
 * not depend on an Activitiy's onCreateDialog method so it can be
 * used anywhere without having to integrate it into a particular
 * activity.
 */
public class MessageDialog
{
	static final long DEFAULT_DURATION = 3000;

	AlertDialog m_alert = null;

	public void dismiss()
	{
		if ( m_alert != null )
		{
			m_alert.dismiss();
		}
	}


    public void show( Activity activity, String title, String message )
    {
		show( activity, title, message, DEFAULT_DURATION );
	}


    public void show( Activity activity, String title,
			String message, long duration )
    {
		AlertDialog m_alert = new AlertDialog.Builder( activity )
                .setIcon( R.drawable.event_boss_icon )
                .setTitle( title )
                .setMessage( message )
                .setPositiveButton( "OK",
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{
								// Do nothing
							}
						})
                .create();

		m_alert.setOwnerActivity( activity );

		m_alert.show();
    }
}
