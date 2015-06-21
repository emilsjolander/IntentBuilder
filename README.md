#IntentBuilder
Type safe intent building for services and activities.

IntentBuilder is a type safe way of creating intents and populating them with extras. Intents were created to be very dynamic but often times the dynamic nature of intents is not needed and just gets in the way of writing safe code.

##Installation
```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
    }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies {
    compile 'se.emilsjolander:intentbuilder-api:0.10.0'
    apt 'se.emilsjolander:intentbuilder-compiler:0.10.0'
}
```

##Usage
Annotate your activities and services with an `@IntentBuilder` annotation so that they are picked up by the library. For every class with an `@IntentBuilder` annotation a class named `MyActivityIntentBuilder` will be generated (Replace 'MyActivity' in the class name whith whatever the name of your Activity or Service class is). If your activity or service takes in parameters via extras in the intent you can now mark field with the `@Extra` annotation and they can be injected with the static `inject` method on the generated intent builder class. Extras can be marked as optional with the `@Optional` annotation.

Sample activity using IntentBuilder:
```java
@IntentBuilder
class DetailActivity extends Activity {
	
	@Extra
	String id;

	@Extra @Optional 
	String title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DetailActivityIntentBuilder.inject(getIntent(), this);
		// TODO use id and title
	}

startActivity(new DetailActivityIntentBuilder("12345")
	.title("MyTitle")
	.build(context))
}
```

Sample service using IntentBuilder:
```java
@IntentBuilder
class DownloadService extends IntentService {

    @Extra
    String downloadUrl;
	
	@Override
    protected void onHandleIntent(Intent intent) {
        MyServiceIntentBuilder.inject(intent, this);
    }

}

startService(new DownloadServiceIntentBuilder("http://google.com").build(context))
```

##Contributing
Contributions are very welcome! Both bug fixes and additional features if they make sense. Open a pull request to discuss any changes :)