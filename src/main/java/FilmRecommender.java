

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.*;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class FilmRecommender {

    private static RecommenderIRStatsEvaluator evaluator;
    private static RecommenderBuilder recommenderUserBased;
    private static DataModelBuilder modelBuilder;
    private static RecommenderBuilder recommenderItemBased;
    private static GenericBooleanPrefDataModel GB;
    private static Genres genres;
    private static Movies movies;
    private static FileDataModel movieModel, genreModel;

    public static void main(String[] args) throws Exception {
        genres = new Genres();
        movies = new Movies();

        evaluator = new GenericRecommenderIRStatsEvaluator();

        modelBuilder = new DataModelBuilder() {
            public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
                GB = new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
                return GB;
            }
        };

        movieModel = new FileDataModel(new File("dati/userId-movieId-rating.csv"));
        genreModel = new FileDataModel(new File("dati/userId-genreId-rating.csv"));

        testMovies(movieModel);
        testGenre(genreModel);

        extractRecommendation();

    }

    private static void extractRecommendation() throws TasteException {

        recommenderUserBased = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                UserSimilarity similarity = new PearsonCorrelationSimilarity(genreModel);
                UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
                return new GenericUserBasedRecommender(model, neighborhood, similarity);
            }
        };

        recommenderItemBased = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                ItemSimilarity similarity = new LogLikelihoodSimilarity(movieModel);
                return new GenericItemBasedRecommender(model, similarity);
            }
        };

        LongPrimitiveIterator iterator = movieModel.getUserIDs();
        while (iterator.hasNext()) {
            List<RecommendedItem> recommendationsUserBased = recommenderItemBased.buildRecommender(movieModel).recommend(iterator.nextLong(), 30);
            if (recommendationsUserBased.size() > 20) {
                System.out.print(iterator.nextLong() + " => ");
                System.out.print("Movies: [ ");
                for (RecommendedItem recommendation : recommendationsUserBased) {
                    System.out.print(movies.getGenreById("" + recommendation.getItemID()));
                    System.out.print(" ");
                }
                System.out.println("]");
            }
        }

        LongPrimitiveIterator iterator2 = genreModel.getUserIDs();
        for (int i = 0; i < 10; i++) {
            List<RecommendedItem> recommendationsUserBased = recommenderUserBased.buildRecommender(genreModel).recommend(iterator2.nextLong(), 6);
            System.out.print(iterator2.nextLong() + " => ");
            System.out.print("Genre: [ ");
            for (RecommendedItem recommendation : recommendationsUserBased) {
                System.out.print(genres.getGenreById("" + recommendation.getItemID()));
                System.out.print(" ");
            }
            System.out.println("]");
        }
    }

    public static void testGenre(final DataModel genreModel) throws TasteException {

        recommenderUserBased = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                UserSimilarity similarity = new PearsonCorrelationSimilarity(genreModel);
                UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
                return new GenericUserBasedRecommender(model, neighborhood, similarity);
            }
        };

        recommenderItemBased = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                ItemSimilarity similarity = new LogLikelihoodSimilarity(genreModel);
                return new GenericItemBasedRecommender(model, similarity);
            }
        };

        evaluateStats(genreModel);
    }

    public static void testMovies(final DataModel movieModel) throws TasteException {

        recommenderUserBased = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                UserSimilarity similarity = new PearsonCorrelationSimilarity(movieModel);
                UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
                return new GenericUserBasedRecommender(model, neighborhood, similarity);
            }
        };

        recommenderItemBased = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                ItemSimilarity similarity = new LogLikelihoodSimilarity(movieModel);
                return new GenericItemBasedRecommender(model, similarity);
            }
        };

        evaluateStats(movieModel);
    }

    private static void printUserBaseRecommendationGenre(List<RecommendedItem> recommendationsUserBased) {

    }

    private static void printItemBaseRecommendationMovies(List<RecommendedItem> recommendationsUserBased) {

    }

    private static void evaluateStats(DataModel dataModel) throws TasteException {

        try {
            IRStatistics stats = evaluator.evaluate(recommenderUserBased, modelBuilder, dataModel, null, 8, 0.1, 0.8);
            System.out.println("Precision ItemBased: " + stats.getPrecision());
            System.out.println("Recall ItemBased: " + stats.getRecall());

            IRStatistics stats2 = evaluator.evaluate(recommenderItemBased, modelBuilder, dataModel, null, 8, 0.1, 0.8);
            System.out.println("Precision UserBased: " + stats2.getPrecision());
            System.out.println("Recall UserBased: " + stats2.getRecall());
        } catch (Throwable t) {
            System.out.println("throwing " + t);
        }
    }
}
