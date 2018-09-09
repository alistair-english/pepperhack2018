package com.example.alistair.pepperhackathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.HolderBuilder;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.holder.AutonomousAbilitiesType;

public class IntroActivity extends RobotActivity implements RobotLifecycleCallbacks {

    QiContext myQiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
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

        myQiContext = qiContext;

        Say askForPermission = SayBuilder.with(qiContext)
                .withText("Hey! I'm Pepper. The library staff have asked me to ask around and see how the library is going. Wanna help me out?")
                .build();

        askForPermission.run(); // wait for syncronus run

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.ALWAYS);
                findViewById(R.id.agree_button).setVisibility(View.VISIBLE);
                findViewById(R.id.disagree_button).setVisibility(View.VISIBLE);
                findViewById(R.id.info_text).setVisibility(View.VISIBLE);
            }
        });


        //Listen for a response
        final PhraseSet yesPhrase = PhraseSetBuilder.with(qiContext)
                .withTexts("yes")
                .build();

        final PhraseSet noPhrase = PhraseSetBuilder.with(qiContext)
                .withTexts("no")
                .build();

        final Listen listen = ListenBuilder.with(qiContext)
                .withPhraseSets(yesPhrase, noPhrase)
                .build();

        Future<ListenResult> userResponse = listen.async().run();

        userResponse.andThenConsume(new Consumer<ListenResult>() {
            @Override
            public void consume(ListenResult listenResult) throws Throwable {
                if(listenResult.getHeardPhrase().getText().equals("yes")){
                    IntroActivity.this.userAgrees(null);
                } else if (listenResult.getHeardPhrase().getText().equals("no")){
                    IntroActivity.this.userDisagrees(null);
                }
            }
        });
    }

    @Override
    public void onRobotFocusLost() {
        // user has navigated away from our app
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // idk what this one is
    }

    public void userAgrees(View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
                findViewById(R.id.agree_button).setVisibility(View.GONE);
                findViewById(R.id.disagree_button).setVisibility(View.GONE);
                findViewById(R.id.info_text).setVisibility(View.GONE);
            }
        });

        Say getStarted = SayBuilder.with(myQiContext)
                .withText("Great! Let's get started.")
                .build();

        getStarted.run(); // wait for syncronus run

        Intent intent = new Intent(this, WebViewActivity.class);

        startActivity(intent);
    }

    public void userDisagrees(View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
                findViewById(R.id.agree_button).setVisibility(View.GONE);
                findViewById(R.id.disagree_button).setVisibility(View.GONE);
                findViewById(R.id.info_text).setVisibility(View.GONE);
            }
        });

        Say sorry = SayBuilder.with(myQiContext)
                .withText("Ok. Sorry for bothering you.")
                .build();

        sorry.run(); // wait for syncronus run

        Intent intent = new Intent(this, StartActivity.class);

        startActivity(intent);
    }
}
