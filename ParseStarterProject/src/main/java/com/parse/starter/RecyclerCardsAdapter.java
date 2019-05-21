package com.parse.starter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class RecyclerCardsAdapter extends RecyclerView.Adapter<RecyclerCardsAdapter.CradsHolder> {

    Context context;
    List<CardModel> cardsList;

    // constructor
    public RecyclerCardsAdapter(Context context , List<CardModel> cardsList)
    {
        this.context = context;
        this.cardsList = cardsList;
    }
    @Override
    public CradsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_cards , parent ,  false);
        CradsHolder holder = new CradsHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(CradsHolder holder, int position) {
        final CardModel card = cardsList.get(position);
        holder.idCard.setText(card.code);
        holder.statusCard.setText(card.status);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit card code - enter new code");
                builder.setIcon(R.drawable.ic_edit_black_24dp);
                final EditText editText = new EditText(context);
                builder.setView(editText);
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ParseQuery<ParseObject> cardsQuery = ParseQuery.getQuery("Card");
                        cardsQuery.whereEqualTo("Code" , card.code);
                        cardsQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if ( e == null && objects.size()>0)
                                {
                                    for(ParseObject object : objects)
                                    {

                                        object.put("Code" , editText.getText().toString());
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if( e == null)
                                                {
                                                    BranchContentsActivity.getCards();
                                                    Toast.makeText(context, "card edited successfully", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });


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
        });
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               // Toast.makeText(context, ""+card.code, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to delete card :"+card.code);
                builder.setIcon(R.drawable.ic_delete_forever_black_24dp);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setTitle("Deleting");
                        progressDialog.setMessage("Card data erasing in progress...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        // query on the list item from the parse server and delete it
                        ParseQuery<ParseObject> cardsQuery = ParseQuery.getQuery("Card");
                                cardsQuery.whereEqualTo("Code" , card.code);
                                cardsQuery.setLimit(1);
                                cardsQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        progressDialog.cancel();
                                        if ( e == null && objects.size()>0)
                                        {
                                            for(ParseObject object : objects)
                                            {

                                                try {
                                                    object.delete();
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }


                                            }
                                            BranchContentsActivity.getCards();
                                            Toast.makeText(context, "card deleted successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(context, "failed, please try again", Toast.LENGTH_SHORT).show();
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
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    public class CradsHolder extends RecyclerView.ViewHolder
    {

        // declare the views
        TextView idCard;
        TextView statusCard;
        TextView edit;
        View v ;

        public CradsHolder(View itemView) {
            super(itemView);


            // init the views
            v = itemView;
            idCard = (TextView)itemView.findViewById(R.id.ID_card);
            statusCard = (TextView)itemView.findViewById(R.id.status_card);
            edit = (TextView)itemView.findViewById(R.id.edit_toggle_card);
        }
    }
}
