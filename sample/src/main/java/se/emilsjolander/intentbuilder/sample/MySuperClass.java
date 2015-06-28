package se.emilsjolander.intentbuilder.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;
import se.emilsjolander.intentbuilder.Optional;

public class MySuperClass extends AppCompatActivity {

    @Extra
    String one;

    @Extra
    String two;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
