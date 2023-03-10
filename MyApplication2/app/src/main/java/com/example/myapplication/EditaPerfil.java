package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/*
 * Esta classe serve para editar o perfil do cliente
 * A classe está disposta desta maneira:
 * Campo: Campo do Cliente
 * Exemplo:
 * Nome: Nelson Andrade
 *O campo do cliente é retirado da base de dados
 * O campo do cliente pode ser mudado e se for clicado no button "SUBMETER ALTERAÇOES" os campos do cliente
 * vão ser mudados usando um ficheiro php
 * Para obter a imagem do cliente nesta activitie é usado um viewholder e o metodo GetImageFromURL*/

public class EditaPerfil extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //variaveis

    String altera="http://193.137.7.33/~estgv16061/app_php/altera.php";
    String perfil="http://193.137.7.33/~estgv16061/PINT_9/index.php/auth_mobile/perfil";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static  final int IMAGE_PICK_CODE=1000;
    private static  final int PERMISSON_CODE=1001;

    EditText nome, telefone, datanasc, morada, cc, nif;
    Spinner estadoCivil, genero;
    String ec, genero_str;
    String foto;
    RadioButton button_sim, button_nao;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edita_perfil);


        //toolbar
        Toolbar toolbar =findViewById(R.id.toolbar_edita_perfil);
        setSupportActionBar(toolbar);

        // toolbar.setTitle(getIntent().getStringExtra("email"));

        //deteta o clique na "seta" para voltar atrás
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


        //inicialização de variaveis


        button_sim = (RadioButton) findViewById(R.id.edita_perfil_radiosim);
        button_nao = (RadioButton) findViewById(R.id.edita_perfil_radionao);
        nome = (EditText) findViewById(R.id.edita_perfil_editnome);
        telefone = (EditText) findViewById(R.id.edita_perfil_editTlf);
        cc = (EditText) findViewById(R.id.edita_perfil_editCC);
        nif = (EditText) findViewById(R.id.edita_perfil_editNIF);
        morada = (EditText) findViewById(R.id.edita_perfil_editMorada);
        datanasc = (EditText) findViewById(R.id.edita_perfil_editDataNasc);


        //metodos
        getCampos();
        porContadores();
        dataNascimento();
        //preferencias(); //metodo util para projeto integrado (não houve tempo para o aplicar)

        Button edita = (Button)findViewById(R.id.edita_perfil_button_alterar);
        edita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaDados();
            }
        });



    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


