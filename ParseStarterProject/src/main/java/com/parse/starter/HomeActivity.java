package com.parse.starter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // declare view
    FloatingActionButton floatingActionButton;
    static RecyclerView recyclerView;
    static List<BranchModel> branchList;
    static RecyclerBranchesAdapter adapter;
    static ProgressDialog progressDialog;
    static Context context;

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit App");
        builder.setMessage("Are you sure you want to exit ?");
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // actually exit the app

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("shouldFinish", true);
                startActivity(intent);



            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = this;

        // init view
        floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_branches);
        branchList = new ArrayList<>();
        adapter = new RecyclerBranchesAdapter(this , branchList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // get the branches from the parse server
        getBranches();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , BranchCreationActivity.class);
                startActivity(intent);
            }
        });
    }

    public static void getBranches()
    {
        // progress for getting branches
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("getting data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ParseQuery<ParseObject> branchesQuery = ParseQuery.getQuery("Branch");
        branchesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if ( e == null)
                {
                    recyclerView.removeAllViews();
                    branchList.clear();
                    progressDialog.cancel();
                    if(objects.size() > 0)
                    {
                        for(ParseObject object : objects)
                        {

                            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                            String strDate = dateFormat.format(object.getCreatedAt());

                            branchList.add(new BranchModel(object.getString("name") , "code: "+object.getString("code") , "Date Created: "+strDate ));

                        }
                       adapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    progressDialog.cancel();
                    // handle error through alert dialog
                }
            }
        });

    }
}
