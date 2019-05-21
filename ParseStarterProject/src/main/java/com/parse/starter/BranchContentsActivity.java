package com.parse.starter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

public class BranchContentsActivity extends AppCompatActivity implements AddUserDialog.AddDialogListener {

    // declare the views
    TextView branchName;
    TextView branchCode;
    TextView dateCreated;

    static ProgressDialog progressDialog;
    static ProgressDialog progressDialog2;
    static Context context;


    static String bName;
    String bCode;
    String dCreated;


    // list components card
    RecyclerView recyclerViewCards;
    static List<CardModel> cardsList;
    static RecyclerCardsAdapter adapter;

    // list components user
    static List<UserModel> usersList;
    static RecyclerView recyclerViewUsers;
    static RecyclerUsersAdapter usersAdapter;


   /* // list components for transactions
    RecyclerView transactionsRecyclerView;
    static List<TransactionModel> tarnsList;

    */

    // for the logout logic
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.log_out_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Log-Out");
        progressDialog.setMessage("logging out...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    progressDialog.cancel();
                    Intent intent = new Intent(BranchContentsActivity.this , LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    progressDialog.cancel();
                    Toast.makeText(BranchContentsActivity.this, "log-out failed, please check internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return super.onOptionsItemSelected(item);

    }








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_contents);
        progressDialog = new ProgressDialog(this);
        progressDialog2 = new ProgressDialog(this);
        context = this;

        // init the views
        branchName = (TextView)findViewById(R.id.branch_name_contents);
        branchCode = (TextView)findViewById(R.id.branch_code_contents);
        dateCreated = (TextView)findViewById(R.id.date_created_contents);

        // set the values of the text fields
         bName = getIntent().getStringExtra("branchName");
         bCode = getIntent().getStringExtra("branchCode");
         dCreated = getIntent().getStringExtra("dateCreated");
        branchName.setText(bName);
        branchCode.setText(bCode);
        dateCreated.setText(dCreated);

        // init card contents
        recyclerViewCards = (RecyclerView)findViewById(R.id.recycler_view_cards);
        cardsList = new ArrayList<>();
        adapter = new RecyclerCardsAdapter(this , cardsList);
        recyclerViewCards.setAdapter(adapter);
        recyclerViewCards.setLayoutManager(new GridLayoutManager(this , 2 , GridLayoutManager.HORIZONTAL , false));
        getCards();

        // init the users components
        recyclerViewUsers = (RecyclerView)findViewById(R.id.recycler_view_users);
        usersList = new ArrayList<>();
        usersAdapter = new RecyclerUsersAdapter(this , usersList);
        recyclerViewUsers.setAdapter(usersAdapter);
        recyclerViewUsers.setLayoutManager(new GridLayoutManager(this ,2 , GridLayoutManager.HORIZONTAL , false));
        getUsers();


        // floating button to go to the transactions activity
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingActionButton2);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BranchContentsActivity.this , TransactionsActivity.class);
                intent.putExtra("branch" , bName); // send the branch name to query by it
                startActivity(intent);

            }
        });



    }

    // function for adding a new card to the branch
    public void addCard(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new card - enter code");
        builder.setIcon(R.drawable.ic_card);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(editText);
        // the card will be free by default
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //check if the card already exists and do not add it if it exists
                ParseQuery<ParseObject> cardQuery = ParseQuery.getQuery("Card");
                cardQuery.whereEqualTo("Code" , editText.getText().toString());
                cardQuery.setLimit(1); //  to avoid errors if more than one is found due to any reason
                cardQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e == null)
                        {
                            // a card has been found
                            // do not add
                            if(objects.size()>0) {
                                Toast.makeText(BranchContentsActivity.this, "Failed, Card already exists", Toast.LENGTH_LONG).show();
                            }else {
                                // add card to the parse server
                                ParseObject card = new ParseObject("Card");
                                if (editText.getText().toString() != "") {
                                    card.put("status", "free");
                                    card.put("branch", bName);
                                    card.put("Code", editText.getText().toString());
                                    card.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                            if (e == null) {
                                                Toast.makeText(BranchContentsActivity.this, "card successfully added", Toast.LENGTH_SHORT).show();
                                                getCards();
                                            }
                                        }
                                    });

                                }else
                                {
                                    Toast.makeText(BranchContentsActivity.this, "failed , code cannot be empty", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                        else{

                            Toast.makeText(BranchContentsActivity.this, "Failed, please check internet connection and try again "+e.getMessage(), Toast.LENGTH_LONG).show();


                        }
                    }
                });


            }


        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    // function to add users
    public void addUser(View view)
    {
        /*Intent intent = new Intent(this , AddUserEmployeeActivity.class);
        intent.putExtra("branchName" , bName);
        startActivity(intent);*/

        // alet dialog to add a new user
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // create the dialog
        builder.setView(R.layout.activity_add_user_employee);
        builder.show();*/
       AddUserDialog addUserDialog = new AddUserDialog();
       addUserDialog.show(getSupportFragmentManager() , "addUserDialog");


    }

    // get the cards after querying them from the parse server
    public static void getCards()
    {


        progressDialog.setTitle("Cards Info");
        progressDialog.setMessage("loading cards...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        // populate the list from parse server
        ParseQuery<ParseObject> cardsQuery = ParseQuery.getQuery("Card");
        cardsQuery.whereEqualTo("branch" , bName);
        cardsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                cardsList.clear();
                if(e == null && objects.size() > 0)
                {
                    progressDialog.cancel();
                    for(ParseObject object : objects)
                    {
                        cardsList.add(new CardModel("ID: "+object.getObjectId() , "status: "+object.getString("status") ,object.getString("Code")));
                    }
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    progressDialog.cancel();
                    Toast.makeText(context, "failed to load cards", Toast.LENGTH_SHORT).show();
                    // maybe implement an alert dialog to retry
                }
            }
        });
    }
    public static void getUsers()
    {

        progressDialog2.setTitle("Users");
        progressDialog2.setMessage("loading users data...");
        progressDialog2.setCancelable(false);
        progressDialog2.show();
        // get the users after querying them from the parse server
        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereEqualTo("branch" , bName);
        userParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                usersList.clear();
                if( e == null && users.size() > 0)
                {
                    progressDialog2.cancel();
                    for(ParseUser user : users)
                    {
                        usersList.add(new UserModel(user.getUsername() , "" , user.getString("age") , user.getString("type") , user.getString("branch") , "ID: "+user.getObjectId()));
                    }
                    usersAdapter.notifyDataSetChanged();
                }
                else {
                    progressDialog2.cancel();
                    Toast.makeText(context, "failed to load users", Toast.LENGTH_SHORT).show();
                    // maybe implement an alert dialog to retry
                }
            }
        });
    }




    @Override
    public void apply(String name, String password, String age) {

        // the contents returned fromthe alert dialog fragment
        // flow of action inf the user selected the add option from the alert dialog
        final ParseUser parseUser = new ParseUser();
        parseUser.setUsername(name);
        parseUser.setPassword( password);
        parseUser.put("age" , age);
        parseUser.put("branch" , bName);
        parseUser.put("type" , "employee");
        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    getUsers();
                    Toast.makeText(BranchContentsActivity.this, "success", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(BranchContentsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
