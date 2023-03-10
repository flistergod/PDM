package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

/*
* Classe que contêm os fragments principais/ de navegação da app
* É aqui em função da mudança de fragment atual que o titulo da toolbar é mudado*/
public class homePage extends AppCompatActivity {


   Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

       BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setOnNavigationItemSelectedListener(navListener);


       getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new Home()).commit();

       toolbar =findViewById(R.id.toolbar_home);
       setSupportActionBar(toolbar);



    }

      private  BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment=null;

                    switch(item.getItemId()){

                        case R.id.nav_home:
                            toolbar.setTitle("Página Inicial");
                            selectedFragment = new Home();

                            break;

                        case R.id.nav_cartao:
                            toolbar.setTitle("Fidelizações");
                            selectedFragment = new Cartao();

                            break;

                        case R.id.nav_procura:
                            toolbar.setTitle("Fidelizar");
                            selectedFragment = new Procura();

                            break;


                        case R.id.nav_perfil:
                            toolbar.setTitle("Perfil");
                            selectedFragment = new Perfil();

                            break;



                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedFragment).commit();

                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_home,menu);
        return true;
    }

    //icons da toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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

    private void showAlert(String msg) {
        Toast.makeText(homePage.this, msg,
                Toast.LENGTH_LONG).show();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new Home()).commit();
                //Update List
            }
            if (resultCode == RESULT_CANCELED) {
                //Do nothing?
            }
        }
    }

}
