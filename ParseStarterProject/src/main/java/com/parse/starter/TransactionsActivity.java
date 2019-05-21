package com.parse.starter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.Transaction;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    // declare the list components
    List<TransactionModel> transactionModelList;
    RecyclerView recyclerView;
    RecyclerTransactionsAdapter adapter;

    // floating button for date picker
    //FloatingActionButton floatingActionButton;
    FloatingActionButton searchFloating;

    Calendar calendar;
    DatePickerDialog datePickerDialog;

    // search bar for cards
    EditText editText;

    // image not found
    ImageView imageView;

    // views for total time and cash
    TextView totalTime;
    TextView totalCash;
    double totalCashValue=0;
    ImageView billImage;
    TextView dateTxt;
    TextView userTxt;

    // for the filter
    String date;

    // spinner array -- data in dropdown
    List<String> userList;
    String selectedUser;

    boolean clickedonce;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        // init  the total cash and time views
       // totalTime = (TextView)findViewById(R.id.total_time);
        totalCash = (TextView)findViewById(R.id.total_cash);
        billImage = (ImageView)findViewById(R.id.bill_image);
        dateTxt = (TextView)findViewById(R.id.date_txt) ;
        userTxt = (TextView)findViewById(R.id.user_txt) ;
        dateTxt.setText("Date: All time");
        userTxt.setText("Employee: All employees");
        clickedonce = false;

        // hide the keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE);

        // start the progress
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("getting transactions...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // get the branch name
        String branch = getIntent().getStringExtra("branch");

        // specify the default date
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat dayformat = new SimpleDateFormat("dd/MM/yy");
        String day = dayformat.format(calendar1.getTime());
        date = day;


        // init the list components
        transactionModelList = new ArrayList<>();
        // holds only the users for the spinner
        userList = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.recycler_transactions);
        adapter = new RecyclerTransactionsAdapter(this , transactionModelList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // populate the list
        final ParseQuery<ParseObject> transQuery = ParseQuery.getQuery("Trans");
        transQuery.whereEqualTo("branch" , branch);
        transQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if( e==null && objects.size()>0)

                {

                    for(ParseObject object : objects)
                    {
                        transactionModelList.add(new TransactionModel(object.getString("branch") , object.getString("code") , object.getString("start") , object.getString("end") , object.getString("amount") , object.getString("day") , object.getString("user")));
                        // calculate the total cash
                      //  String cash = object.getString("amount").replace("EGP" , "");
                     //   totalCashValue+=Double.parseDouble(cash);

                    }
                    //reverse array lost to get the most recent at the top
                    Collections.reverse(transactionModelList);
                  //  totalCash.setText("Total"+totalCashValue+" EGP");
                    adapter.notifyDataSetChanged();
                    getTotal(transactionModelList);
                    progressDialog.cancel();
                }
                else
                {
                    progressDialog.cancel();
                    Toast.makeText(TransactionsActivity.this, "failed to load transactions , check connection and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // populate the user list // get all the users
        final ProgressDialog progressDialog1 = new ProgressDialog(this);
        progressDialog1.setTitle("Loading");
        progressDialog1.setMessage("Loading user names...");
        progressDialog1.setCancelable(false);
        progressDialog1.show();
        // get the users from parse
        ParseQuery<ParseUser> usersQuery = ParseQuery.getUserQuery();
        usersQuery.whereEqualTo("branch" , branch);
        usersQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                progressDialog1.cancel();
                if(e == null)
                {
                    if(objects.size()>0) {
                        searchFloating.setVisibility(View.VISIBLE);

                        for (ParseUser user : objects) {
                            userList.add(user.getUsername());
                        }
                    }else
                    {
                        // hide the floating button to avoid error
                        searchFloating.setVisibility(View.INVISIBLE);
                    }
                }

            }
        });




        //floating button action --> pick a date
       /* floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingActionButton3);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(TransactionsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        String yearZ = String.valueOf(year).substring(2);

                        if(day<10 && month<10)
                        {
                            date = 0+""+day+"/0"+(month+1)+"/"+yearZ;
                            Toast.makeText(TransactionsActivity.this, ""+date, Toast.LENGTH_SHORT).show();
                           // filterByDate(date);

                        }
                        else if(day<10 && month>10) {


                            date = 0+""+day + "/" + (month + 1) + "/" + yearZ;
                            Toast.makeText(TransactionsActivity.this, "" + date, Toast.LENGTH_SHORT).show();
                            //filterByDate(date);
                        }
                        else if ( day>10 && month<10)
                        {
                            date = day + "/0" + (month + 1) + "/" + yearZ;
                            Toast.makeText(TransactionsActivity.this, "" + date, Toast.LENGTH_SHORT).show();
                            //filterByDate(date);

                        }
                        else
                        {
                            date = +day + "/" + (month + 1) + "/" + yearZ;
                            Toast.makeText(TransactionsActivity.this, "" + date, Toast.LENGTH_SHORT).show();
                           // filterByDate(date);
                        }



                    }
                } , year , month , day);
                datePickerDialog.show();

            }
        });/*

        editText = (EditText)findViewById(R.id.editText4);
        /*editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });*/

       searchFloating = (FloatingActionButton)findViewById(R.id.search_floating);


       searchFloating.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               // reset the date
               date = "";
               View v = LayoutInflater.from(TransactionsActivity.this).inflate(R.layout.search_view , null);
               // views in teh search dialog
               final EditText searchEditText = (EditText)v.findViewById(R.id.search_txt);
               final Spinner employeeSpinner = (Spinner)v.findViewById(R.id.spinner_search_view);
               // setup the spinner drop down data
               ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                       TransactionsActivity.this, android.R.layout.simple_spinner_item, userList);

               adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               clickedonce = true;
               if(clickedonce)
               {
                   adapter.remove("All");
               }
               adapter.insert("All" , 0);
               employeeSpinner.setAdapter(adapter);
               // get the spinner selectiion



               Button dateButton = (Button)v.findViewById(R.id.date_btn);
               dateButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       calendar = Calendar.getInstance();
                       int day = calendar.get(Calendar.DAY_OF_MONTH);
                       int month = calendar.get(Calendar.MONTH);
                       int year = calendar.get(Calendar.YEAR);
                       datePickerDialog = new DatePickerDialog(TransactionsActivity.this, new DatePickerDialog.OnDateSetListener() {
                           @Override
                           public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                               String yearZ = String.valueOf(year).substring(2);

                               if(day<10 && month<10)
                               {
                                   date = 0+""+day+"/0"+(month+1)+"/"+yearZ;
                                   Toast.makeText(TransactionsActivity.this, ""+date, Toast.LENGTH_SHORT).show();
                                   // filterByDate(date);

                               }
                               else if(day<10 && month>10) {


                                   date = 0+""+day + "/" + (month + 1) + "/" + yearZ;
                                   Toast.makeText(TransactionsActivity.this, "" + date, Toast.LENGTH_SHORT).show();
                                   //filterByDate(date);
                               }
                               else if ( day>10 && month<10)
                               {
                                   date = day + "/0" + (month + 1) + "/" + yearZ;
                                   Toast.makeText(TransactionsActivity.this, "" + date, Toast.LENGTH_SHORT).show();
                                   //filterByDate(date);

                               }
                               else
                               {
                                   date = +day + "/" + (month + 1) + "/" + yearZ;
                                   Toast.makeText(TransactionsActivity.this, "" + date, Toast.LENGTH_SHORT).show();
                                   // filterByDate(date);
                               }



                           }
                       } , year , month , day);
                       datePickerDialog.show();

                   }
               });
               AlertDialog.Builder builder = new AlertDialog.Builder(TransactionsActivity.this);
               builder.setTitle("Search");
               builder.setIcon(R.drawable.ic_search_ora_24dp);
               builder.setMessage("choose search criteria");
               builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                      // Toast.makeText(TransactionsActivity.this, ""+date+""+selectedUser, Toast.LENGTH_SHORT).show();
                       // initiate search
                       if(date.equals("") || searchEditText.getText().toString().equals(""))
                       {
                           Toast.makeText(TransactionsActivity.this, "missing date or card code", Toast.LENGTH_SHORT).show();
                       }
                       else {

                           selectedUser = employeeSpinner.getSelectedItem().toString();
                           filter(searchEditText.getText().toString(), date, selectedUser);
                           Log.i("result", searchEditText.getText().toString());
                           Log.i("result", date);
                           Log.i("result", selectedUser);
                       }
                       }


               });
               builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                   }
               });

               builder.setView(v);
               builder.show();

           }
       });



    }

    // search filter
    private void filter(String text , String dateF , String userF) {
        //new array list that will hold the filtered data
        List<TransactionModel> filterdNames = new ArrayList<>();
        imageView.setVisibility(View.INVISIBLE);

        //looping through existing elements
        if(userF.equals("All"))
        {
            for (TransactionModel s : transactionModelList) {
                //if the existing elements contains the search input
                              if ((s.code.toLowerCase().contains(text.toLowerCase())) && (s.day.toLowerCase().contains(dateF.toLowerCase()))) {
                    imageView.setVisibility(View.INVISIBLE);
                    dateTxt.setText("Date: "+dateF);
                    userTxt.setText("Emoloyee: All emolyees");
                    //adding the element to filtered list
                    filterdNames.add(s);
                }

            }

        }
        else {
            for (TransactionModel s : transactionModelList) {
                //if the existing elements contains the search input
                if ((s.code.toLowerCase().contains(text.toLowerCase())) && (s.day.toLowerCase().contains(dateF.toLowerCase())) && (s.user.toLowerCase().contains(userF)) ) {
                    imageView.setVisibility(View.INVISIBLE);
                    dateTxt.setText("Date: "+dateF);
                    userTxt.setText("Emoloyee: "+userF);
                    //adding the element to filtered list
                    filterdNames.add(s);
                }

            }

        }

                    //adding the element
            /*else if(dateF.equals(""))
        {
            for (TransactionModel s : transactionModelList) {
                //if the existing elements contains the search input
                if ((s.code.toLowerCase().contains(text.toLowerCase())) && (s.user.toLowerCase().contains(userF)) ) {
                    imageView.setVisibility(View.INVISIBLE);
                    //adding the element to filtered list
                    filterdNames.add(s);
                }

            }

        }
        else if(userF.equals("All"))
        {
            for (TransactionModel s : transactionModelList) {
                //if the existing elements contains the search input
                if ((s.code.toLowerCase().contains(text.toLowerCase())) && (s.day.toLowerCase().contains(dateF.toLowerCase()))) {
                    imageView.setVisibility(View.INVISIBLE);
                    //adding the element to filtered list
                    filterdNames.add(s);
                }

            }
        }
        else if(userF.equals("All") && dateF.equals("") && text.equals(""))
        {
            for (TransactionModel s : transactionModelList) {
                //if the existing elements contains the search input

                    imageView.setVisibility(View.INVISIBLE);
                    //adding the element to filtered list
                    filterdNames.add(s);


            }

        }
        else {
            for (TransactionModel s : transactionModelList) {
                //if the existing elements contains the search input
                if ((s.code.toLowerCase().contains(text.toLowerCase())) && (s.day.toLowerCase().contains(dateF.toLowerCase())) && (s.user.toLowerCase().contains(userF)) ) {
                    imageView.setVisibility(View.INVISIBLE);
                    //adding the element to filtered list
                    filterdNames.add(s);
                }

            }
        }*/

        if(filterdNames.size()==0)
        {
            imageView.setVisibility(View.VISIBLE);
            getTotal(filterdNames);
        }

        //calling a method of the adapter class and passing the filtered list
        getTotal(filterdNames);
        adapter.filterList(filterdNames);
    }

    // filter by date
    /*public void filterByDate(String date)
    {
        ArrayList<TransactionModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        imageView.setVisibility(View.INVISIBLE);
        for (TransactionModel s : transactionModelList) {
            //if the existing elements contains the search input
            if (s.day.toLowerCase().contains(date.toLowerCase())) {
                imageView.setVisibility(View.INVISIBLE);
                //adding the element to filtered list
                filterdNames.add(s);
            }

        }
        if(filterdNames.size()==0)
        {
            imageView.setVisibility(View.VISIBLE);
            getTotal(filterdNames);
        }

        //calling a method of the adapter class and passing the filtered list
        getTotal(filterdNames);
        adapter.filterListByDate(filterdNames);

    }*/

    public void getCards()
    {


    }


    public void getTotal(List<TransactionModel> transactionList)
    {
        totalCashValue = 0.0;

        for(TransactionModel transaction : transactionList)
        {

            // calculation of total cash
            String cash = transaction.amount.replace("KWD" , "");
            totalCashValue+=Double.parseDouble(cash);
            // calculation of total time
            String time = transaction.day;
        }
        final Animation myAnim = AnimationUtils.loadAnimation(TransactionsActivity.this, R.anim.bounce);
        billImage.startAnimation(myAnim);
        totalCash.setText("Total revenue: "+totalCashValue+" KWD");

    }


    /*public void onSearch(View v)
    {
        filter(editText.getText().toString());
       // filterByDate(date);

    }

    public void onReset(View v)
    {

    }*/

    public void onRefresh(View v)
    {

        // restart the activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }




}
