/*
 * Remoteroid - A remote control solution for Android platform, including handy file transfer and notify-to-PC.
 * Copyright (C) 2012 Taeho Kim(jyte82@gmail.com), Hyomin Oh(ohmnia1112@gmail.com), Hongkyun Kim(godgjdgjd@nate.com), Yongwan Hwang(singerhwang@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package org.secmem.remoteroid.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.secmem.remoteroid.IRemoteroid;
import org.secmem.remoteroid.R;
import org.secmem.remoteroid.adapter.DataList;
import org.secmem.remoteroid.adapter.ExplorerAdapter;
import org.secmem.remoteroid.data.CategoryList;
import org.secmem.remoteroid.dialog.CategoryDialog;
import org.secmem.remoteroid.expinterface.OnFileLongClickListener;
import org.secmem.remoteroid.expinterface.OnFileSelectedListener;
import org.secmem.remoteroid.expinterface.OnPathChangedListener;
import org.secmem.remoteroid.service.RemoteroidService;
import org.secmem.remoteroid.service.RemoteroidService.ServiceState;
import org.secmem.remoteroid.util.HongUtil;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Annotation;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class ExplorerActivity extends SherlockActivity implements OnScrollListener {
	
	public static boolean SCROLL_STATE = false;
	public static ArrayList<CategoryList> searchList = new ArrayList<CategoryList>();
	public static boolean isSearched=false;
	
	public static List<String> fileInfo = new ArrayList<String>();
	public static DataList dataList;
	
	private static int CODE_CATEGORY = 1;
	
	public static String TYPE_IMAGE = "0";
	public static String TYPE_VIDEO = "1";
	public static String TYPE_MUSIC = "2";
	public static String TYPE_CUTSOM = "3";
	
	public static int ADAPTER_TYPE_EXPLORER = 1;
	public static int ADAPTER_TYPE_CATEGORY = 2;
	
	private Button categoryBtn;
	private Button homeBtn;
	private Button topBtn;
	private Button exitBtn;
	
	private TextView pathTv;
	
	private GridView gridview;
	
	public static ExplorerAdapter adapter;
	
	private boolean isTimer=false;
	private boolean isBound=false;
	
	private ProgressDialog mProgress;
	private IRemoteroid mRemoteroid;
	private ServiceConnection conn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mRemoteroid = IRemoteroid.Stub.asInterface(service);
			try {
				if(mRemoteroid.isConnected()){
					Log.i("qq","Connection True");
					mRemoteroid.onSendFile(fileInfo);
				}
				else{
					Log.i("qq","Connection False");
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mRemoteroid = null;
		}
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.explorer_activity);	
		
		categoryBtn = (Button)findViewById(R.id.explorer_btn_category);
		topBtn = (Button)findViewById(R.id.explorer_btn_top);
		homeBtn = (Button)findViewById(R.id.explorer_btn_home);
		exitBtn = (Button)findViewById(R.id.explorer_btn_exit);
		
		categoryBtn.setOnClickListener(topBtnListener);
		topBtn.setOnClickListener(topBtnListener);
		homeBtn.setOnClickListener(topBtnListener);
		exitBtn.setOnClickListener(topBtnListener);
		
		pathTv = (TextView)findViewById(R.id.explorer_tv_path);
		
		gridview = (GridView)findViewById(R.id.explorer_view_grid);
		gridview.setOnScrollListener(this);
		
		dataList = new DataList(this);
		dataList.setOnPathChangedListener(onPathChanged);
		dataList.setOnFileSelected(onFileSelected);
		
		dataList.setPath("/mnt/sdcard");
		
		adapter = new ExplorerAdapter(this, R.layout.grid_explorer_row, dataList, ADAPTER_TYPE_EXPLORER, onFileLongClick);
		
		LayoutAnimationController gridAnimation = AnimationUtils.loadLayoutAnimation(ExplorerActivity.this, R.anim.layout_wave_scale);
		gridview.setLayoutAnimation(gridAnimation);
		
		gridview.setAdapter(adapter);
		
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dataList = null;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	OnClickListener topBtnListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			
			case R.id.explorer_btn_home : 			// ��
				
				if(adapter.getType()==ADAPTER_TYPE_CATEGORY){
					adapter.setType(ADAPTER_TYPE_EXPLORER);
					fileInfo.clear();
				}
				dataList.setPath("/mnt/sdcard");
				LayoutAnimationController gridAnimation = AnimationUtils.loadLayoutAnimation(ExplorerActivity.this, R.anim.layout_wave_scale);
				gridview.setLayoutAnimation(gridAnimation);
				setDisplayView();
				
				break;
			
			case R.id.explorer_btn_top : 				// ���.
				
				if(adapter.getType()==ADAPTER_TYPE_CATEGORY){
					adapter.setType(ADAPTER_TYPE_EXPLORER);
					fileInfo.clear();
					setDisplayView();
				}
				else{
					String backPath = dataList.getBackPathName();
					if(dataList.getPathCount()!=0){
						dataList.setPath(backPath);
						setDisplayView();
					}
					else{
						HongUtil.makeToast(ExplorerActivity.this, getString(R.string.is_the_parent_folder));
					}
				}
				break;
			
			case R.id.explorer_btn_category:			// 移댄�怨�━
				
				Intent intent = new Intent(ExplorerActivity.this, CategoryDialog.class);
				startActivityForResult(intent, CODE_CATEGORY);
				break;
				
			case R.id.explorer_btn_exit:
				HongUtil.showExitDialog(ExplorerActivity.this);
				
				break;
			}
		}
	};
	
	public void onBackPressed() {
		
		String backPath = dataList.getBackPathName();
		
		if(adapter.getType()==ADAPTER_TYPE_CATEGORY){
			adapter.setType(ADAPTER_TYPE_EXPLORER);
			fileInfo.clear();
			setDisplayView();
		}
		else if(dataList.getPath().equals("/mnt/sdcard/")){
			if(!isTimer){
				HongUtil.makeToast(ExplorerActivity.this, "\'�ㅻ�媛�린\' 踰�����������Ⅴ��㈃ 醫���⑸���");
				backTimer timer = new backTimer(2000, 1);
				timer.start();
			}
			else{
//				android.os.Process.killProcess(android.os.Process.myPid());
				finish();
			}
		}
		else if(dataList.getPath().equals("/mnt/")){
			dataList.setPath("/mnt/sdcard");
			setDisplayView();
		}
		else if(dataList.getPath().equals("/")){
			dataList.setPath("/mnt/");
			setDisplayView();
		}
		else{
			dataList.setPath(backPath);
			setDisplayView();
		}
		
//		if(dataList.getPathCount()==0){
//			finish();
//		}
	};
	
	private void setDisplayView(){
		adapter.notifyDataSetChanged();
		gridview.setSelection(20);
		gridview.invalidateViews();
	}
	
	public class backTimer extends CountDownTimer{
		public backTimer(long millisInFuture , long countDownInterval){
			super(millisInFuture, countDownInterval);
			isTimer = true;
		}

		@Override
		public void onFinish() {
			isTimer = false;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == CODE_CATEGORY){
				
				if(searchList.size()!=0){
					searchList.clear();
				}
				
				String index = "."+data.getStringExtra("category");
				String type = data.getStringExtra("type");
				
				new SearchAsync().execute(index,type);
				
			}
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch(scrollState){
		case OnScrollListener.SCROLL_STATE_IDLE:
			SCROLL_STATE= false;
			adapter.notifyDataSetChanged();
			break;
			
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			SCROLL_STATE = true;
			break;
			
		case OnScrollListener.SCROLL_STATE_FLING:
			SCROLL_STATE = true;
			break;		
		}
	}
	
	private OnPathChangedListener onPathChanged = new OnPathChangedListener() {
		public void onChanged(String path) {
			pathTv.setText(path);
			
		}
	};
    
    private OnFileSelectedListener onFileSelected = new OnFileSelectedListener() {

		public void onSelected(String path, String fileName) {
		}
	};
	
	private OnFileLongClickListener onFileLongClick = new OnFileLongClickListener() {
		
		@Override
		public void onLongclick() {
				if(fileInfo.size()!=0 && !isBound){
					bindService(new Intent(ExplorerActivity.this, RemoteroidService.class), conn, Context.BIND_AUTO_CREATE);
					isBound=true;
				}
				else if(isBound){
					try {
						mRemoteroid.onSendFile(fileInfo);
					} catch (RemoteException e) {
					}
				}
		}
	};
	
	private class SearchAsync extends AsyncTask<String, Void, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress = new ProgressDialog(ExplorerActivity.this);
			mProgress.setTitle(getString(R.string.searching));
			mProgress.setMessage(getString(R.string.searching_for_file_));
			mProgress.show();
			mProgress.setCancelable(false);
			isSearched=true;
		}

		@Override
		protected String doInBackground(String... params) {
			String type = params[1];
			
			if(type.equals(TYPE_IMAGE)){
				String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
				
				@SuppressWarnings("deprecation")
				Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
				HongUtil.getPhoto(imageCursor);
			}
			
			else if(type.equals(TYPE_VIDEO)){
				String[] infoVideo = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA};
				
				@SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, infoVideo, null, null, null);
				
				HongUtil.getVideo(cursor);
			}
			
			else if(type.equals(TYPE_MUSIC)){
				String[] mediaData = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM_ID};
				
				@SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaData, null, null, null);
				
				HongUtil.getMusic(cursor);
			}
			
			else if(type.equals(TYPE_CUTSOM)){
				HongUtil.searchIndex(HongUtil.getRootPath(), params[0]);
			}
			
			return type;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			adapter.setType(ADAPTER_TYPE_CATEGORY);
			adapter.setCategoryList(searchList);
			adapter.setCategoryType(result);
			fileInfo.clear();
			adapter.notifyDataSetChanged();
			isSearched=false;
			mProgress.dismiss();
		}
		
	}
	
	public GridView getGridview() {
		return gridview;
	}
	public void setGridview(GridView gridview) {
		this.gridview = gridview;
	}
	
}
