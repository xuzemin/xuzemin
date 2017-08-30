package com.d3.yiqi.listener;

import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.d3.yiqi.model.Msg;
import com.d3.yiqi.model.OneFridenMessages;
import com.d3.yiqi.service.XmppApplication;
import com.d3.yiqi.util.DateUtil;

public class MessageInterceptor implements PacketInterceptor {

	OneFridenMessages mOneFridenMessages;

	@Override
	public void interceptPacket(Packet packet) {
		Message nowMessage = (Message) packet;
		Log.i("send",nowMessage.getFrom()+"  to"+nowMessage.getTo()+"   :  "+nowMessage.getBody());
		
		String friendUser = nowMessage.getTo();
		if (friendUser.contains("/")) {
			friendUser = friendUser.substring(0, friendUser.indexOf("/"));
		}

		mOneFridenMessages = XmppApplication.AllFriendsMessageMapData
				.get(friendUser);

		// 我们发出去的Message
		if (mOneFridenMessages == null) {
			// 创建一个好友的OneFriendMessage
			mOneFridenMessages = new OneFridenMessages();
			XmppApplication.AllFriendsMessageMapData.put(friendUser,
					mOneFridenMessages);
		}
		// 记录我们发出去的消息
		Msg nowMsg = new Msg(nowMessage.getFrom().substring(0,
				friendUser.indexOf("@")), nowMessage.getBody(),
				DateUtil.now_MM_dd_HH_mm_ss(), "OUT");
		mOneFridenMessages.MessageList.add(nowMsg);
		XmppApplication.dbHelper.saveChatMsg(nowMsg,friendUser);

		Intent messageIntent = new Intent(
				XmppApplication.XMPP_UP_MESSAGE_ACTION);
		messageIntent.setData(Uri.parse("xmpp://"
				+ friendUser.substring(0, friendUser.indexOf("@"))));
		XmppApplication.xmppApplication.sendBroadcast(messageIntent);
	}

}
