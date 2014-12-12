package com.jesi.apps.alcoblow;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;

public class MainActivity extends Activity{
	
	ImageView btnTwitter;
	ImageView btnFacebook;
	
	public static final String TAG = "Alcoblow";
	
	private void openFacebookSession(){
		openActiveSession(MainActivity.this, true, Arrays.asList(new String[] {"email", "user_birthday", "user_hometown", "user_location"}), new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception){
				if(exception != null){
					Log.d("Facebook", exception.getMessage());
				}
				else{
					Intent i = new Intent(MainActivity.this, ProfileActivity.class);
					startActivity(i);
					finish();
				}
				
				Log.d("Facebook", "Session State: " + session.getState());
			}
		});
	}
	
	private static Session openActiveSession(Activity activity, boolean allowLoginUI, @SuppressWarnings("rawtypes") List permissions, StatusCallback callback){ 
		@SuppressWarnings("unchecked")
		OpenRequest openRequest = new OpenRequest(activity).setPermissions(permissions).setCallback(callback);
		Session session = new Session.Builder(activity).build();
		
		if(SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI){
			Session.setActiveSession(session);
			session.openForRead(openRequest);
			return session;
		}
		
		return null;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(MainActivity.this, requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnTwitter = (ImageView) findViewById(R.id.imgLoginWithTwitter);
		btnFacebook = (ImageView) findViewById(R.id.imgLoginWithFb);
		Session session = Session.getActiveSession();
		
		if(session == null){
            session = Session.openActiveSessionFromCache(this);
        }
		
		if(session.isOpened()){
			Intent i = new Intent(MainActivity.this, ProfileActivity.class);
			startActivity(i);
			finish();
		}
		
		btnTwitter.setOnClickListener(new View.OnClickListener(){      
			public void onClick(View view){
				
			}
		});
		
		btnFacebook.setOnClickListener(new View.OnClickListener(){      
			public void onClick(View view){
				openFacebookSession();
			}
		});
	}
	
	@Override
	protected void onResume(){
		super.onResume();	
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}
	
	@Override
	public void onDestroy(){
	    super.onDestroy();
	}
}