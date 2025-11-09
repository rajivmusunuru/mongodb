package dev.rajivm.mongo;

import com.mongodb.client.MongoCollection;
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
    private final MongoCollection<Document> collection;

    public UserDao(String dbName, String collectionName) {
        this.collection = MongoClientProvider.getDatabase(dbName).getCollection(collectionName);
    }

    public User create(User user) {
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
    }
}

