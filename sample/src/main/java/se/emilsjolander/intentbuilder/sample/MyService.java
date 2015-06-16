package se.emilsjolander.intentbuilder.sample;

import android.app.IntentService;
import android.content.Intent;

import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;

@IntentBuilder
public class MyService extends IntentService {

    @Extra
    String downloadUrl;

    public MyService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MyServiceIntentBuilder.inject(intent, this);
    }

}
