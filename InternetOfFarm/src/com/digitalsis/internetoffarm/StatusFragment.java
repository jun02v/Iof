package com.digitalsis.internetoffarm;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class StatusFragment extends Fragment {
	
	private static StatusFragment sFragment = null;
	
	public static Fragment getInstance(){
		if(sFragment == null){
			sFragment = new StatusFragment();
		}
		return sFragment;
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
		LinearLayout statusLayout = (LinearLayout)inflater.inflate(R.layout.fragment_stauts, container, false);
		
		return statusLayout;		
	}
	
}
