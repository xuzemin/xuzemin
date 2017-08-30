package com.d3.yiqi.listener;

import java.io.IOException;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.d3.yiqi.R;
import com.d3.yiqi.model.Msg;
import com.d3.yiqi.model.OneFridenMessages;
import com.d3.yiqi.service.XmppApplication;
import com.d3.yiqi.service.XmppConnection;
import com.d3.yiqi.util.DateUtil;
import com.d3.yiqi.util.NotiUtil;

public class MessageListener implements PacketListener {

	OneFridenMessages mOneFridenMessages;

	@Override
	public void processPacket(Packet packet) {
		// 在这里处理所有我们收到的消息
		Message nowMessage = (Message) packet;
		Log.i("come",nowMessage.getFrom()+"  to"+nowMessage.getTo()+"   :  "+nowMessage.getBody());
		//系统消息
		if(nowMessage.getFrom().equals(XmppConnection.SERVER_NAME)){
		NotiUtil.setNotiType(XmppApplication.xmppApplication.getApplicationContext(),
				R.drawable.default_head, nowMessage.getFrom()+":  "+nowMessage.getBody());
		}	
		
		String friendUser = nowMessage.getFrom();
		if (friendUser.contains("/")) {
			friendUser = friendUser.substring(0, friendUser.indexOf("/"));
		}
		//获取和此好友的对话信息
		mOneFridenMessages = XmppApplication.AllFriendsMessageMapData
				.get(friendUser);

		if (mOneFridenMessages == null) {
			mOneFridenMessages = new OneFridenMessages();
			XmppApplication.AllFriendsMessageMapData.put(friendUser,
					mOneFridenMessages);
		}
		//加入
		Msg nowMsg = new Msg(nowMessage.getFrom().substring(0,
				friendUser.indexOf("@")), nowMessage.getBody(),
				DateUtil.now_MM_dd_HH_mm_ss(), "IN");
		mOneFridenMessages.MessageList.add(nowMsg);
		//保存到sql
		XmppApplication.dbHelper.saveChatMsg(nowMsg,friendUser);
		mOneFridenMessages.NewMessageCount++;
		//保存到本地share，以免非正常关闭程序丢失新消息条数
		XmppApplication.sharedPreferences.edit().putInt(friendUser+XmppApplication.user, 
				mOneFridenMessages.NewMessageCount).commit();
		//发送广播
		Intent messageIntent = new Intent(
				XmppApplication.XMPP_UP_MESSAGE_ACTION);

		messageIntent.setData(Uri.parse("xmpp://"
				+ friendUser.substring(0, friendUser.indexOf("@"))));

		XmppApplication.xmppApplication.sendBroadcast(messageIntent);
		XmppApplication.xmppApplication.sendBroadcast(new Intent(
				"newMsg"));
		
		// 播放声音
		MediaPlayer mPlayer  = MediaPlayer.create(XmppApplication.xmppApplication.getApplicationContext(),R.raw.msn );
		try {
			if (mPlayer != null) {
				mPlayer.stop();
			}
			mPlayer.prepare();
			mPlayer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
