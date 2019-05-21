package com.parse.starter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class BranchCreationActivity extends AppCompatActivity {

    // declare views
    EditText branchName;
    EditText branchCode;
    EditText parkingFee;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_creation);

        // init views
        branchName = (EditText)findViewById(R.id.branch_name_txt);
        branchCode = (EditText)findViewById(R.id.branch_code_txt);
        parkingFee = (EditText)findViewById(R.id.branch_pricing_txt);

    }


    // function to add the branch to parse server
    public void submitBranch(View view)
    {
        final ParseObject branch = new ParseObject("Branch");
        // add the branch to the branch class in the parse server
        final String name = branchName.getText().toString();
        final String code = branchCode.getText().toString();
        final String pricing = parkingFee.getText().toString();

        if (!(name.isEmpty()) && !(code.isEmpty()) && !(pricing.isEmpty()))
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Create New Branch ?");
            builder.setMessage("A fee of "+pricing+" KWD "+"will be applied to all cars parked in the branch");
            builder.setCancelable(false);
            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // check if the code already exists
                    // later
                    // add the branch
                    final ProgressDialog progressDialog = new ProgressDialog(BranchCreationActivity.this);
                    progressDialog.setTitle("Create");
                    progressDialog.setMessage("Creating new branch");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    ParseQuery<ParseObject> branchQuery = ParseQuery.getQuery("Branch");
                    branchQuery.whereEqualTo("name" , name);
                    branchQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if( e == null && objects.size() > 0)
                            {
                                progressDialog.cancel();
                                Toast.makeText(BranchCreationActivity.this, "A branch already exists with this name", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                // open an alert dialog
                                branch.put("name" , name);
                                branch.put("code" , code);
                                branch.put("pricing" ,pricing);
                                branch.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if ( e == null)

                                        {
                                            progressDialog.cancel();
                                            Toast.makeText(BranchCreationActivity.this, "branch created successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext() , HomeActivity.class);
                                            startActivity(intent);
                                        }
                                        else {
                                            progressDialog.cancel();
                                            Toast.makeText(BranchCreationActivity.this, "failed to create branch , please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    });

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();


        }
        else
        {
            Toast.makeText(this, "failed , fields cannot be left empty", Toast.LENGTH_SHORT).show();
        }
    }
}
