package com.example.alistair.pepperhackathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.EditText;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    public static final String EXTRA_MESSAGE = "com.example.pepperhackathon.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register the RobotLifeCycleCallback to call this Activity?
        QiSDK.register(this, this);

    }

    @Override
    protected void onDestroy() {
        // Unregister the activity
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        // activity has focus on app

        Say say = SayBuilder.with(qiContext)
                .withText("Hello Human! My name is Pepper")
                .build();
        say.run();

    }

    @Override
    public void onRobotFocusLost() {
        // user has navigated away from our app
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // idk what this one is
    }

    // called when user presses send message button
    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    // called when user presses be a cat button
    public void gotoCat(View view) {
        Intent intent = new Intent(this, AnimateCatActivity.class);
        startActivity(intent);
    }
}
