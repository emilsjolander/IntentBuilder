package se.emilsjolander.intentbuilder.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;

import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;

@IntentBuilder
public class MySubClass extends MySuperClass {

    @Extra @Nullable
    String three;

    @Extra @Nullable
    String four;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MySubClassIntentBuilder.inject(getIntent(), this);
    }

}
