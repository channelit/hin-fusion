package biz.channelit.graph.hin.twitter;

import biz.channelit.graph.hin.twitter.service.Tweets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

@EnableAutoConfiguration
public class App {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
        Tweets tweets = new Tweets();
        tweets.getTweets();
    }

}