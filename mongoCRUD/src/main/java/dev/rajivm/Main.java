package dev.rajivm;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import dev.rajivm.mongo.MongoClientProvider;
import dev.rajivm.mongo.MoviesDao;
import dev.rajivm.mongo.UserDao;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String dbName = System.getenv().getOrDefault("MONGO_DB", "sample_mflix");
        String collection = System.getenv().getOrDefault("MONGO_COLLECTION", "movies");
        MoviesDao moviesDao = new MoviesDao(dbName, collection);
        try {

            System.out.println("##### insertOne #####");

            ArrayList<String> directors = new ArrayList<>();
            directors.add("Chloé Zhao");
            InsertOneResult insertOneResult = moviesDao.insertOne(new Document()
                    .append("directors", directors)
                    .append("title", "Nomadland 2"));
            System.out.println(insertOneResult.getInsertedId());

            System.out.println("##### find the inserted movie #####");
            moviesDao.findMovies(Filters.eq("_id", insertOneResult.getInsertedId())).forEach(doc -> {
                System.out.println(doc.toJson());
            });

            System.out.println("##### insertMany #####");
            Document document1 = new Document()
                    .append("directors", directors)
                    .append("title", "Nomadland 3");
            Document document2 = new Document()
                    .append("directors", directors)
                    .append("title", "Nomadland 4");
            ArrayList<Document> movieList = new ArrayList<>();
            movieList.add(document1);
            movieList.add(document2);

            InsertManyResult insertManyResult = moviesDao.insertMany(movieList);
            System.out.println(insertManyResult.getInsertedIds());

            System.out.println("##### find the inserted movies #####");
            moviesDao.findMovies(Filters.in("_id", insertManyResult.getInsertedIds().values())).forEach(doc -> {
                System.out.println(doc.toJson());
            });

            System.out.println("##### find with query with and #####");
            moviesDao.findMovies(Filters.and(Filters.eq("directors", "Chloé Zhao"), Filters.eq("title", "Nomadland 2"))).forEach(doc -> {
                System.out.println(doc.toJson());
            });

            System.out.println("##### find first #####");
            Document firstDoc = moviesDao.findMovies(Filters.eq("directors", "Chloé Zhao")).first();
            System.out.println(firstDoc.toJson());

            System.out.println("##### updateOne #####");
            Bson query = Filters.eq("title", "Nomadland 2");

            Bson updateDocument = Updates.combine(Updates.push("directors", "Rajiv Musunuru"), Updates.set("imdb.rating", 7.4));

            UpdateResult updateResult = moviesDao.updateOne(query, updateDocument);
            System.out.println("Updated Result: " + updateResult);

            System.out.println("##### find the updated movie #####");
            moviesDao.findMovies(Filters.eq("title", "Nomadland 2")).forEach(doc -> {
                System.out.println(doc.toJson());
            });

            System.out.println("##### updateMany #####");
            Bson updateManyQuery = Filters.in("title", "Nomadland 5");
            ArrayList<String> newDirectors = new ArrayList<>();
            newDirectors.add("Rajiv Musunuru");
            newDirectors.add("Chloe Zhao");
            Bson updateManyDocument = Updates.combine(Updates.set("year", 2025), Updates.set("directors", newDirectors));

            UpdateResult updateManyResult = moviesDao.updateMany(updateManyQuery, updateManyDocument);
            System.out.println("Update Many Result: " + updateManyResult);
            System.out.println("##### find the updated movies #####");
            moviesDao.findMovies(Filters.eq("title", "Nomadland 5")).forEach(doc -> {
                System.out.println(doc.toJson());
            });
            System.out.println("Upserted document id: " + updateManyResult.getUpsertedId());
            moviesDao.findMovies(Filters.eq("_id", updateManyResult.getUpsertedId())).forEach(doc -> {
                System.out.println(doc.toJson());
            });

            System.out.println("##### deleteOne #####");
            Bson deleteOneQuery = Filters.eq("title", "Nomadland 5");
            DeleteResult deleteOneResult = moviesDao.deleteOne(deleteOneQuery);
            System.out.println("Delete One Result: " + deleteOneResult);
            moviesDao.findMovies(Filters.eq("title", "Nomadland 5")).forEach(doc -> {
                System.out.println(doc.toJson());
            });

            System.out.println("##### deleteMany #####");
            Bson deleteManyQuery = Filters.in("title", "Nomadland 2");
            DeleteResult deleteManyResult = moviesDao.deleteMany(deleteManyQuery);
            System.out.println("Delete Many Result: " + deleteManyResult);
            moviesDao.findMovies(Filters.eq("title", "Nomadland 2")).forEach(doc -> {
                System.out.println(doc.toJson());
            });

            System.out.println(" transaction to delete user and user's comments");
            UserDao userDao=new UserDao(dbName);
            String transactionsResult=userDao.deleteUserAndCommentsTransaction("nikolaj_coster-waldau@gameofthron.es");
            System.out.println(transactionsResult);

            // aggregation examples
            System.out.println("##### match stage example #####");
            moviesDao.matchGenreStage().forEach(document -> System.out.println(document.toJson()));

            System.out.println("##### match and group stages example #####");
            moviesDao.matchAndGroupGenreByYearStages().forEach(document -> System.out.println(document.toJson()));

            System.out.println("##### match, sort and project stages example #####");
            moviesDao.matchSortAndProjectStages().forEach(document -> System.out.println(document.toJson()));

        }
        catch (Exception e){
            System.err.println("MongoDB demo failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            MongoClientProvider.close();
        }
    }
}