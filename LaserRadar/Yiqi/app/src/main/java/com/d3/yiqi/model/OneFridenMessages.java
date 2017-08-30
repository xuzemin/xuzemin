package com.d3.yiqi.model;

import java.util.ArrayList;
import java.util.List;


public class OneFridenMessages {

	//保存某个好友的当前会话消息：包括发出和收到两种
	public List<Msg> MessageList = new ArrayList<Msg>();
	
	//保存好友的基本信息
//	public RosterEntry FriendEntry;
	
	//保存好友发来的新消息(未读)数量
	public int NewMessageCount = 0;
	
}
