package com.algolia.firebase.connector.docManager;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.algolia.firebase.connector.App;
import com.algolia.search.saas.APIClient;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Index;
import com.firebase.client.DataSnapshot;
import com.google.gson.Gson;

public class Algolia extends DocManager {
	APIClient client;
	Index index;
	ArrayList<JSONObject> batch;
	
	public Algolia(String indexName) {
		String[] credentials = App.getDocManagerUrl().split(":");
		assert(credentials.length == 2);
		client = new APIClient(credentials[0], credentials[1]);
		index = client.initIndex(indexName);
		batch = new ArrayList<>();
	}
	
	private JSONObject convertToJSON(Object value) {
		try {
			return new JSONObject(new Gson().toJson(value));
		} catch (JSONException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private void addBatch(JSONObject body, String objectID, String action) {
		try {
			if (objectID != null) {
				body.put("objectID", objectID);
			}
			batch.add(new JSONObject().put("action", action).put("body", body));
		} catch (JSONException e) {
			commit();
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void add(DataSnapshot arg0) {
		addBatch(convertToJSON(arg0.getValue()), arg0.getName(), "addObject");
	}

	@Override
	public void update(DataSnapshot arg0) {
		addBatch(convertToJSON(arg0.getValue()), arg0.getName(), "updateObject");
	}

	@Override
	public void delete(DataSnapshot arg0) {
		addBatch(new JSONObject(), arg0.getName(), "deleteObject");
		
	}

	@Override
	public void commit() {
		try {
			index.batch(batch);
			batch.clear();
		} catch (AlgoliaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}
