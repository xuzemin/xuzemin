package com.d3.yiqi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import com.d3.yiqi.R;
import com.d3.yiqi.service.FriendAddService;
import com.d3.yiqi.service.XmppApplication;
import com.d3.yiqi.service.XmppConnection;
import com.d3.yiqi.service.XmppService;
import com.d3.yiqi.util.MyToast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressWarnings("all")
public class FriendAddActivity extends Activity {
	private String pUSERID;// ??????
	private Button search_button;
	private Button goback_button;
	private EditText search_textEditText;
	private String queryResult = "";
	private ListView list;
	Roster roster = XmppConnection.getConnection().getRoster();
	private FriendAddService friendAddService = new FriendAddService();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ?????????
		setContentView(R.layout.friend_add);
		this.pUSERID =  XmppApplication.user;
		initView();
		// ???????
		search_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				searchFriend();
			}
		});
		// ??????
		goback_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void initView(){
		list = (ListView) findViewById(R.id.testlistshow);
		search_button = (Button) findViewById(R.id.search_cancel_button);
		goback_button = (Button) findViewById(R.id.goback_button);
		search_textEditText = ((EditText) findViewById(R.id.search_text));
	}

	public void searchFriend() {
		String search_text = search_textEditText.getText().toString();
		if (search_text.equals("")) {
			MyToast.showToast(getApplicationContext(), "??????????????");
		} else {
			queryResult = friendAddService.searchFriend(search_text, queryResult);
			
			if (!queryResult.equals("")) {
				// ?????????�????????
				ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", queryResult); // ??????
				listItem.add(map);
				// ????????????Item???????????????
				SimpleAdapter listItemAdapter = new SimpleAdapter(this,
						listItem,// ?????
						R.layout.friend_search_view,// ListItem??XML???
						// ?????????ImageItem?????????
						new String[] { "name", },
						// ImageItem??XML???????????ImageView,????TextView ID
						new int[] { R.id.itemtext });
				// ?????????
				list.setAdapter(listItemAdapter);
				// ?????????
				list.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
						HashMap<String, String> map = (HashMap<String, String>) list.getItemAtPosition(position);
						final String name = map.get("name");
						//????
						AlertDialog.Builder dialog = new AlertDialog.Builder(FriendAddActivity.this);
						dialog.setTitle("??????").setIcon(R.drawable.default_head)
								.setMessage("??????????" + name + "?????????")
								.setPositiveButton("???",new DialogInterface.OnClickListener() {     //??????
								@Override
								public void onClick(DialogInterface dialog,int which) {
									// stub
									Roster roster = XmppConnection.getConnection().getRoster();
									String userName = name+ "@"+ XmppConnection.getConnection().getServiceName();
									// 
									XmppService.addUser(roster,userName, name);
									Presence subscription = new Presence(Presence.Type.subscribe);
									subscription.setTo(userName);
									dialog.cancel();// ?????????
									Intent intent = new Intent();
									intent.putExtra("USERID",pUSERID);
									intent.setClass(FriendAddActivity.this,FriendListActivity.class);
									startActivity(intent);
									finish();
								}
							})
								.setNegativeButton("???",              //??????
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,int which) {
												dialog.cancel();// ?????????
											}
										}).create().show();
					}
				});
			} else {
				Toast.makeText(FriendAddActivity.this, "??????????????????????????????",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!queryResult.equals("")) {
			menu.clear();
			menu.add(Menu.NONE, Menu.FIRST + 1, 1, "???????").setIcon(
					R.mipmap.addfriends_icon_icon);
			menu.add(Menu.NONE, Menu.FIRST + 2, 1, "???????").setIcon(
					R.mipmap.menu_exit);
		} else {
			menu = null;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
//			View view = View.inflate(this, R.layout.dialog, null);
//			final PopupWindow mPopupWindow = new PopupWindow(view,
//					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
//			mPopupWindow.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
//					LayoutParams.WRAP_CONTENT);
//			mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
//			mPopupWindow.showAtLocation(((Activity) this).getWindow()
//					.getDecorView(), Gravity.CENTER, 0, 0);
//			mPopupWindow.setAnimationStyle(R.style.animationmsg);
//			mPopupWindow.setFocusable(true);
//			mPopupWindow.setTouchable(true);
//			mPopupWindow.setOutsideTouchable(true);
//			mPopupWindow.update();
//			final EditText addFriend = (EditText) view
//					.findViewById(R.id.addfriend);
//			Button sure = (Button) view.findViewById(R.id.sure);
//			Button cancle = (Button) view.findViewById(R.id.cancle);
//			sure.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					String groupName = addFriend.getText().toString().trim();
//					if (groupName.equals("") || groupName.equals("")) {
//						Toast.makeText(FriendAddActivity.this, "?????????????!",
//								Toast.LENGTH_SHORT).show();
//					} else {
//						boolean result = false;
//						result = XmppService.addGroup(roster, groupName);
//						if (result) {
//							Roster roster = XmppConnection.getConnection()
//									.getRoster();
//							String userName = queryResult+ "@"+ XmppConnection.getConnection().getServiceName();
//							XmppService.addUsers(roster, userName, queryResult,groupName);
//							Intent intent = new Intent();
//							intent.putExtra("USERID", pUSERID);
//							intent.setClass(FriendAddActivity.this,FriendListActivity.class);
//							startActivity(intent);
//						} else {
//							Toast.makeText(FriendAddActivity.this, "?????????!",
//									Toast.LENGTH_SHORT).show();
//						}
//					}
//					mPopupWindow.dismiss();
//				}
//			});
//			cancle.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					mPopupWindow.dismiss();
//				}
//			});
			break;
		case Menu.FIRST + 2:
			Intent intent = new Intent(this, ChatActivity.class);
			String pFRIENDID = queryResult + "@"+ XmppConnection.getConnection().getServiceName();
			intent.putExtra("FRIENDID", pFRIENDID);
			intent.putExtra("user", pFRIENDID);
			intent.putExtra("USERID", pUSERID);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
