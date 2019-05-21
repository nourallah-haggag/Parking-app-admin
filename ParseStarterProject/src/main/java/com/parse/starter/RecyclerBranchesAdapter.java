package com.parse.starter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class RecyclerBranchesAdapter extends RecyclerView.Adapter<RecyclerBranchesAdapter.branchesHolder> {

    List<BranchModel> branchesList;
    Context context;

    // constructor
    public RecyclerBranchesAdapter(Context context , List<BranchModel> branchesList)
    {
        this.context = context;
        this.branchesList = branchesList;
    }

    @Override
    public branchesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_branches , parent ,  false);
        branchesHolder holder = new branchesHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(branchesHolder holder, int position) {

        final BranchModel branch = branchesList.get(position);
        holder.branchName.setText(branch.branchName);
        holder.branchCode.setText(branch.branchCode);
        holder.dateCreated.setText(branch.dateCreated);
        // set the click listener on the holder view
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move to the branch contents activity and pass the branch attributes
                Intent intent = new Intent(context , BranchContentsActivity.class);
                intent.putExtra("branchName" , branch.branchName);
                intent.putExtra("branchCode" , branch.branchCode);
                intent.putExtra("dateCreated" , branch.dateCreated);
                view.getContext().startActivity(intent);
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to delete the whole branch ?");
                builder.setIcon(R.drawable.ic_delete_forever_black_24dp
                );
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // set the progress for deleting the branch
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setTitle("Delete");
                        progressDialog.setMessage("Deleting Branch");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        // delete the branch object
                        ParseQuery<ParseObject> branchQuery = ParseQuery.getQuery("Branch");
                        branchQuery.whereEqualTo("name" , branch.branchName);
                        branchQuery.setLimit(1);
                        branchQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                progressDialog.cancel();
                                if( e == null && objects.size()>0)
                                {
                                    for(ParseObject object : objects)
                                    {
                                        // delete the branch

                                            object.deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if( e == null)
                                                    {
                                                        // branch deleted successfully
                                                        progressDialog.cancel();

                                                        // progress dialog for deleting cards
                                                        final ProgressDialog progressDialog1 = new ProgressDialog(context);
                                                        progressDialog1.setTitle("Delete");
                                                        progressDialog1.setMessage("Deleting cards in branch "+branch.branchName);
                                                        progressDialog1.setCancelable(false);
                                                        progressDialog1.show();

                                                        // delete the cards associated with the branch
                                                        ParseQuery<ParseObject> cardsQuery = ParseQuery.getQuery("Card");
                                                        cardsQuery.whereEqualTo("branch" , branch.branchName);
                                                        cardsQuery.findInBackground(new FindCallback<ParseObject>() {
                                                            @Override
                                                            public void done(List<ParseObject> objects, ParseException e) {
                                                                progressDialog1.cancel();
                                                                if(objects.size()>0 && e == null)
                                                                {
                                                                    for(final ParseObject object : objects)
                                                                    {
                                                                        // delete the branch

                                                                            object.deleteInBackground(new DeleteCallback() {
                                                                                @Override
                                                                                public void done(ParseException e) {
                                                                                    if(e==null)
                                                                                    {
                                                                                        Toast.makeText(context, "Card "+ object.getString("Code") +" in branch deleted successfully", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        Toast.makeText(context, "cards will be deleted when connection is restored", Toast.LENGTH_SHORT).show();
                                                                                        object.deleteEventually();
                                                                                    }
                                                                                }
                                                                            });

                                                                    }
                                                                  //  progressDialog1.cancel();
                                                                }
                                                                else {
                                                                   // progressDialog1.cancel();
                                                                  //  Toast.makeText(context, "failed to delete cards", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                    }

                                                }
                                            });


                                        HomeActivity.getBranches();
                                        // restart the activity
                                     /*   Intent intent = new Intent(HomeActivity.context , HomeActivity.class);
                                        ((Activity)context).finish();
                                        context.startActivity(intent);*/
                                        Toast.makeText(context, "branch deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
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

            // delete the users belonging to the branch
            // delete the cards belonging to the branch

        });



    }


    @Override
    public int getItemCount() {
        return branchesList.size();
    }

    public class branchesHolder extends RecyclerView.ViewHolder
    {

        // declare views
        TextView branchName;
        TextView branchCode;
        TextView dateCreated;
        Button deleteBtn;
        View v;

        public branchesHolder(View itemView) {
            super(itemView);
            v = itemView;
            branchName = (TextView)itemView.findViewById(R.id.branch_row_name_txt);
            branchCode = (TextView)itemView.findViewById(R.id.branch_row_code_txt);
            dateCreated = (TextView)itemView.findViewById(R.id.date_created_branch);
            deleteBtn = (Button)itemView.findViewById(R.id.delete_branch_btn);
        }
    }
}
