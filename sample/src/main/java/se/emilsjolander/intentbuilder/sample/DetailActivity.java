package se.emilsjolander.intentbuilder.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;

@IntentBuilder
public class DetailActivity extends AppCompatActivity {

    @Extra
    String one;

    @Extra
    String two;

    @Extra @Nullable
    String three;

    @Extra @Nullable
    String four;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailActivityIntentBuilder.inject(getIntent(), this);
        setContentView(R.layout.activity_detail);

        ((TextView)findViewById(R.id.one)).setText(one);
        ((TextView)findViewById(R.id.two)).setText(two);
        ((TextView)findViewById(R.id.three)).setText(three);
        ((TextView)findViewById(R.id.four)).setText(four);
    }

}
