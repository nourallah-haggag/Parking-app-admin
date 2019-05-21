package com.parse.starter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    // declare the views
    EditText username;
    EditText password;

    String name;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize the views
        username = (EditText)findViewById(R.id.name_txt);
        password = (EditText)findViewById(R.id.pass_txt);
    }

    public void loginFunction(View view)
    {
         name = username.getText().toString();
         pass = password.getText().toString();

        // progress Dialog
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Log-in");
        progressDialog.setMessage("logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // check if the user is an admin or not
        ParseQuery<ParseUser> adminsQuer = ParseUser.getQuery();
        adminsQuer.whereEqualTo("type" , "admin");
        adminsQuer.whereEqualTo("username" , name);
        adminsQuer.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if( e == null && objects.size() > 0)
                {
                    for(ParseUser user  : objects)
                    {

                        // attempt login to parse server
                        user.logInInBackground(name, pass, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if ( e == null && user != null)
                                {

                                    progressDialog.cancel();
                                    Toast.makeText(LoginActivity.this, "welcome "+user.getUsername(), Toast.LENGTH_SHORT).show();
                                    // if successful go to home activity
                                    Intent intent = new Intent(getBaseContext() , HomeActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    progressDialog.cancel();
                                    // login failed
                                    Toast.makeText(LoginActivity.this, "login failed "+e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
                else
                {
                    progressDialog.cancel();
                    Toast.makeText(LoginActivity.this, "Access restricted to admins", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
}
