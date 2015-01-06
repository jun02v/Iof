package com.digitalsis.internetoffarm;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handstudio.android.hzgrapherlib.graphview.LineGraphView;
import com.handstudio.android.hzgrapherlib.vo.linegraph.LineGraph;
import com.handstudio.android.hzgrapherlib.vo.linegraph.LineGraphVO;

public class GraphFragment extends Fragment {
	
	private static GraphFragment gFragment = null;
	private ViewGroup tempGraphViewLayout;
	private ViewGroup humidityGraphViewLayout;
	private ViewGroup ppfdGraphViewLayout;
	private ViewGroup co2GraphViewLayout;
	
	public static GraphFragment getInstance(){
		if(gFragment == null){
			gFragment = new GraphFragment();
		}
		return gFragment;
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
		LinearLayout graphLayout = (LinearLayout)inflater.inflate(R.layout.fragment_graph, container, false);
		tempGraphViewLayout = (ViewGroup) graphLayout.findViewById(R.id.temp_graph);
		LineGraphVO tempLineVo = setTemparatureGraph();
		tempGraphViewLayout.addView(new LineGraphView(getActivity(), tempLineVo));
		
		humidityGraphViewLayout = (ViewGroup) graphLayout.findViewById(R.id.humidity_graph);
		LineGraphVO humidityLineVo = setHumidityGraph();
		humidityGraphViewLayout.addView(new LineGraphView(getActivity(), humidityLineVo));
		
		ppfdGraphViewLayout = (ViewGroup) graphLayout.findViewById(R.id.ppfd_graph);
		LineGraphVO ppfdLineVo = setPPFDGraph();
		ppfdGraphViewLayout.addView(new LineGraphView(getActivity(), ppfdLineVo));
		
		co2GraphViewLayout = (ViewGroup) graphLayout.findViewById(R.id.co2_graph);
		LineGraphVO co2LineVo = setCo2Graph();
		co2GraphViewLayout.addView(new LineGraphView(getActivity(), co2LineVo));
		
		return graphLayout;		
	}
	
	private LineGraphVO setTemparatureGraph() {
		
		String[] legendArr 	= {"1","2","3","4","5"};
		float[] graph1 		= {500,100,300,200,100};
		//float[] graph2 		= {000,100,200,100,200};
		//float[] graph3 		= {200,500,300,400,000};
		
		List<LineGraph> arrGraph = new ArrayList<LineGraph>();
		arrGraph.add(new LineGraph("temparature", 0xaa66ff33, graph1));
		//arrGraph.add(new LineGraph("ios", 0xaa00ffff, graph2));
		//arrGraph.add(new LineGraph("tizen", 0xaaff0066, graph3));
		
		LineGraphVO vo = new LineGraphVO(legendArr, arrGraph);
		//vo.setAnimation(new GraphAnimation(GraphAnimation.LINEAR_ANIMATION, GraphAnimation.DEFAULT_DURATION));
		//vo.setGraphNameBox(new GraphNameBox());
		return vo;
	}
	
	private LineGraphVO setHumidityGraph() {
		
		String[] legendArr 	= {"1","2","3","4","5"};
		float[] humidityGraph 		= {000,100,200,100,200};
		
		List<LineGraph> arrGraph = new ArrayList<LineGraph>();
		arrGraph.add(new LineGraph("Humidity", 0xaa00ffff, humidityGraph));
		
		LineGraphVO vo = new LineGraphVO(legendArr, arrGraph);
		return vo;
	}
	
	private LineGraphVO setPPFDGraph() {
		
		String[] legendArr 	= {"1","2","3","4","5"};
		float[] ppfdGraph 		= {200,500,300,400,000};
		
		List<LineGraph> arrGraph = new ArrayList<LineGraph>();
		arrGraph.add(new LineGraph("PPFD", 0xaaff0066, ppfdGraph));
		
		LineGraphVO vo = new LineGraphVO(legendArr, arrGraph);
		return vo;
	}
	
	private LineGraphVO setCo2Graph() {
		
		String[] legendArr 	= {"1","2","3","4","5"};
		float[] co2Graph 		= {100,300,200,500,400};
		
		List<LineGraph> arrGraph = new ArrayList<LineGraph>();
		arrGraph.add(new LineGraph("CO2", 0xaaffff66, co2Graph));
		
		LineGraphVO vo = new LineGraphVO(legendArr, arrGraph);
		return vo;
	}
}
