package com.jesi.apps.alcoblow;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class ProfileActivity extends Activity{
	
	Session session = Session.getActiveSession();
	ProfilePictureView profile;
	TextView username;
	
	@Override
	protected void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_profile);
		
		username = (TextView) findViewById(R.id.lblUserName);
		profile = (ProfilePictureView) findViewById(R.id.imgUserProfile);
		
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback(){
		    @Override
		    public void onCompleted(GraphUser user, Response response){
		        if(session == Session.getActiveSession()){
		            if(user != null){
		                // Set the id for the ProfilePictureView
		                // view that in turn displays the profile picture.
		            	profile.setProfileId(user.getId());
		                // Set the Textview's text to the user's name.
		                username.setText(user.getName());
		            }
		        }
		        if(response.getError() != null){
		            // Handle errors, will do so later.
		        }
		    }
		});
		request.executeAsync();
	}
}
