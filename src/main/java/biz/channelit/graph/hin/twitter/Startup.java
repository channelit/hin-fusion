package biz.channelit.graph.hin.twitter;

import biz.channelit.graph.hin.twitter.service.Tweets;
import biz.channelit.graph.hin.twitter.service.TwitterUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.TwitterException;

import javax.annotation.PostConstruct;

@Component
public class Startup {

    @Autowired
    Tweets tweets;

    @Autowired
    TwitterUsers twitterUsers;

    @PostConstruct
    public void init() {
//        tweets.getTweets();
        try {
            twitterUsers.getFollowers();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }



}
