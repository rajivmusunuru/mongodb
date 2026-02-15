package dev.rajivm.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
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

    public AggregateIterable<Document> matchGenreStage(){
        Bson matchStage=Aggregates.match(Filters.eq("genres", "Drama"));
        Bson limitStage=Aggregates.limit(5);
        return moviesCollection.aggregate(Arrays.asList(matchStage, limitStage));
    }

    public AggregateIterable<Document> matchAndGroupGenreByYearStages(){
        Bson matchStage=Aggregates.match(Filters.eq("genres", "Drama"));
        Bson groupStage=Aggregates.group("$year", Accumulators.sum("count", 1));
        return moviesCollection.aggregate(Arrays.asList(matchStage, groupStage));
    }

    public AggregateIterable<Document> matchSortAndProjectStages(){
        Bson matchStage=Aggregates.match(Filters.gte("imdb.rating", 9));
        Bson sortStage=Aggregates.sort(Sorts.orderBy(Sorts.descending("year")));
        Bson projectStage=Aggregates.project(Projections.fields(Projections.include("title", "year", "imdb.rating"), Projections.excludeId()));
        return moviesCollection.aggregate(Arrays.asList(matchStage, sortStage, projectStage));
    }





}
