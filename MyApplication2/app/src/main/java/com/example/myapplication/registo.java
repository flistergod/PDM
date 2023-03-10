package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wafflecopter.charcounttextview.CharCountTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
/*Esta classe trata do registo de um cliente
*Verifica os dados inseridos do cliente
* Se forem válidos, usando o volley, manda esses dados para o ficheiro php e regista na base de dados*/
public class registo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //variaveis
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static  final int IMAGE_PICK_CODE=1000;
    private static  final int PERMISSON_CODE=1001;
    String registo="http://193.137.7.33/~estgv16061/app_php/register.php";

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registo);

        porContadores(); //coloca os contadores nas editTexts
        dataNascimento(); //trata do DatePciker da data de nacimento
        spinners(); //mete os items nos spinners
        //preferencias();
        foto_perfil(); //trata da foto escolhida da galeria do utilizador
        termos(); //mostra um alertDialog com os termos de utilização

        //caso o utilizador não escolha nenhuma foto
        Drawable drawable = this.getResources().getDrawable(R.drawable.profile);
        bitmap = ((BitmapDrawable) drawable).getBitmap();

    }

    private void porContadores() {

        //coloca counters nas editTexts

        EditText nome = (EditText) findViewById(R.id.editName);
        CharCountTextView nomeCounter = (CharCountTextView) findViewById(R.id.nomeCounter);
        EditText telefone = (EditText) findViewById(R.id.editTlf);
        CharCountTextView tlfCounter = (CharCountTextView) findViewById(R.id.tlfCounter);
        EditText email = (EditText) findViewById(R.id.editEmail);
        CharCountTextView emailCounter = (CharCountTextView) findViewById(R.id.emailCounter);
        EditText cc = (EditText) findViewById(R.id.editCC);
        CharCountTextView ccCounter = (CharCountTextView) findViewById(R.id.ccCounter);
        EditText nif = (EditText) findViewById(R.id.editNIF);
        CharCountTextView nifCounter = (CharCountTextView) findViewById(R.id.nifCounter);
        EditText morada = (EditText) findViewById(R.id.editMorada);
        CharCountTextView moradaCounter = (CharCountTextView) findViewById(R.id.moradaCounter);
        EditText password = (EditText) findViewById(R.id.editPassword);
        CharCountTextView passCounter = (CharCountTextView) findViewById(R.id.passCounter);

        counter(nomeCounter,nome);
        counter(tlfCounter,telefone);
        counter(emailCounter,email);
        counter(ccCounter,cc);
        counter(nifCounter,nif);
        counter(moradaCounter,morada);
        counter(passCounter,password);

    }

    private void counter(CharCountTextView charCountTextView, EditText editText) {


        charCountTextView.setEditText(editText);
        charCountTextView.setCharCountChangedListener(new CharCountTextView.CharCountChangedListener() {
            @Override
            public void onCountChanged(int i, boolean b) {

            }
        });
    }

    private void termos() {

        //cria um alertDialog e associa o layout dos termos e condiçoes
        final TextView showDialog = (TextView) findViewById(R.id.termosView);
        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(registo.this);
                View mView = getLayoutInflater().inflate(R.layout.termos_condicoes, null);
                mBuilder.setTitle(Html.fromHtml("<font color='#E84424'>Termos e Condições:</font>"));

                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                });


                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });
    }



    private void foto_perfil() {

        Button profileButton;

        //escolher imagem de perfil


    }

    private void pickImageFromGallery() {
        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //trata do resultado da permissão



    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {


        Uri path = data.getData();

        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);


        } catch (IOException e){
            e.printStackTrace();

        }
    }


    private void spinners() {

        //associa os item nos arrays de strings no strings.xml nos spinners:
        //1º - cria o spinner
        //2º cria o adapter com o array de string escolhido
        //3º associa o adapter ao spinner

        Spinner estadoCivil = findViewById(R.id.editEstadoCivil);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.estadoCivil,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estadoCivil.setAdapter(adapter);
        estadoCivil.setOnItemSelectedListener(this);


        Spinner genero = findViewById(R.id.editGenero);
        ArrayAdapter<CharSequence> adapter_genero = ArrayAdapter.createFromResource(this, R.array.genero,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genero.setAdapter(adapter_genero);
        genero.setOnItemSelectedListener(this);
    }

    private void dataNascimento() {

        //cria um alertDialog DatePicker em que a data máxima é a data atual -18 anos
        //é mudada a cor do datePicker
        //Atenção ---> a mudança de cor só funciona em API de 26 ou superior
        //caso tenha uma inferior meta em comentário a linha 272 até 295

        final EditText mDisplayDate = (EditText) findViewById(R.id.editDataNasc);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -18);

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog newFragment = new DatePickerDialog(registo.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                        year, month, day);

                // Divider changing:
                DatePicker dpView = newFragment.getDatePicker();
                LinearLayout llFirst = (LinearLayout) dpView.getChildAt(0);
                LinearLayout llSecond = (LinearLayout) llFirst.getChildAt(0);
                for (int i = 0; i < llSecond.getChildCount(); i++) {
                    NumberPicker picker = (NumberPicker) llSecond.getChildAt(i); // Numberpickers in llSecond
                    // reflection - picker.setDividerDrawable(divider); << didn't seem to work.
                    Field[] pickerFields = NumberPicker.class.getDeclaredFields();
                    for (Field pf : pickerFields) {
                        if (pf.getName().equals("mSelectionDivider")) {
                            pf.setAccessible(true);
                            try {
                                pf.set(picker, new ColorDrawable(getResources().getColor(R.color.orange)));
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }


                newFragment.getDatePicker().setMaxDate(System.currentTimeMillis() - 568032000000L); //mete a data à 18 anos atrás
                newFragment.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //mete o masaico tranparente
                newFragment.show();
                // Toast.makeText(this, "Invalid date, please try again", Toast.LENGTH_LONG).show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month++;
                Log.d("registo", "onDataSet: date: " + month + "/" + day + "/" + year);
                String date;
                if (month < 10) {
                    if (day < 10) {
                        date = "0" + day + "/" + "0" + month + "/" + year;

                    } else {
                        date = day + "/" + "0" + month + "/" + year;
                    }

                } else {
                    if (day < 10) {
                        date = "0" + day + "/" + month + "/" + year;

                    } else {
                        date = day + "/" + month + "/" + year;
                    }
                }

                mDisplayDate.setText(date);
            }
        };
    }


    //trata da imagem escolhida


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        finish();

    }


    public void continuar(View view) {

        //antes de registar, são validados os dados
        //verifica se as editTexts não estão vazias ou se têm lenghts obrigatórias
        //verifica se radiobuttons ou checkboxs têm condições e se cumprem essas condições
        //verifica se alguns dados cumprem os "PATTERNS"
        //se os dados cumprirem estas validações todas usa-se o volley para enviar os dados para o ficheiro de php
        //que registará este novo cliente com estes dados

        boolean temerro = false;
        boolean setError =false;
        final String nome = ((EditText) findViewById(R.id.editName)).getText().toString();
        final String telefone = ((EditText) findViewById(R.id.editTlf)).getText().toString();
        final String email = ((EditText) findViewById(R.id.editEmail)).getText().toString();
        final String cc = ((EditText) findViewById(R.id.editCC)).getText().toString();
        final String nif = ((EditText) findViewById(R.id.editNIF)).getText().toString();
        final String morada = ((EditText) findViewById(R.id.editMorada)).getText().toString();
        final String data_nasc = ((EditText) findViewById(R.id.editDataNasc)).getText().toString();
        final String password = ((EditText) findViewById(R.id.editPassword)).getText().toString();
        final CheckBox reg_terms = (CheckBox) findViewById(R.id.termos);
        RadioGroup radioGroup= (RadioGroup) findViewById(R.id.radio_animais);
        String radio="0";
        final RadioButton radioButton = (RadioButton) findViewById(R.id.radiosim);
        Spinner estadoCivil = (Spinner) findViewById(R.id.editEstadoCivil);
        final String ec = estadoCivil.getSelectedItem().toString();
        Spinner genero_spinner = (Spinner) findViewById(R.id.editGenero);
        final String genero = genero_spinner.getSelectedItem().toString();


        if (nome.isEmpty()) {
            temerro = true;
            setError =true;
        }

        if(!reg_terms.isChecked()){
            Toast.makeText(registo.this, "Tem de aceitar os termos e condições de uso da aplicação",
                    Toast.LENGTH_LONG).show();
            temerro = true;
        }



        if (Patterns.PHONE.matcher(telefone).matches() == false || telefone.length()!=9) {
            temerro = true;
            setError =true;

        }

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() == false) {
            setError =true;
            temerro = true;
        }
        if (cc.length()!=12) {
            setError =true;
            temerro = true;
        }
        if (nif.length()!=9) {
            setError =true;
            temerro = true;
        }
        if (morada.isEmpty()) {
            setError =true;
            temerro = true;
        }
        if (data_nasc.isEmpty()) {
            setError =true;
            temerro = true;
        }
        if (password.isEmpty()) {
            setError =true;
            temerro = true;
        }

        if (radioGroup.getCheckedRadioButtonId()==-1) {
            Toast.makeText(registo.this, "Diga se tem animais",
                    Toast.LENGTH_SHORT).show();
            temerro = true;

        }

        if(radioButton.isChecked()){
            radio="Sim";

        } else{radio="Não";}


        if (setError) {
            Toast.makeText(registo.this, "Preencha bem todos os campos",
                    Toast.LENGTH_SHORT).show();

        }



        if(!temerro){

            //dados válidos

            showAlert("A processar...");
            RequestQueue queue = Volley.newRequestQueue(registo.this);
            String url =registo;

            final String finalRadio = radio;

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("APPLOG", response);
                            parseJson(response, email); //processa o JSON


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
                    //PARAMETROS enviados para o ficheiro php
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("post_nome", nome.trim());
                    params.put("post_telefone", telefone.trim());
                    params.put("post_datanasc", data_nasc.trim());
                    params.put("post_password", password.trim());
                    params.put("post_email", email.trim());
                    params.put("post_morada", morada.trim());
                    params.put("post_ec", ec.trim());
                    params.put("post_genero", genero.trim());
                    params.put("post_animais", finalRadio.trim());
                    params.put("post_cc", cc.trim());
                    params.put("post_nif", nif.trim());
                    params.put("post_image",imageToString(bitmap));

                    return params;
                }

            };
            queue.add(postRequest);


        }
    }



    private  String imageToString(Bitmap bitmap){
        //converte a imagem para string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imgBytes,Base64.DEFAULT);

    }


    public  void parseJson(String jsonStr, String email){

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonStr);

            if(jsonObject.get("REGISTER").equals("OK")){
                //registo feito e entra na app

                //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                showAlert(jsonObject.get("MSG").toString());

               Intent intent= new Intent(this, homePage.class);
               intent.putExtra("email", email );
               startActivity(intent);
            }
            else{
//erro no registo, tem de submeter dados válidos
                //Toast.makeText(this,"Login Failed",Toast.LENGTH_LONG).show();
                showAlert(jsonObject.get("MSG").toString());
            }

        }catch (JSONException e){

        }
    }



    private void showAlert(String msg) {
        Toast.makeText(registo.this, msg,
                Toast.LENGTH_LONG).show();
    }


}


