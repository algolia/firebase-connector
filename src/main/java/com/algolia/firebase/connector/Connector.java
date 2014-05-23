package com.algolia.firebase.connector;

import com.algolia.firebase.connector.docManager.Algolia;
import com.algolia.firebase.connector.docManager.DocManager;
import com.firebase.client.Firebase;

public class Connector {
	
	private Firebase[] tables;
	private DocManager[] listeners;
	
	public Connector() {
		String[] params = App.getMapping().split(",");
		tables = new Firebase[params.length];
		listeners = new DocManager[params.length];
		App.logger.info("Listen " + params.length + " values");
		for (int i = 0; i < params.length; ++i) {
			App.logger.info("Listen " + params[i]);
			tables[i] = new Firebase(App.getFirebaseURL() + "/" + params[i]);
			listeners[i] = new Algolia(params[i].replace("/", "_"));//TODO
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
