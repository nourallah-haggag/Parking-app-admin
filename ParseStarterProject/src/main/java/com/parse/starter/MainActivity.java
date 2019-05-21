/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
//import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity {




  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
      // close the app entirely
      if (getIntent().getBooleanExtra("shouldFinish", false)) {
          finish();
      }

      final ImageView imageView = (ImageView)findViewById(R.id.logo_splash);
      ParseUser.getCurrentUser().logOut();

      // timer --> change activity after some time
      new CountDownTimer(2000 , 1 ){

          @Override
          public void onTick(long l) {
              //Toast.makeText(SplashScreenActivity.this, "timeeee", Toast.LENGTH_SHORT).show();

          }


          @Override
          public void onFinish() {
              Intent intent = new Intent(MainActivity.this , LoginActivity.class);
              // transistion
              Pair[] pairs = new Pair[1];
              pairs[0]= new Pair<View, String>(imageView , "imgTrans");

              //pairs[3]= new Pair<View , String>(layout1 , "layoutTrans");
              ActivityOptions options = null;
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                  options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this ,pairs );
              }
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                  startActivity(intent , options.toBundle());
              }

          }
      }.start();
    // creating a parse object
   /* ParseObject score = new ParseObject("Score");
    // add attributes in score object
    score.put("username" , "rob"); // create a variable called username with value = rob
    score.put("score" , 80);
    score.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if ( e == null)
        {
          // successful
          Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
        }
        else
        {
          Toast.makeText(MainActivity.this, "failed"+ e.toString(), Toast.LENGTH_SHORT).show();
        }
      }
    });*/

   // restore a value from parseserver
    /*ParseQuery<ParseObject> query = ParseQuery.getQuery("Score"); // perform a query on the class score
    query.getInBackground("HFATa8jbW1", new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject object, ParseException e) {
        if(e == null && object != null)
        {

          // update the score to 200
          object.put("score" , 200);
          object.saveInBackground();
          Toast.makeText(MainActivity.this, object.getString("username"), Toast.LENGTH_SHORT).show();
          Toast.makeText(MainActivity.this, Integer.toString(object.getInt("score")), Toast.LENGTH_SHORT).show();


        }
      }
    });/*/

    // create a tweet class containing a username and a tweet
   /* ParseObject tweet = new ParseObject("Tweet");
    // create attributes for the parse object
    tweet.put("username" , "haggag");
    tweet.put("tweet" , "hi there I am 7aggoog");
    // save theb parse object
    tweet.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if( e == null )
        {
          Toast.makeText(MainActivity.this, "tweet saved successfully", Toast.LENGTH_SHORT).show();
        }
        else
        {
          Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();

        }
      }
    });*/

    /*// query the tweet object
    ParseQuery<ParseObject> tweetQuery = ParseQuery.getQuery("Tweet");
    tweetQuery.getInBackground("TTjnw68ke6", new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject object, ParseException e) {

        if( e== null && object != null)
        {
          Toast.makeText(MainActivity.this, object.getString("username"), Toast.LENGTH_SHORT).show();
          Toast.makeText(MainActivity.this, object.getString("tweet"), Toast.LENGTH_SHORT).show();
        }

      }
    });*/

   /* ParseQuery<ParseObject> scoreQuery = ParseQuery.getQuery("Score");
    scoreQuery.whereLessThan("score" , 1000);
    scoreQuery.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> objects, ParseException e) {
        if( e == null)
        {
          Log.i("find" , "retrieved:" +objects.size());
          if (objects.size() > 0)
          {
            // loop over the parse objects in the parse list
            for(ParseObject object :objects)
            {

                object.put("score" , object.getInt("score")+100 );
                object.saveInBackground();

              Log.i("name" , object.getString("username"));
              Log.i("score" ,Integer.toString(object.getInt("score")));
            }
          }
        }

      }
    });*/

/*
   // create a new usser registration
   ParseUser user = new ParseUser();
   user.setUsername("alaa");
   user.setPassword("0000");
   user.signUpInBackground(new SignUpCallback() {
     @Override
     public void done(ParseException e) {
       if ( e == null)
       {
         Log.i("log status " , "success");
       }
       else
       {
         Log.i("log status " , "failed");
       }
     }
   });
   */

   /*ParseUser.logInInBackground("haggag", "0000", new LogInCallback() {
       @Override
       public void done(ParseUser user, ParseException e) {
           if (user!=null &&  e == null)
           {
               Log.i("log" , "welocome "+user.getUsername());
           }
           else
           {
               Log.i("log" , e.toString());
           }
       }
   });*/




    
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}