package com.amazonaws.lambda.demo;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class LambdaFunctionHandler implements RequestHandler<Object, String> {
    public String handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);
        String consumerKey = System.getenv("consumerKey");
        String consumerSecret = System.getenv("consumerSecret");
        String token = System.getenv("token");
        String tokenSecret = System.getenv("tokenSecret");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokenSecret);
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        List<Long> myTweetTmp = new ArrayList<>();
        Paging paging = new Paging(1, 100);
        List<Status> myTweets = null;
        try {
            myTweets = twitter.getUserTimeline(paging);
        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
        for (Status my_i : myTweets) {
            if (my_i.getFavoriteCount() == 0 && my_i.getFavoriteCount() == 0 &&
                    !my_i.getText().matches("^@[0-9a-zA-Z_].*")) {
                System.out.print(my_i.getUser().getName() + " : " + my_i.getText() + "\n");
                myTweetTmp.add(my_i.getId());
            }
        }
        List<Status> mentions = null;
        try {
            mentions = twitter.getMentionsTimeline(paging);
        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
        List<Long> mentionsTmp = new ArrayList<>();
        for (Status men_i : mentions) {
            System.out.print(men_i.getUser().getName() + " : " + men_i.getText() + "\n");
            mentionsTmp.add(men_i.getInReplyToStatusId());
        }
        System.out.print("中間リスト = " + myTweetTmp.size() + "\n");
        System.out.print("除去対象 = " + mentionsTmp.size() + "\n");
        myTweetTmp.removeAll(mentionsTmp);
        System.out.print("削除リスト = " + myTweetTmp.size() + "\n");
        for (Long del_i : myTweetTmp) {
            try {
                System.out.print(twitter.showStatus(del_i).getText() + "\n");
                twitter.destroyStatus(del_i);
            } catch (Exception e) {
                System.out.print(e);
            }
        }
        return "Hello from Lambda!";
    }

}
