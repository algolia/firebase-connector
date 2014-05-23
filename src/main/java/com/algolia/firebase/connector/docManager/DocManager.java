package com.algolia.firebase.connector.docManager;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;

public abstract class DocManager implements ChildEventListener {

	@Override
	public void onCancelled() {
		commit();
	}
	public void onChildAdded(DataSnapshot arg0, String arg1) {
		add(arg0);
	}
	public void onChildChanged(DataSnapshot arg0, String arg1) {
		update(arg0);
	}
	public void onChildMoved(DataSnapshot arg0, String arg1) {
		update(arg0);
	}
	public void onChildRemoved(DataSnapshot arg0) {
		delete(arg0);
	}
	
	public abstract void add(DataSnapshot arg0);
	public abstract void update(DataSnapshot arg0);
	public abstract void delete(DataSnapshot arg0);
	public abstract void commit();
}

