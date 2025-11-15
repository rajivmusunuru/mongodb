package dev.rajivm.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Simple provider for a MongoClient. Reads connection string from environment variable MONGODB_URI
 * or falls back to mongodb://localhost:27017. Keeps a single shared client.
 */
public class MongoClientProvider {
    private static final String DEFAULT_URI = "mongodb://localhost:27017";
    private static volatile MongoClient client;

    private MongoClientProvider() {}

    public static MongoClient getClient() {
        if (client == null) {
            synchronized (MongoClientProvider.class) {
                if (client == null) {
                    String uri = System.getenv("MONGODB_URI");
                    if (uri == null || uri.isBlank()) {
                        uri = DEFAULT_URI;
                    }
                    client = MongoClients.create(uri);
                }
            }
        }
        return client;
    }

    public static MongoDatabase getDatabase(String dbName) {
        return getClient().getDatabase(dbName);
    }

    public static void close() {
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
