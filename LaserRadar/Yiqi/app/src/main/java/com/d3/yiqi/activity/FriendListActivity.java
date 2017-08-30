package com.d3.yiqi.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.d3.yiqi.R;
import com.d3.yiqi.adapter.FriendListAdapter;
import com.d3.yiqi.model.FriendInfo;
import com.d3.yiqi.model.GroupInfo;
import com.d3.yiqi.service.XmppApplication;
import com.d3.yiqi.service.XmppConnection;
import com.d3.yiqi.service.XmppService;
import com.d3.yiqi.util.TransUtils;
/**
 * 好友列表
 */
@SuppressWarnings("all")
public class FriendListActivity extends Activity implements OnItemClickListener,OnItemLongClickListener{
	private String pUSERID;//当前用户
	private String pGROUPNAME;//当前组
	private ListView listView;
	private List<FriendInfo> friendList;
	public static FriendListAdapter adapter;
	public static FriendListActivity friendListActivity;
	FriendInfo friendInfo;
	Roster roster = XmppConnection.getConnection().getRoster();
    XMPPConnection connection = XmppConnection.getConnection();
    private String fromUserJid = null;//发送邀请的用户的userJid，当有人要邀请时不为空
    private String toUserJid = null;//收到邀请的用户的userJid
	private TextView myStatusText = null;           //心情状态栏
    private String myMood = null;
    private String friendMood = null;
    private NewMsgReceiver newMsgReceiver;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		friendListActivity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_list);
//		NotiUtil.setNotiType(this,R.drawable.default_head, "登陆成功！");
		this.pUSERID = XmppApplication.user;
		//显示自己名字
		TextView friend_list_myName = (TextView)findViewById(R.id.friend_list_myName);
		myStatusText = (TextView)findViewById(R.id.myStatusText);
		friend_list_myName.setText(pUSERID);
		listView = (ListView) findViewById(R.id.contact_list_view);
		registerForContextMenu(listView);
		try {		//加载好友列表
			loadFriend();
		} catch (Exception e) {	
			Toast.makeText(this, "出错啦:"+e.getMessage(),0).show();
		}
		adapter = new FriendListAdapter(this,friendList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		//新消息监听
		newMsgReceiver = new NewMsgReceiver();
		registerReceiver(newMsgReceiver, new IntentFilter("newMsg"));
		
		roster.addRosterListener(new FriendListner());  

//        if(fromUserJid!=null){         //有人要申请添加
//    		AlertDialog.Builder dialog=new AlertDialog.Builder(FriendListActivity.this);
//			initDialog(dialog);
//    	}         
	}
	
	public void loadFriend() {
		try {
			friendList = new ArrayList<FriendInfo>();
				Collection<RosterEntry> entries = roster.getEntries();
				for (RosterEntry entry : entries) {
//					if("both".equals(entry.getType().name())){//只添加双边好友 
						friendInfo = new FriendInfo();
						friendInfo.setUsername(XmppService.getUsername(entry.getUser()));
						if(entry.getStatus() == null){
							friendMood ="Q我吧，静待你的来信！";
						}
						friendInfo.setMood(friendMood);
						friendList.add(friendInfo);
						friendInfo = null;			
//					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		FriendInfo info = friendList.get(position);
		Intent intent = new Intent(this,ChatActivity.class);
		intent.putExtra("FRIENDID", info.getJid());
		startActivity(intent);
	}
	/**
	 * 长按删除
	*/
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int postion,
			long arg3) {
		final FriendInfo friendInfo = friendList.get(postion);
		LayoutInflater layoutInflater= LayoutInflater.from(this);
        View delFriendView = layoutInflater.inflate(R.layout.dialog_del_friend, null);
        TextView delname = (TextView)delFriendView.findViewById(R.id.delname);
        delname.setText(friendInfo.getJid());
        final CheckBox delCheckBox = (CheckBox)delFriendView.findViewById(R.id.delCheckBox);
        Dialog dialog =new AlertDialog.Builder(this)
        .setIcon(R.drawable.default_head)
        .setTitle("删除好友")
        .setView(delFriendView)
        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
            	 XmppService.removeUser(roster, friendInfo.getJid());
				 if(delCheckBox.isChecked()){
					 XmppService.removeUser(roster, friendInfo.getJid());
				 }
				 Intent intent = new Intent();
      			 intent.setClass(FriendListActivity.this, FriendListActivity.class);
      			 startActivity(intent); 
            }
        })
        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                	dialog.cancel();
                }
        })
        .create();
        dialog.show();
		return true;
	}

	@Override
	protected void onResume() {
		adapter.notifyDataSetChanged();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		XmppConnection.closeConnection();
		Intent intent = new Intent(this, XmppService.class);
		stopService(intent);
		friendListActivity = null;
		unregisterReceiver(newMsgReceiver);
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(Menu.NONE, Menu.FIRST + 1, 1,"刷新列表").setIcon(R.drawable.menu_refresh);
		menu.add(Menu.NONE, Menu.FIRST + 2, 1,"更新心情").setIcon(R.mipmap.menu_setting);
		menu.add(Menu.NONE, Menu.FIRST + 3, 1,"添加好友").setIcon(R.mipmap.addfriends_icon_icon);
		menu.add(Menu.NONE, Menu.FIRST + 4, 1,"退出登录").setIcon(R.mipmap.menu_exit);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			loadFriend();
			Intent intent1 = new Intent();
			intent1.setClass(FriendListActivity.this, FriendListActivity.class);
			startActivity(intent1);
			break;
		case Menu.FIRST + 2:
			LayoutInflater layoutInflater= LayoutInflater.from(this);
            final View myMoodView = layoutInflater.inflate(R.layout.dialog_mood, null);           
            Dialog dialog =new AlertDialog.Builder(this)
            .setView(myMoodView)
            .setPositiveButton("更改", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                	myMood = ((EditText)myMoodView.findViewById(R.id.myMood)).getText().toString().trim();
                    XmppService.changeStateMessage(connection, myMood);
                    myStatusText.setText(myMood);
                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                	dialog.cancel();
                 }
            })
            .create();
            dialog.show();
			break;
		case Menu.FIRST + 3:
			Intent intent11 = new Intent();
			intent11.setClass(FriendListActivity.this, FriendAddActivity.class);
			startActivity(intent11);
			break;
		case Menu.FIRST + 4:
            Intent exits = new Intent(Intent.ACTION_MAIN);		
            exits.addCategory(Intent.CATEGORY_HOME);
            exits.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(exits);
            System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

