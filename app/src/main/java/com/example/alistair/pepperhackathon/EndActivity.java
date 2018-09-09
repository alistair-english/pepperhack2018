package com.example.alistair.pepperhackathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.HolderBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.holder.AutonomousAbilitiesType;

public class EndActivity extends RobotActivity implements RobotLifecycleCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);

        // Register the RobotLifeCycleCallback to call this Activity
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
        Say sayThanks = SayBuilder.with(qiContext)
                .withText("Ok, thats it. Thanks for talking with me! It was great to meet you!")
                .build();

        sayThanks.run(); // wait for syncronus run

        Intent intent = new Intent(this, StartActivity.class);

        startActivity(intent);
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
