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
import java.util.Date;
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
    private static int numRecommendation=0;

    public static void main(String[] args) throws Exception {


        System.out.println("Inizio: " + new Date().toString());

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

        System.out.println("movieModel.getNumUsers() "+movieModel.getNumUsers());
        System.out.println("movieModel.getNumItems() "+movieModel.getNumItems());
        System.out.println("genreModel.getNumUsers() "+genreModel.getNumUsers());
        System.out.println("genreModel.getNumItems() "+genreModel.getNumItems());

        testMovies(movieModel);
        testGenre(genreModel);

        extractRecommendation();

        System.out.println("Numreccomantation: "+numRecommendation);
        System.out.println("Fine: "+ new Date().toString());

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

        LongPrimitiveIterator iterator3 = movieModel.getUserIDs();
        while (iterator3.hasNext()) {
            long userId = iterator3.nextLong();
            boolean found = false;
            List<RecommendedItem> recommendationsMovies = recommenderItemBased.buildRecommender(movieModel).recommend(userId, 30);


//            if (recommendationsMovies.size() > 20) {
//                System.out.print(userId + " => ");
//                System.out.print("Movies: [ ");
//                for (RecommendedItem recommendation : recommendationsMovies) {
//                    System.out.print(movies.getGenreById("" + recommendation.getItemID()));
//                    System.out.print(" ");
//                }
//                System.out.println("]");
//            }
//
//
            List<RecommendedItem> recommendationsGenres = recommenderUserBased.buildRecommender(genreModel).recommend(userId, 6);
//            System.out.print(userId + " => ");
//            System.out.print("Genre: [ ");
//            for (RecommendedItem recommendation : recommendationsGenres) {
//                System.out.print(genres.getGenreById("" + recommendation.getItemID()));
//                System.out.print(" ");
//            }
//            System.out.println("]");



            if (recommendationsMovies.size() > 20 && recommendationsGenres.size() > 0) {

                for (RecommendedItem recommendedMovies : recommendationsMovies) {
                    if (!found) {
                        String moviesGenre = movies.getGenreById("" + recommendedMovies.getItemID());
//                        System.out.println("######### " + moviesGenre.toUpperCase() + " #########");

                        for (RecommendedItem recommendedGenre : recommendationsGenres) {
                            String genre = genres.getGenreById("" + recommendedGenre.getItemID());
                            if (moviesGenre.equals(genre)) {
                                System.out.print(userId + " => ");
                                System.out.println("" + movies.getTitleById(""+recommendedMovies.getItemID()));
                                numRecommendation+=1;
                                found = true;
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
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
