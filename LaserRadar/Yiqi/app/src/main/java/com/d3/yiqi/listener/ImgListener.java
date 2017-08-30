package com.d3.yiqi.listener;

import java.io.File;
import java.io.IOException;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.d3.yiqi.R;
import com.d3.yiqi.model.Msg;
import com.d3.yiqi.service.XmppApplication;
import com.d3.yiqi.service.XmppConnection;
import com.d3.yiqi.util.DateUtil;
//图片接受监听
public class ImgListener implements FileTransferListener {
		private IncomingFileTransfer imgCome;
		private String friendUser;
		@Override
		public void fileTransferRequest(final FileTransferRequest request) {
			imgCome = request.accept();
			File file = new File(XmppConnection.SAVE_PATH + "/"
					+ request.getFileName());
			try {
				if (!file.exists()) {
					if (!file.isDirectory()) {
						new File(XmppConnection.SAVE_PATH).mkdirs();
						file.createNewFile();
					} else {
						file.createNewFile();
					}
				}
				imgCome.recieveFile(file);
				Log.i("done",String.valueOf(imgCome.isDone()));
				
				
				friendUser = request.getRequestor();
				if (friendUser.contains("/")) {
					friendUser = friendUser.substring(0, friendUser.indexOf("/"));
				}
				//加入
				Msg nowMsg = new Msg(friendUser.substring(0,
						friendUser.indexOf("@")),  file.getPath(),
						DateUtil.now_MM_dd_HH_mm_ss(), "IN");
				XmppApplication.AllFriendsMessageMapData.get(friendUser).MessageList.add(nowMsg);
				//保存到sql
				XmppApplication.dbHelper.saveChatMsg(nowMsg,friendUser);
				XmppApplication.AllFriendsMessageMapData.get(friendUser).NewMessageCount++;
				//保存到本地share，以免非正常关闭程序丢失新消息条数
				XmppApplication.sharedPreferences.edit().putInt(friendUser+XmppApplication.user, 
						XmppApplication.AllFriendsMessageMapData.get(friendUser).NewMessageCount).commit();
				//发送广播
				Intent messageIntent = new Intent(
						XmppApplication.XMPP_UP_MESSAGE_ACTION);

				messageIntent.setData(Uri.parse("xmpp://"
						+ friendUser.substring(0, friendUser.indexOf("@"))));

				XmppApplication.xmppApplication.sendBroadcast(messageIntent);
				XmppApplication.xmppApplication.sendBroadcast(new Intent("newMsg"));
				
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
				new Thread(new Runnable() {
					boolean img_finish = false;
					@Override
					public void run() {
						while (!img_finish) {
							if(imgCome.isDone()==true){
			            		   Intent messageIntent = new Intent(
			       						XmppApplication.XMPP_UP_MESSAGE_ACTION);

			       					messageIntent.setData(Uri.parse("xmpp://"
			       						+ friendUser.substring(0, friendUser.indexOf("@"))));
			       					XmppApplication.xmppApplication.sendBroadcast(messageIntent);
			       					img_finish = true;
			            	   }
			               }
					}
				}).start();
				
				
			} catch (XMPPException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
}
