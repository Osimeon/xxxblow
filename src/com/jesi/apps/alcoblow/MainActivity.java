package com.jesi.apps.alcoblow;

import java.util.Arrays;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
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
	public static final int WEBVIEW_REQUEST_CODE = 100;
	
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
		
		if(resultCode == Activity.RESULT_OK){
			String verifier = data.getExtras().getString(oAuthVerifier);
			try{
				AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

				//long userID = accessToken.getUserId();
				//final User user = twitter.showUser(userID);
				//String username = user.getName();
				
				saveTwitterInfo(accessToken);

			} 
			catch (Exception e){
				Log.e("Twitter Login Failed", e.getMessage());
			}
		}
	}
	
	/* Shared preference keys */
	private static final String PREF_NAME = "sample_twitter_pref";
	private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
	private static final String PREF_USER_NAME = "twitter_user_name";
	
	private static Twitter twitter;
	private static RequestToken requestToken;
	
	private static SharedPreferences mSharedPreferences;
	
	private String consumerKey = null;
	private String consumerSecret = null;
	private String callbackUrl = null;
	private String oAuthVerifier = null;
	
	

	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnTwitter = (ImageView) findViewById(R.id.imgLoginWithTwitter);
		btnFacebook = (ImageView) findViewById(R.id.imgLoginWithFb);
		Session session = Session.getActiveSession();
		
		/* initializing twitter parameters from string.xml */
		initTwitterConfigs();
		
		/* Enabling strict mode */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		/* Initialize application preferences */
		mSharedPreferences = getSharedPreferences(PREF_NAME, 0);
		
		boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
		
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
				loginToTwitter();
			}
		});
		
		btnFacebook.setOnClickListener(new View.OnClickListener(){      
			public void onClick(View view){
				openFacebookSession();
			}
		});
	}
	
	private void saveTwitterInfo(AccessToken accessToken){

		long userID = accessToken.getUserId();

		User user;
		try{
			user = twitter.showUser(userID);

			String username = user.getName();

			/* Storing oAuth tokens to shared preferences */
			Editor e = mSharedPreferences.edit();
			e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
			e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
			e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
			e.putString(PREF_USER_NAME, username);
			e.commit();

		} 
		catch(TwitterException e1){
			e1.printStackTrace();
		}
	}
	
	/* Reading twitter essential configuration parameters from strings.xml */
	private void initTwitterConfigs(){
		consumerKey = getString(R.string.twitter_consumer_key);
		consumerSecret = getString(R.string.twitter_consumer_secret);
		callbackUrl = getString(R.string.twitter_callback);
		oAuthVerifier = getString(R.string.twitter_oauth_verifier);
	}
	
	private void loginToTwitter(){
		boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);

		if(!isLoggedIn){
			final ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(consumerKey);
			builder.setOAuthConsumerSecret(consumerSecret);

			final Configuration configuration = builder.build();
			final TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try{
				requestToken = twitter.getOAuthRequestToken(callbackUrl);

				/**
				 * Loading twitter login page on webview for authorization Once
				 * authorized, results are received at onActivityResult
				 * */
				final Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
				startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

			} 
			catch (TwitterException e){
				e.printStackTrace();
			}
		} 
		else{
			
		}
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