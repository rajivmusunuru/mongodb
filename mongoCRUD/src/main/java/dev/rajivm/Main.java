package dev.rajivm;

import dev.rajivm.model.User;
import dev.rajivm.mongo.MongoClientProvider;
import dev.rajivm.mongo.UserDao;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("MongoDB CRUD demo starting...");
        String dbName = System.getenv().getOrDefault("MONGO_DB", "testdb");
        String collection = System.getenv().getOrDefault("MONGO_COLLECTION", "users");

        UserDao dao = new UserDao(dbName, collection);
        try {
            // Create
            User u = new User(null, "Alice Example", "alice@example.com");
            dao.create(u);
            System.out.println("Created user id=" + u.getId());

            // Read
            User fetched = dao.findById(u.getId());
            System.out.println("Fetched user: " + (fetched != null ? fetched.getName() : "null"));

            // Update
            fetched.setEmail("alice+updated@example.com");
            boolean updated = dao.update(fetched);
            System.out.println("Updated: " + updated);

            // List
            List<User> all = dao.findAll();
            System.out.println("All users count: " + all.size());

            // Delete
            boolean deleted = dao.delete(fetched.getId());
            System.out.println("Deleted: " + deleted);

        } catch (Exception e) {
            System.err.println("MongoDB demo failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            MongoClientProvider.close();
        }

        System.out.println("Demo finished.");
    }
}