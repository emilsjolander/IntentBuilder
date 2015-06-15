package se.emilsjolander.intentbuilder.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goToDetail = (Button) findViewById(R.id.go_to_detail);
        goToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new DetailActivityIntentBuilder("ett", "två")
                        .three("tre")
                        .build();
                startActivity(i);
            }
        });
    }

}
