package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.mail.GMailSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * Esta classe é o inicio da app
 * É aqui feito o login
 * é aqui feito o "esqueceu-se da password?" e para isso é feito um alertDialog onde se retira o email do
 * cliente com a interface implementada ForgotPassDialog.ForgotPassDialogListener
  * O login é feito com o volley*/
public class Inicio extends AppCompatActivity implements ForgotPassDialog.ForgotPassDialogListener{

    EditText email;
    String forgot_email="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/forgot_email";
    String updatePass="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/updatePass";
    String login="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
      email= (EditText) findViewById(R.id.insereEmail);
        mostraPass(); //mostra a password escrita

        TextView textView_forgotPass = (TextView) findViewById(R.id.textViewforgot_password);

        //deteta o clique no esqueceu-se password e abre o AlertDialog
        textView_forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

            }
        });

    }

    //abre alertDialog do "esqueceu-se da palavra passe"
    public void openDialog(){

        ForgotPassDialog forgotPassDialog = new ForgotPassDialog();
        forgotPassDialog.show(getSupportFragmentManager(), "forgotPass dialog");

    }


    //verifica se os dados inseridos são válidos na base de dados
    //se forem é alterada a password e enviado um  email ao email inserido no alertDialog
    // com a nova password
    @Override
    public void applyTexts(final String email, final  String telefone) {

        showAlert("A processar...");

        //vai verificar se o email existe, se existir muda-lhe a pass e envia um email para o cliente a dizer qual a nova password
        RequestQueue queue = Volley.newRequestQueue(Inicio.this);
        String url =  forgot_email;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("APPLOG", response);

                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(response);

                            if(jsonObject.get("ENVIA").equals("OK")){
//encontrou o cliente e mudou a password
                                showAlert("Email enviado para "+jsonObject.get("EMAIL").toString());


                                final String novaPass=buttonSend_onClick(""+jsonObject.get("EMAIL"));

                                RequestQueue queue = Volley.newRequestQueue(Inicio.this);
                                String url =  updatePass;

                                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String response)
                                            {
                                                Log.d("APPLOG", response);

                                                JSONObject jsonObject = null;

                                                try {
                                                    jsonObject = new JSONObject(response);


                                                }catch (JSONException e){

                                                }


                                            }

                                        },
                                        new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Log.d("APPLOG", error.toString());
                                                showAlert("Sem conexão!");


                                            }
                                        }
                                ){
                                    @Override
                                    protected Map<String,String> getParams()
                                    {
//parametros usados no ficheiro php
                                        Map<String,String> params = new HashMap<String,String>();
                                        params.put("post_pass", novaPass.trim());
                                        params.put("post_email", email.trim());
                                        return params;
                                    }

                                };
                                queue.add(postRequest);


                            }
                            else{

                                showAlert("Email inválido");
                            }

                        }catch (JSONException e){

                        }


                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("APPLOG", error.toString());
                        showAlert("Sem conexão!");

                    }
                }
        ){
            @Override
            protected Map<String,String> getParams()
            {
//parametros usados no ficheiro php
                Map<String,String> params = new HashMap<String,String>();
                params.put("post_email", email.trim());
                params.put("post_telefone", telefone.trim());
                return params;
            }

        };
        queue.add(postRequest);

    }

//gera a nova password
    public  String  geraPalavrapass(int n){

// chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    //envia email
    private  String  buttonSend_onClick(String email){
        String novaPass=geraPalavrapass(10);

        try{

            //corpo do email enviado para o cliente
            String subject = "Recuperação Palavra-passe";
            String body = "Utilize esta nova palavra-passe para aceder à sua conta, aconselhamos que a altere nas definições<br>" +
                    "Palavra-passe nova: "+novaPass;
            String name = "Bizdirect Tecnical SuportTeam<br>";
            String phone = "Contacto para informações adicionais:<br>Telefone: +351 210 100 520<br>Email: contact@bizdirect.pt";
            String address = "Lisboa: Rua Viriato, 13 1050-233 Lisboa<br> Porto: Lugar do Espido, Via Norte 4470-177 Maia<br>" +
                    "Viseu: Bizdirect Competence Center Campus Politécnico 3504-510 Viseu<br>";
            String toAddress = email;
            String fromAddress="contact@bizdirect.pt";
            String content = "Nome: " + name;
            content += "<br>Moradas:<br> " + address;
            content += "<br>" + phone;
            content += "<br><br>Informação: " + body;


            boolean result = new SendMailAssync().execute(fromAddress, toAddress, subject,
                    content).get();


        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }

        return novaPass;

    }

    private class SendMailAssync extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                //este email é um email provisório só para mostrar que funciona
                String fromAdress = strings[0];
                String toAddress = strings[1];
                String subject = strings[2];
                String body = strings[3];
                GMailSender gMailSender = new GMailSender("nelson.andrade98@gmail.com",
                        "Leitinho1.");
                gMailSender.sendMail(subject,body,fromAdress,toAddress);
                return true;

            }catch (Exception e){
                return false;

            }
        }
    }

    //mostra a password inserida
    private void mostraPass() {

        final EditText password = findViewById(R.id.inserePassword);
        final CheckBox showPassword = findViewById(R.id.showPass);
        Button registo = findViewById(R.id.button_registo);


        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        registo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), registo.class);
                startActivity(intent);

            }
        });
    }

//verifica os dados inseridos e se forem válidos, faz login/ entra na app
    public void fazLogin(View view) {

        showAlert("A processar...");

        final EditText pass = (EditText) findViewById(R.id.inserePassword);

        //testa se os dados são válidos
        if(email.getText().toString().trim().isEmpty() || pass.getText().toString().trim().isEmpty()){
            showAlert("Impossível fazer login, verifique os dados inseridos!");
            return;

        }

//é utilizado o volley
        RequestQueue queue = Volley.newRequestQueue(Inicio.this);
        String url =  login;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APPLOG", response);
                        parseJson(response);
                        //processa-se o Json
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("APPLOG", error.toString());
                        showAlert("Sem conexão!");


                    }
                }
        ){
            @Override
            protected Map<String,String> getParams()
            {
//parametros usados no ficheiro php
                Map<String,String> params = new HashMap<String,String>();
                params.put("post_email", email.getText().toString().trim());
                params.put("post_password", pass.getText().toString().trim());
                return params;
            }

        };
        queue.add(postRequest);
    }


//processa o Json
    public  void parseJson(String jsonStr){

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonStr);

            if(jsonObject.get("LOGIN").equals("OK")){
//se foi encontrada a conta, "entra" na app
                Toast.makeText(this,"Bem-vindo "+jsonObject.get("USER")+"!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, homePage.class ).putExtra("email",email.getText().toString().trim()));

                finish();
            }
            else{
//não foi encontrada a conta
                Toast.makeText(this,"Impossível fazer login, verifique os dados inseridos!",Toast.LENGTH_LONG).show();
            }

        }catch (JSONException e){

        }
    }


// toast messafe
    private void showAlert(String msg) {
        Toast.makeText(Inicio.this, msg,
                Toast.LENGTH_LONG).show();
    }
}
