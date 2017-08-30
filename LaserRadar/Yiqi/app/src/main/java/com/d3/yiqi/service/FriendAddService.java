package com.d3.yiqi.service;

import java.util.Iterator;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.util.Log;

public class FriendAddService {
	
	public String searchFriend(String search_text,String queryResult){
		try {
			XMPPConnection connection = XmppConnection.getConnection();
			UserSearchManager search = new UserSearchManager(connection);
			// 此处一定要加上 search.
			Form searchForm = search.getSearchForm("search."+ connection.getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", search_text.toString().trim());
			ReportedData data = search.getSearchResults(answerForm,
					"search." + connection.getServiceName());
			Iterator<Row> it = data.getRows();
			Row row = null;
			while (it.hasNext()) {
				row = it.next();
				queryResult = row.getValues("Username").next().toString();
			}
		} catch (Exception e) {
			Log.e("searchFriend", e.getMessage() + " " + e.getClass().toString());
		}
		return queryResult;
	}
}
