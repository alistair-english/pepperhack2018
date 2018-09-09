package com.example.alistair.pepperhackathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class DisplayMessageActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private String toSay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // get the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // put the intents string in the layout shit
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        toSay = message;

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
                .withText(toSay)
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
}
