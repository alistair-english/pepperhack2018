package com.example.alistair.pepperhackathon;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Function;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.Qi;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;

import java.util.ArrayList;
import java.util.List;

public class WebPepperQuestionInterface {

    private WebView myWebView;
    private QiContext myQiContext;
    private WebViewActivity myWebActivity;

    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;

    private Future<Void> a1Future = null;
    private Future<Void> a2Future = null;
    private Future<Void> a3Future = null;
    private Future<Void> a4Future = null;
    private Future<ListenResult> listenResultFuture = null;


    WebPepperQuestionInterface(WebView w, QiContext q, WebViewActivity a) {
        myWebView = w;
        myQiContext = q;
        myWebActivity = a;
    }

    @JavascriptInterface
    public void setValues(String question_,String answer1_,String answer2_,String answer3_,String answer4_) {
        question = question_;
        answer1 = answer1_;
        answer2 = answer2_;
        answer3 = answer3_;
        answer4 = answer4_;
    }

    @JavascriptInterface
    public void sayQuestion() {
        if(myQiContext != null) {
            Say sayQuestion = SayBuilder.with(myQiContext)
                    .withText(question)
                    .build();

            Future<Void> sayFuture = sayQuestion.async().run();

            sayFuture.thenConsume(Qi.onUiThread(new Consumer<Future<Void>>() {
                @Override
                public void consume(Future<Void> voidFuture) throws Throwable {
                    myWebView.evaluateJavascript("advanceToAnswers()", null);
                }
            }));

        }

    }

    @JavascriptInterface
    public void sayAnswers() {
        Say sayAnswer1 = SayBuilder.with(myQiContext)
                .withText(answer1 + "\\pau=200\\")
                .build();

        a1Future = sayAnswer1.async().run();

        a2Future = a1Future.thenCompose(new Function<Future<Void>, Future<Void>>() {
            @Override
            public Future<Void> execute(Future<Void> ignore) throws Throwable {
                Say sayAnswer2 = SayBuilder.with(myQiContext)
                        .withText(answer2 + "\\pau=200\\")
                        .build();

                Future<Void> middle = sayAnswer2.async().run();
                return middle;
            }
        });

        a3Future = a2Future.thenCompose(new Function<Future<Void>, Future<Void>>() {
            @Override
            public Future<Void> execute(Future<Void> ignore) throws Throwable {
                Say sayAnswer3 = SayBuilder.with(myQiContext)
                        .withText(answer3 + "\\pau=200\\")
                        .build();

                Future<Void> middle = sayAnswer3.async().run();
                return middle;
            }
        });

        a4Future = a3Future.thenCompose(new Function<Future<Void>, Future<Void>>() {
            @Override
            public Future<Void> execute(Future<Void> ignore) throws Throwable {
                Say sayAnswer4 = SayBuilder.with(myQiContext)
                        .withText(answer4 + "\\pau=200\\")
                        .build();

                Future<Void> middle = sayAnswer4.async().run();
                return middle;
            }
        });

        //Chain the says with the listen

        listenResultFuture = a4Future.andThenCompose(new Function<Void, Future<ListenResult>>() {
            @Override
            public Future<ListenResult> execute(Void aVoid) throws Throwable {
                // build the listen
                PhraseSet answer1Phrases = PhraseSetBuilder.with(myQiContext)
                        .withTexts(answer1)
                        .build();

                PhraseSet answer2Phrases = PhraseSetBuilder.with(myQiContext)
                        .withTexts(answer2)
                        .build();

                PhraseSet answer3Phrases = PhraseSetBuilder.with(myQiContext)
                        .withTexts(answer3)
                        .build();

                PhraseSet answer4Phrases = PhraseSetBuilder.with(myQiContext)
                        .withTexts(answer4)
                        .build();

                Listen listen = ListenBuilder.with(myQiContext)
                        .withPhraseSets(answer1Phrases, answer2Phrases, answer3Phrases, answer4Phrases)
                        .build();

                Future<ListenResult> answerListening = listen.async().run();
                return answerListening;
            }
        });

        listenResultFuture.andThenConsume(new Consumer<ListenResult>() {
            @Override
            public void consume(ListenResult listenResult) throws Throwable {
                WebPepperQuestionInterface.this.nextQuestion(listenResult.getHeardPhrase().getText());
            }
        });
    }

    @JavascriptInterface
    public void nextQuestion(String selectedAnswer) {
        if(listenResultFuture != null){
            listenResultFuture.requestCancellation();
        } else if(a4Future != null){
            a4Future.requestCancellation();
        } else if (a3Future != null){
            a3Future.requestCancellation();
        } else if (a2Future != null){
            a2Future.requestCancellation();
        } else if (a1Future != null){
            a1Future.requestCancellation();
        }
        myWebActivity.nextQuestion(selectedAnswer);
    }
}
