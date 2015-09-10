package com.zahdoo.android.extension.voiceinput;

public class ContactDetails {
	public String id;
	public String Name;
	public String phNumber[];
	public String[] phType;
	public String[] eMail;
	public String[] mType;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String phName) {
		this.Name = phName;
	}
	public String[] getPhNumber() {
		return phNumber;
	}
	public void setPhNumber(String[] ph) {
		this.phNumber = ph;
	}
	public String[] getPhType() {
		return phType;
	}
	public void setPhType(String[] phType2) {
		this.phType = phType2;
	}
	public String[] getEMail() {
		return eMail;
	}
	public void setEMail(String[] mail) {
		this.eMail = mail;
	}
	public String[] getEMailType() {
		return mType;
	}
	public void setEMailType(String[] mType) {
		this.mType = mType;
	}
	
	
	
	
}
