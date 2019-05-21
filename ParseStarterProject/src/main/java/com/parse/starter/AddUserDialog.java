package com.parse.starter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddUserDialog extends AppCompatDialogFragment {

    // declare the views
    EditText nameTxt;
    EditText passTxt;
    EditText ageTxt;

    // declare the listener
    private AddDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_user_dialog , null);
        builder.setView(view)
                .setTitle("Add a new User")
                .setIcon(R.drawable.ic_person)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // pull the text out of the edit text
                        String name , password , age;
                        name = nameTxt.getText().toString();
                        password = passTxt.getText().toString();
                        age = ageTxt.getText().toString();
                        listener.apply(name , password , age);

                    }
                })
                .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        // init the views
        nameTxt = (EditText)view.findViewById(R.id.name_add_dialog);
        passTxt = (EditText)view.findViewById(R.id.pass_add_dialog);
        ageTxt = (EditText)view.findViewById(R.id.age_add_dialog);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try
        {
            listener = (AddDialogListener)context;
        }catch (Exception e ){
            e.printStackTrace();
        }

    }

    public interface AddDialogListener{
        void apply(String name , String password , String age );
    }
}
