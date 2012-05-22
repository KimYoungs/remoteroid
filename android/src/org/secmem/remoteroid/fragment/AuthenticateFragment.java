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

package org.secmem.remoteroid.fragment;

import java.util.regex.Pattern;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.data.NativeKeyCode;
import org.secmem.remoteroid.natives.InputHandler;
import org.secmem.remoteroid.service.FrameBufferService;
import org.secmem.remoteroid.util.Util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class AuthenticateFragment extends Fragment {
	
	private EditText mEdtIpAddr;
	private EditText mEdtPassword;
	private CheckBox mCbAutoConn;
	private Button mBtnConnect;
	
	private boolean isIpValid=false;
	private boolean isPwValid=false;
	
	InputHandler hd = new InputHandler();
	
	
	public AuthenticateFragment(){
		
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_authenticate, null);
		
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mEdtIpAddr = (EditText)view.findViewById(R.id.ip_address);
		mEdtPassword = (EditText)view.findViewById(R.id.password);
		mCbAutoConn = (CheckBox)view.findViewById(R.id.auto_connect);
		mBtnConnect = (Button)view.findViewById(R.id.connect);
		
		mEdtIpAddr.setText("210.118.74.85");
		mEdtPassword.setText("aaa");
		
		mEdtIpAddr.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(Pattern.matches("^(([2][5][0-5]|[2][0-4][0-9]|[0-1][0-9][0-9]|[0-9][0-9]|[0-9])\\.){3}([2][5][0-5]|[2][0-4][0-9]|[0-1][0-9][0-9]|[0-9][0-9]|[0-9])$", s))
					isIpValid = true;
				else
					isIpValid = false;
				mBtnConnect.setEnabled(isIpValid&&isPwValid);
			}
			
		});
		
		mEdtPassword.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {	
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(count>0)
					isPwValid = true;
				else
					isPwValid = false;
				mBtnConnect.setEnabled(isIpValid&&isPwValid);
			}
			
		});
		
		mCbAutoConn.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Util.Connection.setAutoConnect(getActivity(), isChecked);
			}
			
		});
		
		hd.openInputDevice();
		mBtnConnect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getFragmentManager().beginTransaction().replace(R.id.container, 
						new ConnectingFragment(mEdtIpAddr.getText().toString(), mEdtPassword.getText().toString()))
						.commit();
				
				
				hd.keyStroke(NativeKeyCode.KEY_A);
				hd.keyStroke(NativeKeyCode.KEY_K);
			}
		});
		
		if(Util.Connection.isAutoConnectEnabled(getActivity())){
			getFragmentManager().beginTransaction().replace(R.id.container, 
					new ConnectingFragment(Util.Connection.getIpAddress(getActivity()), Util.Connection.getPassword(getActivity())))
					.commit();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		hd.closeInputDevice();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
