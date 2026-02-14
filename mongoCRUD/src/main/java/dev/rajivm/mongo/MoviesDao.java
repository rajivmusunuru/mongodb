package dev.rajivm.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;


public class MoviesDao {

    private MongoCollection<Document> moviesCollection;

    public MoviesDao(String dbName, String collectionName) {
        this.moviesCollection = MongoClientProvider.getDatabase(dbName).getCollection(collectionName);
    }

    // method to find movies for a given query
    public FindIterable<Document> findMovies(Bson query) {
        return moviesCollection.find(query);
    }

    // insertOne
    public InsertOneResult insertOne(Document movie) {
        return moviesCollection.insertOne(movie);
    }
    // insertMany

    public InsertManyResult insertMany(List<Document> movies){
        return moviesCollection.insertMany(movies);
    }

    // updateOne
    public UpdateResult updateOne(Bson query, Bson updateDocument){
        return moviesCollection.updateOne(query, updateDocument);
    }

    // updateMany
    public UpdateResult updateMany(Bson query, Bson updateDocument){
        return moviesCollection.updateMany(query, updateDocument, new UpdateOptions().upsert(true));
    }

    // deleteOne
    public DeleteResult deleteOne(Bson query) {
        return moviesCollection.deleteOne(query);
    }

    // deleteMany
    public DeleteResult deleteMany(Bson query) {
        return moviesCollection.deleteMany(query);
    }



}
