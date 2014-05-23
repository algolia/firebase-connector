package com.algolia.firebase.connector;

import com.algolia.firebase.connector.docManager.Algolia;
import com.algolia.firebase.connector.docManager.DocManager;
import com.firebase.client.Firebase;

public class Connector {
	
	private Firebase[] tables;
	private DocManager[] listeners;
	
	public Connector() {
		String[] url = App.getMapping().split(",");
		String[] params = null;
		if (App.getDocManagerParam() != null) {
			params = App.getDocManagerParam().split(",");
		}
		tables = new Firebase[url.length];
		listeners = new DocManager[url.length];
		App.logger.info("Listen " + url.length + " values");
		for (int i = 0; i < url.length; ++i) {
			App.logger.info("Listen " + url[i]);
			tables[i] = new Firebase(App.getFirebaseURL() + "/" + url[i]);
			if (params != null) {
				listeners[i] = new Algolia(params[i]);//TODO
			} else {
				listeners[i] = new Algolia();//TODO
			}
		}
	}

	
	public void initialImport() {
		App.logger.info("Initial import");
		for (int i = 0; i < tables.length; ++i) {
			tables[i].addListenerForSingleValueEvent(new ListChild(listeners[i]));
		}
		App.logger.info("Initial import : Launched");
	}
	
	public void listen() {
		App.logger.info("Listen");
		for (int i = 0; i < tables.length; ++i) {
			tables[i].addChildEventListener(listeners[i]);//TODO
		}
		App.logger.info("Listen: Done");
	}
	
	public void commit() {
		App.logger.info("Commit");
		for (int i = 0; i < listeners.length; ++i) {
			listeners[i].commit();
		}
		App.logger.info("Commit: Done");
	}
}
