package se.emilsjolander.intentbuilder.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;
import se.emilsjolander.intentbuilder.Optional;

@IntentBuilder
public class DetailActivity extends AppCompatActivity {

    @Extra
    String one;

    @Extra
    String two;

    @Extra @Optional
    String three;

    @Extra @Optional
    String four;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailActivityIntentBuilder.inject(getIntent(), this);
        setContentView(R.layout.activity_detail);

        ((TextView)findViewById(R.id.one)).setText(one);
        ((TextView)findViewById(R.id.one)).setText(two);
        ((TextView)findViewById(R.id.one)).setText(three);
        ((TextView)findViewById(R.id.one)).setText(four);
    }

}
