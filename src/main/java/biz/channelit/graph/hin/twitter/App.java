package biz.channelit.graph.hin.twitter;

import biz.channelit.graph.hin.twitter.service.Tweets;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@EnableAutoConfiguration
@ComponentScan({"biz.channelit.graph.hin.twitter"})
public class App {

    private static final String token = "36754386-dUYaCAWfwkoegk9GekTvIaXo1E1zeY6Xer5HvYiEY";
    private static final String tokenSecret = "4oNyXC5X3idupExP4BpkxmHKN1tkOUkAk9CQ1qM2TasRB";
    private static final String consumerKey = "o5mxswbDptDXSj9cr5swmZYle";
    private static final String consumerSecret = "ZryB6aSxlPcYoamhxhPyRoCCDCJDFPrZGp1s5pCQqo2xbsPorZ";
    public static BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);

    @Bean
    public Twitter getTwitter() throws IOException, TwitterException {
        TwitterFactory factory = new TwitterFactory();
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(accessToken);
        return twitter;
    }

    @Bean
    public Client getHBC() {
        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Client hosebirdClient;
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

        // Optional: set up some followings and track terms
        //List<Long> followings = Lists.newArrayList(1234L, 566788L);
        List<String> terms = Lists.newArrayList("orlando", "mgm", "vegas", "shooting", "nfl", "gun", "concert", "dead", "nevada", "mandalay");
//            endpoint.followings(followings);
        endpoint.trackTerms(terms);

        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);
        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")        // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        hosebirdClient = builder.build();

        return hosebirdClient;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }

    @Bean(name="msgQueue")
    public BlockingQueue<String> getMsgQueue() {
        return msgQueue;
    }

}