package com.example.alistair.pepperhackathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.HolderBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.autonomousabilities.AutonomousAbilities;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.holder.AutonomousAbilitiesType;
import com.aldebaran.qi.sdk.object.holder.Holder;

public class StartActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private Holder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
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
        // activity has focus on app
        holder = HolderBuilder.with(qiContext)
                .withAutonomousAbilities(
                        AutonomousAbilitiesType.AUTONOMOUS_BLINKING,
                        AutonomousAbilitiesType.BASIC_AWARENESS,
                        AutonomousAbilitiesType.BACKGROUND_MOVEMENT
                )
                .build();

        //hold dem ablilties
        holder.async().hold();
    }

    @Override
    public void onRobotFocusLost() {
        // user has navigated away from our app
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // idk what this one is
    }

    // function that is run by 'play with me!' button
    public void start_program(View view) {

        // start up the cheeky robot boi
        holder.async().release();

        // go to the intro action
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);


    }
}
