package biz.channelit.graph.hin.twitter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

@Service
public class TwitterUsers {

    @Autowired
    Twitter twitter;

    private static final String path = "../twitter_followers/";

    private static long cursor = -1L;

    public void getFollowers() throws TwitterException, IOException {
        Stream<String> users = Files.lines(Paths.get("src/main/resources/twitter_journalists.txt"));

        users.forEach( user -> {
                    try {
                        PagableResponseList<User> followersList;
                        followersList = twitter.getFollowersList("hhpatel", cursor);
                        for (int i = 0; i < followersList.size(); i++) {
                            User follower = followersList.get(i);
                            String name = follower.getName();
                            System.out.println("Name" + i + ":" + follower.getName());
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
        });


    }
}
