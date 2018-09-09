package com.example.alistair.pepperhackathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class WebViewActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private WebView webView;
    private String[] questionURLs = {"Question1", "Question2", "Question3", "Question4"};
    private int questionPos = -1;
    private static final int maxQuestions = 4;
    private QiContext myQiContext;
    private static final boolean DO_SELECTION_FEEDBACK = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);

        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

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
        // activity has focus on app
        final WebPepperQuestionInterface questionInterface = new WebPepperQuestionInterface(webView, qiContext, this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.addJavascriptInterface(questionInterface, "Pepper");
            }
        });

        this.nextQuestion("");
    }

    @Override
    public void onRobotFocusLost() {
        // user has navigated away from our app
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // idk what this one is
    }

    public void nextQuestion(String selectedAnswer) {
        Future<Void> sayFuture = null;
        if (myQiContext != null && selectedAnswer != "" && DO_SELECTION_FEEDBACK) {
            Say saySelectedAnswer = SayBuilder.with(myQiContext)
                    .withText("You chose " + selectedAnswer)
                    .build();
            sayFuture = saySelectedAnswer.async().run();
        }

        if (sayFuture != null) {
            sayFuture.andThenConsume(new Consumer<Void>() {
                @Override
                public void consume(Void aVoid) throws Throwable {
                    WebViewActivity.this.incrementQuestion();
                }
            });
        } else {
            this.incrementQuestion();
        }
    }

    private void incrementQuestion(){
        questionPos ++;
        if(questionPos < maxQuestions) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("file:///android_asset/" + questionURLs[questionPos] + ".html");
                }
            });

        } else {
            Intent intent = new Intent(this, EndActivity.class);

            startActivity(intent);
        }
    }
}
