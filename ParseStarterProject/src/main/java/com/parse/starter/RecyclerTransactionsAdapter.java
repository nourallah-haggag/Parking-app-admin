package com.parse.starter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerTransactionsAdapter extends RecyclerView.Adapter<RecyclerTransactionsAdapter.TransHolder> {

    //This method will filter the list
    //here we are passing the filtered data
    //and assigning it to the list with notifydatasetchanged method
    public void filterList(List<TransactionModel> filterdNames) {
        this.transList = filterdNames;
        notifyDataSetChanged();
    }
   /* public void filterListByDate(ArrayList<TransactionModel> filterdNames) {
        this.transList = filterdNames;
        notifyDataSetChanged();
    }*/

    // the context and the list from the calling activity
    Context context;
    List<TransactionModel> transList;

    // constructor
    public RecyclerTransactionsAdapter(Context context , List<TransactionModel> transLIst)
    {
        this.context = context;
        this.transList = transLIst;
    }
    @Override
    public TransHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_transactions , parent ,  false);
       TransHolder holder = new TransHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(TransHolder holder, int position) {

        // bind data from the list to the views
        TransactionModel transactionModel = transList.get(position);
        holder.code.setText("Card: "+transactionModel.code);
        holder.start.setText("Start: "+transactionModel.start);
        holder.end.setText("End: "+transactionModel.end);
        holder.amount.setText("Amount Paid: "+transactionModel.amount);
        holder.day.setText("Date: "+transactionModel.day);
        holder.user.setText("Employee: "+transactionModel.user);


    }



    @Override
    public int getItemCount() {
        return transList.size();
    }

    // declare the view holder class
    public class TransHolder extends RecyclerView.ViewHolder{

        // declare views
        TextView code;
        TextView start;
        TextView end;
        TextView amount;
        TextView day;
        TextView user;

        public TransHolder(View itemView) {
            super(itemView);

            code = (TextView)itemView.findViewById(R.id.card_code_trans_txt);
            start = (TextView)itemView.findViewById(R.id.start_time_trans_txt);
            end = (TextView)itemView.findViewById(R.id.end_time_trans_txt);
            amount = (TextView)itemView.findViewById(R.id.amount_paid_trans_txt);
            day = (TextView)itemView.findViewById(R.id.day_trans_txt);
            user = (TextView)itemView.findViewById(R.id.user_trans);
        }
    }
}
