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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;
/*
 * Na classe "definicoes" existe uma opção para mudar a password
 * Ao clicar nessa opção (button) aparece um alert dialog para apagar a conta e outros parâmetros de confirmação
 * Esta classe cria esse alertDialog e cria o getApagar() que envia todos os parâmetros do alertDialog para a classe "definicoes"
 * Caso esta implemente a interface " Apaga_contaListener"*/

public class Apaga_conta extends AppCompatDialogFragment {

    //variaveis
    private EditText edit_conta_email, edit_conta_pass;
    private Apaga_contaListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.apaga_conta, null);

        builder.setView(view)
                //titulo
                .setTitle(Html.fromHtml("<font color='#E84424'>Apagar Conta:</font>"))
                //buttons
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ALTERAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String conta_email = edit_conta_email.getText().toString().trim();
                        String conta_pass = edit_conta_pass.getText().toString().trim();
                        listener.getApagar(conta_email, conta_pass);



                    }
                });

        edit_conta_email = view.findViewById(R.id.edit_conta_email);
        edit_conta_pass = view.findViewById(R.id.edit_conta_pass);

        return  builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (Apaga_contaListener) context;
        } catch (ClassCastException e) {
            throw  new ClassCastException(context.toString() + "must implement ApagacontaListener" );
        }

    }

    //envia parametros
    public  interface  Apaga_contaListener{
        void getApagar(String conta_email, String conta_pass);

    }

}
