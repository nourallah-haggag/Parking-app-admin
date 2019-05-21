package com.parse.starter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class RecyclerUsersAdapter extends RecyclerView.Adapter<RecyclerUsersAdapter.UsersHolder>{

    Context context;
    List<UserModel> usersList;


    // contsructor
    public RecyclerUsersAdapter(Context context , List<UserModel> usersList){

        this.context = context;
        this.usersList = usersList;

    }
    @Override
    public UsersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_users , parent ,  false);
        UsersHolder holder = new UsersHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(UsersHolder holder, int position) {

        final UserModel userModel = usersList.get(position);
        holder.userNameTxt.setText(userModel.name);
        holder.idText.setText(userModel.ID);
      /*  holder.deleteUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete user ?1");
                builder.setIcon(R.drawable.ic_delete_forever_black_24dp);
                builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
                        userParseQuery.whereEqualTo("username" , userModel.name);
                        userParseQuery.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> users, ParseException e) {
                                if( e== null && users.size()>0)
                                {
                                    for(ParseUser user : users)
                                    {
                                        user.deleteInBackground();
                                        BranchContentsActivity.getUsers();
                                        Toast.makeText(context, "user deleted successfully", Toast.LENGTH_SHORT).show();
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
        });*/




    }



    @Override
    public int getItemCount() {
        return usersList.size();
    }

    // view holder
    public class UsersHolder extends RecyclerView.ViewHolder{

        // declare the views
        TextView userNameTxt;
        TextView idText;


        public UsersHolder(View itemView) {
            super(itemView);

            // init the views
            userNameTxt = (TextView)itemView.findViewById(R.id.user_name_card);
            idText = (TextView)itemView.findViewById(R.id.user_id);

        }
    }
}
