package se.emilsjolander.intentbuilder.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;
import se.emilsjolander.intentbuilder.Optional;

@IntentBuilder
public class MySubClass extends MySuperClass {

    @Extra @Optional
    String three;

    @Extra @Optional
    String four;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MySubClassIntentBuilder.inject(getIntent(), this);
    }

}
