package biz.channelit.graph.hin.twitter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.ArrayList;

@Service
public class TwitterUsers {

    @Autowired
    Twitter twitter;

    private static long cursor = -1L;

    public void getFollowers() throws TwitterException {

        PagableResponseList<User> followersList;

        ArrayList<String> list = new ArrayList<String>();
        followersList = twitter.getFollowersList("hhpatel", cursor);

        for (int i = 0; i < followersList.size(); i++) {
            User user = followersList.get(i);
            String name = user.getName();
            list.add(name);
            System.out.println("Name" + i + ":" + name);
        }


    }
}
