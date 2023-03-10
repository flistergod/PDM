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
 * Na classe "definicoes" existe uma opção para mudar a password
 * Ao clicar nessa opção (button) aparece um alert dialog para escrever a password e outros parâmetros de confirmação
 * Esta classe cria esse alertDialog e cria o getPass() que envia todos os parâmetros do alertDialog para a classe "definicoes"
 * Caso esta implemente a interface " Altera_passListener"*/

public class Altera_pass extends AppCompatDialogFragment {

    //Variaveis
    private EditText editPass_nova, editPass_nova_rep, editPass_atual;
    private Altera_passListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.altera_pass, null);

        builder.setView(view)
                //titulo
                .setTitle(Html.fromHtml("<font color='#E84424'>Alterar Password:</font>"))
                //buttons
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ALTERAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pass_atual = editPass_atual.getText().toString().trim();
                        String pass_nova = editPass_nova.getText().toString().trim();
                        String pass_nova_rep = editPass_nova_rep.getText().toString().trim();
                        listener.getPass(pass_atual, pass_nova, pass_nova_rep);



                    }
                });

        editPass_atual = view.findViewById(R.id.edit_pass_atual);
        editPass_nova = view.findViewById(R.id.edit_nova_pass);
        editPass_nova_rep = view.findViewById(R.id.edit_nova_pass_rep);


        return  builder.create();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (Altera_passListener) context;
        } catch (ClassCastException e) {
            throw  new ClassCastException(context.toString() + "must implement Altera_emailListener" );
        }

    }
//envia pass
    public  interface  Altera_passListener{
        void getPass(String pass_atual, String pass_nova, String pass_nova_rep);

    }
}

