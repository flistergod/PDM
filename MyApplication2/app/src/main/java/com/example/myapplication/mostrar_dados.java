package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/*Esta classe não é utilizada pois não houve tempo de a implementar
 *  */
public class mostrar_dados extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_dados);

        Button registo = (Button) (findViewById(R.id.button_registo_defenitivo));
        registo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), homePage.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