/*	*//**
	 * 好友相加提示框
	*//*
	public void initDialog(AlertDialog.Builder dialog){
		dialog.setTitle("好友申请")
	      .setIcon(R.drawable.log)
	      .setMessage("【"+fromUserJid+"】向你发来好友申请，是否添加对方为好友?")
	      .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {	
              	     dialog.cancel();//取消弹出框
              	     //允许添加好友则回复消息，被邀请人应当也发送一个邀请请求。
              	     Presence subscription = new Presence(Presence.Type.subscribe);
                       subscription.setTo(fromUserJid);
                       XmppConnection.getConnection().sendPacket(subscription);
                       if(pGROUPNAME == null){
                      	 pGROUPNAME = "我的好友";
                       }
                       XmppService.addUser(roster, fromUserJid, XmppService.getUsername(fromUserJid));
                       Intent intent = new Intent();
                       intent.setClass(FriendListActivity.this, FriendListActivity.class);
                       startActivity(intent); 
                   }
                 })
	       .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int which) {			
	                	 XmppService.removeUser(roster, fromUserJid);
	                     dialog.cancel();//取消弹出框
	                 }
	               }).create().show();
	}
	*/
	
	class FriendListner implements  RosterListener{   
        @Override  
        //监听好友申请消息
        public void entriesAdded(Collection<String> invites) { 
        	for (Iterator iter = invites.iterator(); iter.hasNext();) {
        		  String fromUserJids = (String)iter.next();
        		  fromUserJid = fromUserJids;
        	}               	 
        	 if(fromUserJid!=null){
         	   Intent intent = new Intent();
         	   //直接添加
//         	   Presence subscription = new Presence(Presence.Type.subscribe);
//               subscription.setTo(fromUserJid);
//               XmppConnection.getConnection().sendPacket(subscription);
//               XmppService.addUser(roster, fromUserJid, XmppService.getUsername(fromUserJid));
      		   intent.setClass(FriendListActivity.this, FriendListActivity.class);
      		   startActivity(intent); 
     	   }     
        }    
        @Override  
        //监听好友同意添加消息
        public void entriesUpdated(Collection<String> invites) {  
        	 /*  for (Iterator iter = invites.iterator(); iter.hasNext();) {
          		  String fromUserJids = (String)iter.next();
          	      toUserJid = fromUserJids;
            	} 
        	    if(toUserJid!=null){
        	    	XmppService.addUser(roster, toUserJid, XmppService.getUsername(toUserJid));
                    loadFriend();
        	    } */              	    
        } 
        @Override  
        //监听好友删除消息
        public void entriesDeleted(Collection<String> delFriends) { 
        	if(delFriends.size()>0){
        		loadFriend();
    	    }  
        } 
       @Override  
       //监听好友状态改变消息
        public void presenceChanged(Presence presence) {  
    	    friendMood = presence.getStatus();
    		loadFriend();
       }        
	}
	
	private class NewMsgReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 收到廣播更新我们的界面
			adapter.notifyDataSetChanged();
		}
	}
}
