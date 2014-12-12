package com.jesi.apps.alcoblow;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SpotMapActivity extends Activity {
	static final LatLng TutorialsPoint = new LatLng(21, 57);
	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spot_map);
		
		try{
			if(googleMap == null){
				googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			}
			
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			googleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title("TutorialsPoint"));

		} 
		catch (Exception e){
			e.printStackTrace();
		}
	}
}