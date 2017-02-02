package com.tssg.datastore;


/** This class is just a PLACEHOLDER; even the name
 * may not be what we want in the end.  It should be replaced
 * with the main class of this package -- that is, with
 * the class the clients/users of the database with
 * interact with.  It is the 'public face' of the database.
 * note: this class must provide enough data for a mediator
 * to construct a BELEvent object.
 * 
 * @author Kieu Hua
 */

public class BELStoredEvent 
{
	
	@SuppressWarnings("unused")
    private static final String TAG = "BELStoredEvent";
	
	 String eType = null;
	 String eTitle = null;
	 String eDate = null;
	 String eLink = null;
	 String eLocation = null;
	 String eDescription = null;

	public BELStoredEvent() {;};

	public BELStoredEvent(String etype,
				String etitle,
				String edate,
				String elink,
				String elocation,
				String edescription)
	{
		this.eType = etype;
		this.eTitle = etitle;
		this.eDate = edate;
		this.eLink = elink;
		this.eLocation = elocation;
		this.eDescription = edescription;
	}


	// getters for BELStoredEvent object.
	public String geteType(){
		return eType;
	}
	public String geteTitle(){
		return eTitle;
	}
	public String geteDate(){
		return eDate;
	}
	public String geteLink(){
		return eLink;
	}
	public String geteLocation(){
		return eLocation;
	}
	public String geteDescription(){
		return eDescription;
	}
	
	// setters for BELStoredEvent object.
	public void seteType(String etype){
		this.eType = etype;
	}
	public void seteTitle(String etitle){
		this.eTitle = etitle;
	}
	public void seteDate(String edate){
		this.eDate = edate;
	}
	public void seteLink(String elink){
		this.eLink = elink;
	}
	public void seteLocation(String elocation){
		this.eLocation = elocation;
	}
	public void seteDescription(String edescription) {
		this.eDescription = edescription;
	}

}
