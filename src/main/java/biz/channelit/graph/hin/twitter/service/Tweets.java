package biz.channelit.graph.hin.twitter.service;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Tweets {

    private static Client hosebirdClient;

    private static Set<String> fields = new HashSet<>(Arrays.asList("name", "text", "coordinates", "geo", "place", "name","screen_name", "followers_count", "friends_count", "favourites_count", "following", "lang", "user_mentions", "hashtags", "media", "media_url"));

    /**
     * Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream
     */
    private static BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);

    private static JsonParser parser = new JsonParser();
    private static final String del = "|";
    private static final String textFilter = "[^a-zA-Z0-9 +-.#:/@]";
    private static final String path = "../";

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
    private static String getFileName() {
        return new SimpleDateFormat("yyyyMMddHH'.txt'").format(new Date());
    }

    public void getTweets() {
        setupHosebirdClient();
        hosebirdClient.connect();

        while (!hosebirdClient.isDone()) {
            try {
                String tweetText = msgQueue.take();
                JsonObject t = parser.parse(tweetText).getAsJsonObject();
                if (t.has("text") && !t.get("text").getAsString().startsWith("RT")) {
                    String id = t.get("id").getAsString();
                    extractTweets(id, t, "tweet");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void extractTweets(String idIn, JsonObject t, String predicate) {
        t.entrySet().forEach(
                (s)-> {
                    if (!s.getValue().isJsonNull()) {
                        String id =  t.keySet().contains("id") ? t.get("id").getAsString() : idIn;
                        if (s.getValue().isJsonPrimitive()) {
                            if (fields.contains(s.getKey())) {
                                createTriple(id, predicate + "_"+ s.getKey(), s.getValue().getAsString().replaceAll(textFilter, ""));
                            }
                        } else if (s.getValue().isJsonObject()) {
                            JsonObject jasonObj = s.getValue().getAsJsonObject();
                            String newId = (jasonObj.has("id") ? jasonObj.get("id").getAsString():id);
                            if (!newId.equals(id) ) {
                                createTriple(id, predicate + "_" + s.getKey() + "_id" , newId);
                            }
                            extractTweets(newId, jasonObj, predicate + "_" + s.getKey());
                        } else if (s.getValue().isJsonArray()) {
                            s.getValue().getAsJsonArray().forEach((a)-> {
                                if (a.isJsonObject()) {
                                    JsonObject aj = a.getAsJsonObject();
                                    String newId = (aj.has("id") ? aj.get("id").getAsString():id);
                                    if (!newId.equals(id) ) {
                                        createTriple(id, predicate + "_" + s.getKey() + "_id" , newId);
                                    }
                                    extractTweets(newId, aj, predicate + "_" + s.getKey());
                                } else if (a.isJsonPrimitive()) {
                                    if (fields.contains(s.getKey())) {
                                        createTriple(id, predicate + "_"+ s.getKey(), a.getAsString().replaceAll(textFilter, ""));
                                    }
                                }
                            });
                        }
                    }
                }
        );
    }

    private void createTriple(String subject, String predicate, String object) {
        writeToFile(subject + del + predicate + del + object + System.lineSeparator());
    }

    private Set<String> getMatches(String text, Pattern pattern) {
        Set<String> result = new HashSet<>();
        Matcher m = pattern.matcher(text);
        while(m.find()) {
            result.add(m.group());
        }
        return result;
    }

    private static void setupHosebirdClient() {
        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

        // Optional: set up some followings and track terms
        //List<Long> followings = Lists.newArrayList(1234L, 566788L);
        List<String> terms = Lists.newArrayList("orlando", "mgm", "vegas", "shooting", "nfl", "gun", "concert", "dead", "nevada", "mandalay");
//            endpoint.followings(followings);
        endpoint.trackTerms(terms);

        Authentication hosebirdAuth = new OAuth1(
                "o5mxswbDptDXSj9cr5swmZYle",
                "ZryB6aSxlPcYoamhxhPyRoCCDCJDFPrZGp1s5pCQqo2xbsPorZ",
                "36754386-dUYaCAWfwkoegk9GekTvIaXo1E1zeY6Xer5HvYiEY",
                "4oNyXC5X3idupExP4BpkxmHKN1tkOUkAk9CQ1qM2TasRB");
        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")        // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        hosebirdClient = builder.build();
    }
}
