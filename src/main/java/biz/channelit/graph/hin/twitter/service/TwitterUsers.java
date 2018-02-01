package biz.channelit.graph.hin.twitter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

@Service
public class TwitterUsers {

    @Autowired
    Twitter twitter;

    private static final String del = "|";

    private static final String path = "../twitter_followers/";

    private static long cursor = -1L;

    public void getFollowers() throws TwitterException, IOException {
        Stream<String> users = Files.lines(Paths.get("src/main/resources/twitter_journalists.txt"));

        users.forEach( user -> {
                    try {

                        User userInfo = twitter.lookupUsers(user).get(0);
                        createTriple(String.valueOf(userInfo.getId()), "user_screen_name", userInfo.getScreenName());
                        PagableResponseList<User> followersList;
                        followersList = twitter.getFollowersList(userInfo.getId(), cursor);
                        for (int i = 0; i < followersList.size(); i++) {
                            User follower = followersList.get(i);
                            String name = follower.getName();
                            createTriple(String.valueOf(userInfo.getId()), "followed_by", String.valueOf(follower.getId() ));
                            createTriple(String.valueOf(follower.getId()), "user_screen_name", follower.getScreenName());
                            System.out.println("Name" + i + ":" + follower.getName());
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
        });


    }

    private void createTriple(String subject, String predicate, String object) {
        writeToFile(subject + del + predicate + del + object + System.lineSeparator());
    }

    private static String getFileName() {
        return new SimpleDateFormat("yyyyMMddHH'.txt'").format(new Date());
    }

    private void writeToFile(String str) {
        Path filePath = Paths.get(path + getFileName());
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            Files.write(filePath, str.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
