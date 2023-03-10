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
 * Na classe "Inicio" existe uma opção caso se esqueça da password
 * Ao clicar nessa opção (TextView) aparece um alert dialog para escrever o email
 * Esta classe cria esse alertDialog e cria o applyTexts() que envia o email escrito para a classe "Inicio"
 * Caso esta implemente a interface " ForgotPassDialogListener"*/

public class ForgotPassDialog extends AppCompatDialogFragment {
    //variaveis
    private EditText editTextEmail;
    private EditText editTexTelefone;
    private  ForgotPassDialogListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.forgot_password, null);

        builder.setView(view)
                //titulo
                .setTitle(Html.fromHtml("<font color='#E84424'>Recuperar Password:</font>"))
                //buttons
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = editTextEmail.getText().toString();
                        String telefone = editTexTelefone.getText().toString();
                        listener.applyTexts(email, telefone);



                    }
                });

        editTextEmail = view.findViewById(R.id.edit_forgotpass);
        editTexTelefone = view.findViewById(R.id.edit_telefone);

        return  builder.create();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ForgotPassDialogListener) context;
        } catch (ClassCastException e) {
            throw  new ClassCastException(context.toString() + "must implement ForgotDialogListenr" );
        }

    }

    public  interface  ForgotPassDialogListener{
        void applyTexts(String email, String telefone);

    }
}