//atualiza os dados do cliente da base de dados
    public  void mudaDados(){

        //metodo associado ao button "SUBMETE ALTERACOES" (texto, não id)

        //Ao ir buscar os dados do cliente, estes dados todos foram mudados com "setText(), etc
        //então é preciso ir buscar esses atributos ()editText, spinners,...) alterados
        //depois esses dados são verificados
        //se forem todos válidos (variavel temerro=false) irá ser feito um volley que irá
        // enviar esses dados para o ficheiro php que vai alterar os dados do cliente

        nome = (EditText) findViewById(R.id.edita_perfil_editnome);
        telefone = (EditText) findViewById(R.id.edita_perfil_editTlf);
        cc = (EditText) findViewById(R.id.edita_perfil_editCC);
        nif = (EditText) findViewById(R.id.edita_perfil_editNIF);
        morada = (EditText) findViewById(R.id.edita_perfil_editMorada);
        datanasc = (EditText) findViewById(R.id.edita_perfil_editDataNasc);
        estadoCivil = (Spinner) findViewById(R.id.edita_perfil_editEstadoCivil);
        genero = (Spinner) findViewById(R.id.edita_perfil_editGenero);
        RadioGroup radioGroup= (RadioGroup) findViewById(R.id.edita_perfil_radio_animais);
        String radio="0";
        RadioButton radioButton = (RadioButton) findViewById(R.id.edita_perfil_radiosim);
        boolean temerro=false;


        if(radioButton.isChecked()){
            radio="Sim";

        } else{radio="Não";}

        if (nome.getText().toString().isEmpty()) {
            temerro = true;
            showAlert("nome");
        }


        if (Patterns.PHONE.matcher(telefone.getText().toString()).matches() == false || telefone.getText().toString().length()!=9) {
            temerro = true;
            showAlert("telefone");


        }


        if (cc.getText().toString().length()!=12) {
            showAlert("cc");
            temerro = true;
        }
        if (nif.getText().toString().length()!=9) {
            showAlert("nif");
            temerro = true;
        }
        if (morada.getText().toString().isEmpty()) {
            showAlert("morada");
            temerro = true;
        }
        if (datanasc.getText().toString().isEmpty()) {
            showAlert("data");
            temerro = true;
        }

        if(temerro==true){

            //dados inválidos

            showAlert("Verifique os dados que introduziu");
        }

        else{



            //dados válidos
            //usa-se o volley para enviar os dados para o ficheiro php
            //use "APPLOG" no Logcat para ver o que é feito com mais detalhe
// é devolvido um JSon, onde o seu conteúdo será processsado, caso não haja erros de PHP/SQL

            RequestQueue queue = Volley.newRequestQueue(this);
            String url =altera;


            final String finalRadio = radio;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("APPLOG", response);
                            parseJson(response);


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

                    //dados enviados para o ficheiro php
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("post_email",getIntent().getStringExtra("email"));
                    params.put("post_nome", nome.getText().toString());
                    params.put("post_telefone", telefone.getText().toString());
                    params.put("post_datanasc", datanasc.getText().toString());
                    params.put("post_morada", morada.getText().toString());
                    params.put("post_ec", estadoCivil.getSelectedItem().toString());
                    params.put("post_genero", genero.getSelectedItem().toString());
                    params.put("post_animais", finalRadio);
                    params.put("post_cc", cc.getText().toString());
                    params.put("post_nif", nif.getText().toString());





                    return params;
                }

            };
            queue.add(postRequest);
        }


    }

    //processa o JSon
    public  void parseJson(String jsonStr){

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonStr);

            if(jsonObject.get("ALTERA").equals("OK")){
                //perfil alterado

                //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                showAlert(jsonObject.get("MSG").toString());
                finish();
            }
            else{
//perfil não alterado
                //Toast.makeText(this,"Login Failed",Toast.LENGTH_LONG).show();
                showAlert(jsonObject.get("MSG").toString());
            }

        }catch (JSONException e){

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_home,menu);
        return true;
    }


    //icons toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // ao clicar nos icons da toolbar
        if(id== R.id.settings){
            Intent intent = new Intent(getBaseContext(), definicoes.class);
            intent.putExtra("email",getIntent().getStringExtra("email"));
            startActivity(intent);
        }
        else if(id== R.id.exit){
            Intent intent = new Intent(getBaseContext(), Inicio.class);
            startActivity(intent);
            finish();
        }

        return true;
    }



