package reccomender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.*;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.util.List;


public class FilmRecommender {

    private static Genres genres;
    private static Movies movies;
    private static FileDataModel movieModel, genreModel;

    public static void main(String[] args) throws Exception {

        genres = new Genres();
        movies = new Movies();


        //rating 0.5-5
        System.out.println("Evaluation 0-5");
        movieModel = new FileDataModel(new File("dati/userId-movieId-rating.csv"));
        genreModel = new FileDataModel(new File("dati/userId-genreId-rating.csv"));

        Evaluation evaluator = new Evaluation(movieModel, genreModel);
        evaluator.startEvaluate();

        extractRecommendation();

    }

    private static void extractRecommendation() throws TasteException {

        RecommenderBuilder recommenderUserBased = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                UserSimilarity similarity = new PearsonCorrelationSimilarity(genreModel);
                UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
                return new GenericUserBasedRecommender(model, neighborhood, similarity);
            }
        };

        RecommenderBuilder recommenderItemBased = new RecommenderBuilder() {
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
            List<RecommendedItem> recommendationsGenres = recommenderUserBased.buildRecommender(genreModel).recommend(userId, 6);


            makePredictionForUsers(userId, found, recommendationsMovies, recommendationsGenres);
        }
    }

    private static void makePredictionForUsers(long userId, boolean found, List<RecommendedItem> recommendationsMovies, List<RecommendedItem> recommendationsGenres) {
        if (recommendationsMovies.size() > 20 && recommendationsGenres.size() > 0) {

            for (RecommendedItem recommendedMovies : recommendationsMovies) {
                if (!found) {
                    String moviesGenre = movies.getGenreById("" + recommendedMovies.getItemID());

                    for (RecommendedItem recommendedGenre : recommendationsGenres) {
                        String genre = genres.getGenreById("" + recommendedGenre.getItemID());
                        if (moviesGenre.equals(genre)) {
                            System.out.print(userId + " => ");
                            System.out.println("" + movies.getTitleById("" + recommendedMovies.getItemID()));
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
