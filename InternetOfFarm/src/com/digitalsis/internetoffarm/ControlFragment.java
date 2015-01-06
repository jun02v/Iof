package com.digitalsis.internetoffarm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ControlFragment extends Fragment {
	
	private static ControlFragment cFragment = null;
	
	public static ControlFragment getInstance(){
		if(cFragment == null){
			cFragment = new ControlFragment();
		}
		return cFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return super.onCreateView(inflater, container, savedInstanceState);
		LinearLayout controlLayout = (LinearLayout)inflater.inflate(R.layout.fragment_control, container, false);
		
		return controlLayout;		
	}
	
}
