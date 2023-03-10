package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;


/*
* Na classe "definicoes" existe uma opção para mudar o email
* Ao clicar nessa opção (button) aparece um alert dialog para escrever o email
* Esta classe cria esse alertDialog e cria o getEmails() que envia o email escrito para a classe "definicoes"
* Caso esta implemente a interface " Altera_emailListener"*/

public class Altera_email extends AppCompatDialogFragment {

    //Variáveis
    private EditText editEmail_novo ;
    private  Altera_emailListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.altera_email, null);

        builder.setView(view)
                //titulo
                .setTitle(Html.fromHtml("<font color='#E84424'>Alterar Email:</font>"))
                //buttons
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ALTERAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email_novo = editEmail_novo.getText().toString().trim();
                        listener.getEmails(email_novo);



                    }
                });

        editEmail_novo = view.findViewById(R.id.edit_email_novo);

        return  builder.create();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (Altera_emailListener) context;
        } catch (ClassCastException e) {
            throw  new ClassCastException(context.toString() + "must implement Altera_emailListener" );
        }

    }

//envia email
    public  interface  Altera_emailListener{
        void getEmails(String novo);

    }
}