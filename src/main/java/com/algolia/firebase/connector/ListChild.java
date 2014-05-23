package com.algolia.firebase.connector;

import com.algolia.firebase.connector.docManager.DocManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.ValueEventListener;

public class ListChild implements ValueEventListener {
	
	private DocManager docManager;
	
	public ListChild(DocManager docManager) {
		this.docManager = docManager;
	}

	@Override
	public void onCancelled() {
		// Nothing
	}

	@Override
	public void onDataChange(DataSnapshot arg0) {
		for (DataSnapshot arg : arg0.getChildren()) {
			docManager.add(arg);
		}
	}
}
