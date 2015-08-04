/*
 * Copyright 2013 - learnNcode (learnncode@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.share.jabong.twitter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.share.jabong.R;
import com.share.jabong.twitter.HelperMethods.TwitterCallback;

public class BaseActivity extends Activity {
	private Context context;
	private static final String TAG = "BaseActivity";
	private AlertDialog mAlertBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo_twitter_base);

		context = BaseActivity.this;

		findViewById(R.id.postImageButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LoginActivity.isActive(context)) {
					try {

						mAlertBuilder = new AlertDialog.Builder(context).create();
						mAlertBuilder.setCancelable(false);
						mAlertBuilder.setTitle(R.string.please_wait_title);
						View view = getLayoutInflater().inflate(R.layout.view_loading, null);
						((TextView) view.findViewById(R.id.messageTextViewFromLoading)).setText(getString(R.string.posting_image_message));
						mAlertBuilder.setView(view);
						mAlertBuilder.show();

						InputStream inputStream  = v.getContext().getAssets().open("1.png");
						Bitmap bmp = BitmapFactory.decodeStream(inputStream);
						String filename = Environment.getExternalStorageDirectory().toString() + File.separator + "1.png";
						Log.d("BITMAP", filename);
						FileOutputStream out = new FileOutputStream(filename);
						bmp.compress(Bitmap.CompressFormat.PNG, 90, out);

						HelperMethods.postToTwitterWithImage(context, ((Activity)context), filename, getString(R.string.tweet_with_image_text), new TwitterCallback() {

							@Override
							public void onFinsihed(Boolean response) {
								mAlertBuilder.dismiss();
								Log.d(TAG, "----------------response----------------" + response);
								Toast.makeText(context, getString(R.string.image_posted_on_twitter), Toast.LENGTH_SHORT).show();
							}
						});

					} catch (Exception ex) {
						Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
					}
				}else{
					startActivity(new Intent(context, LoginActivity.class));
				}				
			}
		});

		findViewById(R.id.postTweetButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				if (LoginActivity.isActive(context)) {
					try {

						mAlertBuilder = new AlertDialog.Builder(context).create();
						mAlertBuilder.setCancelable(false);
						mAlertBuilder.setTitle(R.string.please_wait_title);
						View view = getLayoutInflater().inflate(R.layout.view_loading, null);
						((TextView) view.findViewById(R.id.messageTextViewFromLoading)).setText(getString(R.string.posting_tweet_message));
						mAlertBuilder.setView(view);
						mAlertBuilder.show();

						HelperMethods.postToTwitter(context, ((Activity)context), getString(R.string.tweet_text), new TwitterCallback() {
							@Override
							public void onFinsihed(Boolean response) {
								Log.d(TAG, "----------------response----------------" + response);
								mAlertBuilder.dismiss();
								Toast.makeText(context, getString(R.string.tweet_posted_on_twitter), Toast.LENGTH_SHORT).show();

							}
						});

					} catch (Exception ex) {
						ex.printStackTrace();
						Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
					}
				}else{
					startActivity(new Intent(context, LoginActivity.class));
				}				
			}
		});
		findViewById(R.id.facebook).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(BaseActivity.this,FBActivity.class));
				
			}
		});
		findViewById(R.id.instagram).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				postOnInstagram();
			}
		});
		findViewById(R.id.whatsapp).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shareImageWhatsApp();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (LoginActivity.isActive(context)) {
			Toast.makeText(BaseActivity.this, getString(R.string.authentication_done_now_post_tweet_or_image_text), Toast.LENGTH_LONG).show();
		}
	}
	private void postOnInstagram(){
		Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (intent != null)
        {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.instagram.android");
            try {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), Environment.getExternalStorageDirectory().toString() + File.separator + "1.png", "I am Happy", "Share happy !")));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            shareIntent.setType("image/jpeg");

            startActivity(shareIntent);
        }
        else
        {
            // bring user to the market to download the app.
            // or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id="+"com.instagram.android"));
            startActivity(intent);
        }
	}
	public void shareImageWhatsApp() {

	    //Bitmap adv = BitmapFactory.decodeResource(getResources(), R.drawable.adv);
	    Intent share = new Intent(Intent.ACTION_SEND);
	    share.setType("image/jpeg");
	    //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	   // adv.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	    File f = new File(Environment.getExternalStorageDirectory()
	            + File.separator + "temporary_file.jpg");
	    /*try {
	        f.createNewFile();
	        new FileOutputStream(f).write(bytes.toByteArray());
	    } catch (IOException e) {
	        e.printStackTrace();
	    } */
	    share.putExtra(Intent.EXTRA_STREAM,
	            Uri.parse(Environment.getExternalStorageDirectory().toString() + File.separator + "1.png"));
	    if(isPackageInstalled("com.whatsapp",this)){
	          share.setPackage("com.whatsapp"); 
	          startActivity(Intent.createChooser(share, "Share Image"));

	    }else{

	        Toast.makeText(getApplicationContext(), "Please Install Whatsapp", Toast.LENGTH_LONG).show();
	    }

	}

	private boolean isPackageInstalled(String packagename, Context context) {
	    PackageManager pm = context.getPackageManager();
	    try {
	        pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
	        return true;
	    } catch (NameNotFoundException e) {
	        return false;
	    }
	}
}
