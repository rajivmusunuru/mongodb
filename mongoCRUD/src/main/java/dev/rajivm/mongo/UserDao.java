package dev.rajivm.mongo;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.TransactionBody;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import dev.rajivm.model.User;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDao {
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> commentsCollection;

    public UserDao(String dbName) {
        this.usersCollection = MongoClientProvider.getDatabase(dbName).getCollection("users");
        this.commentsCollection = MongoClientProvider.getDatabase(dbName).getCollection("comments");
    }

    // transactions
    public String deleteUserAndCommentsTransaction(String userEmail) {
        // start a client session
        ClientSession session = MongoClientProvider.getClient().startSession();
        TransactionBody<String> txBody = new TransactionBody<String>() {
            @Override
            public String execute() {
                // delete all user's comments
                DeleteResult deleteCommentsRes = commentsCollection.deleteMany(session, Filters.eq("email", userEmail));
                // delete user
                DeleteResult deleteUserRes = usersCollection.deleteOne(session, Filters.eq("email", userEmail));
                return "User and comments for email " + userEmail + " deleted. Comments deleted: " + deleteCommentsRes.getDeletedCount() + ", User deleted: " + deleteUserRes.getDeletedCount();
            }
        };

        try {
            return session.withTransaction(txBody);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return "User and user comments deletion failed";
    }



    /*public User create(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }
        Document doc = user.toDocument();
        collection.insertOne(doc);
        return user;
    }

    public User findById(String id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return User.fromDocument(doc);
    }

    public List<User> findAll() {
        List<User> out = new ArrayList<>();
        for (Document d : collection.find()) {
            out.add(User.fromDocument(d));
        }
        return out;
    }

    public boolean update(User user) {
        if (user.getId() == null) return false;
        Bson filter = Filters.eq("_id", user.getId());
        Document update = new Document("$set", new Document("name", user.getName()).append("email", user.getEmail()));
        UpdateResult res = collection.updateOne(filter, update);
        return res.getModifiedCount() > 0;
    }

    public boolean delete(String id) {
        DeleteResult res = collection.deleteOne(Filters.eq("_id", id));
        return res.getDeletedCount() > 0;
    }*/


}