//retira os dados do cliente da base de dados
    private void getCampos() {
        //retira os campos do cliente na base de dados com o volley
        //use "APPLOG" no Logcat para ver o que é feito com mais detalhe
// é devolvido um JSon, onde o seu conteúdo será processsado, caso não haja erros de PHP/SQL

        RequestQueue queue = Volley.newRequestQueue(EditaPerfil.this);
        String url = perfil;



        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APPLOG", response);
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(response);

                            if(jsonObject.get("MSG").equals("OK")){
                                //se foi encontrado um cliente
                                //vai mudar o text das editText, etc

                                nome.setText(jsonObject.get("NOME").toString());
                                telefone.setText(jsonObject.get("TELEFONE").toString());
                                datanasc.setText(jsonObject.get("DATA").toString());
                                morada.setText(jsonObject.get("MORADA").toString());
                                ec=jsonObject.get("EC").toString();
                                genero_str=jsonObject.get("GENERO").toString();
                                nif.setText(jsonObject.get("NIF").toString());
                                cc.setText(jsonObject.get("CC").toString());
                                foto=jsonObject.get("FOTO").toString();

                                //muda o item dos spinners
                                estadoCivil = findViewById(R.id.edita_perfil_editEstadoCivil);
                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditaPerfil.this, R.array.estadoCivil,
                                        android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                estadoCivil.setAdapter(adapter);
                                estadoCivil.setOnItemSelectedListener(EditaPerfil.this);
                                estadoCivil.setSelection(adapter.getPosition(ec));


                                genero = findViewById(R.id.edita_perfil_editGenero);
                                ArrayAdapter<CharSequence> adapter_genero = ArrayAdapter.createFromResource(EditaPerfil.this, R.array.genero,
                                        android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                genero.setAdapter(adapter_genero);
                                genero.setOnItemSelectedListener(EditaPerfil.this);
                                genero.setSelection(adapter_genero.getPosition(genero_str));


//muda o radio button
                                if(jsonObject.get("ANIMAIS").equals("Sim")) {
                                    button_sim.setChecked(true);
                                }else{
                                    button_nao.setChecked(true);
                                }

//faz load da imagem do cliente




                            }

                            if(jsonObject.get("CONTA").equals("NAOENCONTRADA")){
                                //não encontrou o perfil do cliente

                                //Toast.makeText(this,jsonObject.get("MSG"),Toast.LENGTH_LONG).show();
                                showAlert(jsonObject.get("Não foi possível carregar o perfil").toString());
                                // startActivity(new Intent(this,homePage.class));
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

                //parametros enviados para o ficheiro php
                Map<String,String> params = new HashMap<String,String>();
                params.put("post_email",getIntent().getStringExtra("email"));


                return params;
            }

        };
        queue.add(postRequest);
    }

//poe contadores de letras nas editTexts
    private void porContadores() {


//poe contadores nas editTexts
        CharCountTextView nomeCounter = (CharCountTextView) findViewById(R.id.edita_perfil_nomeCounter);
        CharCountTextView tlfCounter = (CharCountTextView) findViewById(R.id.edita_perfil_tlfCounter);
        CharCountTextView ccCounter = (CharCountTextView) findViewById(R.id.edita_perfil_ccCounter);
        CharCountTextView nifCounter = (CharCountTextView) findViewById(R.id.edita_perfil_nifCounter);
        CharCountTextView moradaCounter = (CharCountTextView) findViewById(R.id.edita_perfil_moradaCounter);


        counter(nomeCounter,nome);
        counter(tlfCounter,telefone);
        counter(ccCounter,cc);
        counter(nifCounter,nif);
        counter(moradaCounter,morada);


    }

    private void counter(CharCountTextView charCountTextView, EditText editText) {


        charCountTextView.setEditText(editText);
        charCountTextView.setCharCountChangedListener(new CharCountTextView.CharCountChangedListener() {
            @Override
            public void onCountChanged(int i, boolean b) {

            }
        });
    }




//datePicker da data de nascimento
    private void dataNascimento() {

        //na editText da data de nascimento, ao clicar nesta, aparece um DatePicker alertDialog, este tem data máxima da
        //data atual - 18 anos

        final EditText mDisplayDate = (EditText) findViewById(R.id.edita_perfil_editDataNasc);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -18);

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog newFragment = new DatePickerDialog(EditaPerfil.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                        year, month, day);

                //muda a cor dos "traços" do DatePicker
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        finish();

    }

//conversao de imagem
    private  String imageToString(Bitmap bitmap){
        //transforma a imagem para string para enviar para o ficheiro php
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imgBytes,Base64.DEFAULT);

    }


//toast
    private void showAlert(String msg) {
        //maneira prática de fazer toast message
        Toast.makeText(EditaPerfil.this, msg,
                Toast.LENGTH_LONG).show();
    }

}