package com.d3.yiqi.listener;

import java.io.File;

import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import android.content.Intent;
import android.net.Uri;

import com.d3.yiqi.model.Msg;
import com.d3.yiqi.service.XmppApplication;
import com.d3.yiqi.service.XmppConnection;
import com.d3.yiqi.util.DateUtil;

//图片发送监听
public class ImgInterceptor {
	public static void sendFile(String user,File file) throws Exception{  
        FileTransferManager manager = new FileTransferManager(XmppConnection.getConnection());  
        OutgoingFileTransfer imgSend = manager.createOutgoingFileTransfer(user);  
        imgSend.sendFile(file,file.getName());
    	String friendUser = user;
		if (friendUser.contains("/")) {
			friendUser = friendUser.substring(0, friendUser.indexOf("/"));
		}
		// 记录我们发出去的消息
		Msg nowMsg = new Msg(XmppApplication.user, file.getPath(),
				DateUtil.now_MM_dd_HH_mm_ss(), "OUT");
		XmppApplication.AllFriendsMessageMapData.get(friendUser).MessageList.add(nowMsg);
		XmppApplication.dbHelper.saveChatMsg(nowMsg,friendUser);
		
		//发送广播
		Intent messageIntent = new Intent(
				XmppApplication.XMPP_UP_MESSAGE_ACTION);

		messageIntent.setData(Uri.parse("xmpp://"
				+ friendUser.substring(0, friendUser.indexOf("@"))));

		XmppApplication.xmppApplication.sendBroadcast(messageIntent);
		
	} 
}
