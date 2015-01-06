package com.digitalsis.internetoffarm;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class IOFActivity extends FragmentActivity {

	private static int iconArray[] = new int[] {R.drawable.status, R.drawable.control, R.drawable.graph };
	ArrayAdapter<CharSequence> menuAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iof);
		
		menuAdapter = ArrayAdapter.createFromResource(this, R.array.menu_title, android.R.layout.simple_list_item_single_choice);
		FragmentPagerAdapter adapter = new IOFAdapter(getSupportFragmentManager());
		
		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(adapter);
		
		TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager);		
		
	}
	
	public class IOFAdapter extends FragmentPagerAdapter implements
	IconPagerAdapter {
		
		public IOFAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public int getIconResId(int index) {
			// TODO Auto-generated method stub
			return iconArray[index];
		}
		
		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			int pos = position % menuAdapter.getCount();
			switch(pos) {
				case 0 :				
					return StatusFragment.getInstance();
				case 1 :
					return ControlFragment.getInstance();
				case 2 :
					return GraphFragment.getInstance();
				default :
					return null;
			}
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return menuAdapter.getCount();
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return menuAdapter.getItem(position % menuAdapter.getCount());
		}
	}
}
