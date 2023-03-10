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
 * Na classe "empresaFidelizada" existe uma opção para enviar um email a um amigo a recomendar a empresa fidelizada
 * Esta classe cria esse alertDialog  onde retira o "valor" do email para poder enviar o email
 * Caso esta implemente a interface " recomendaListener"*/

public class recomenda extends AppCompatDialogFragment {

    //Variáveis
    private EditText email ;
    private  recomendaListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.recomenda, null);

        builder.setView(view)
                //titulo
                .setTitle(Html.fromHtml("<font color='#E84424'>Recomendar a um amigo:</font>"))
                //buttons
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("RECOMENDAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email_novo = email.getText().toString();
                        listener.enviaEmail(email_novo);



                    }
                });

        email = view.findViewById(R.id.edit_envia_email);

        return  builder.create();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (recomendaListener) context;
        } catch (ClassCastException e) {
            throw  new ClassCastException(context.toString() + "must implement Altera_emailListener" );
        }

    }

    //envia email
    public  interface  recomendaListener{
        void enviaEmail(String novo);

    }
}