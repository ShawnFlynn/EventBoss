package com.tssg.eventboss2;

import java.sql.Date;
import java.util.List;

import com.tssg.eventboss2.EB2MainActivity.event_list;
import com.tssg.eventsource.BELEvent;

import android.app.ActionBar;
import android.content.res.Resources;

// Interface for EB2MainActivity
public interface EB2Interface {

	boolean DEBUG();

	Resources getEB2Resources();

	int getFeedId();
	void setFeedId(int feedId);

	int getOldFeedId();
	public void setOldFeedId(int feedId);

	String getFeedName();

	void updateListHeader(int EventCount);

	Date getCurrentDate();

	CurrentSectionFragment getCurrentData();

	List<BELEvent> getCurrentEventsList();
	void setCurrentEventsList(List<BELEvent> webEventsList);

	List<BELEvent> getEventsListCache(int feedId);
	void setEventsListCache(int feedId, List<BELEvent> eventsList);
	int getEventsListCacheSize();
	boolean EventsListCacheIsEmpty(int feedId);

	event_list getLastList();

	ActionBar getEB2ActionBar();

	void setCurrentTabLabel(String tabString);

	String getTab0Label();
	void setTab0Label(String tabString);

	String getMainEventText();

	boolean ifReadingFromInternalFile();
	String getInternalFilePath();

	String getDBName();

}
