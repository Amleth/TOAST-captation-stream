package com.artisiou.toast;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws TwitterException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config"));

            MongoClient mongoClient = new MongoClient("localhost", Integer.parseInt(properties.getProperty("mongo-port")));
            MongoDatabase database = mongoClient.getDatabase(properties.getProperty("mongo-db"));
            final MongoCollection<Document> tweets_collection = database.getCollection(properties.getProperty("mongo-collection"));

            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb
                    .setJSONStoreEnabled(true)
                    .setDebugEnabled(Boolean.valueOf(properties.getProperty("debug")))
                    .setOAuthConsumerKey(properties.getProperty("consumerKey"))
                    .setOAuthConsumerSecret(properties.getProperty("consumerSecret"))
                    .setOAuthAccessToken(properties.getProperty("accessTokenKey"))
                    .setOAuthAccessTokenSecret(properties.getProperty("accessTokenSecret"));
            TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

            StatusListener listener = new StatusListener() {
                public void onStatus(Status status) {
                    String rawJSON = TwitterObjectFactory.getRawJSON(status);
                    Document doc = new Document().append("rawJson", Document.parse(rawJSON));
                    tweets_collection.insertOne(doc);
                    System.out.println(((Document)doc.get("rawJson")).get("text"));
                }

                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                    System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
                }

                public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                    System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
                }

                public void onScrubGeo(long userId, long upToStatusId) {
                    System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
                }

                public void onStallWarning(StallWarning warning) {
                    System.out.println("Got stall warning:" + warning);
                }

                public void onException(Exception ex) {
                    ex.printStackTrace();
                }
            };

            twitterStream.addListener(listener);
            twitterStream.filter(new FilterQuery(properties.getProperty("track").split(";")));
        } catch (IOException e) {
            System.out.println("Can't find `config` file.");
        }
    }
}