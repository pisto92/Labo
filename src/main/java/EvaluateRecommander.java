

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.*;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveArrayIterator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.TopItems;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.IntegerTuple;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EvaluateRecommander {

    private static DataModel trainingModel;
    private static DataModel recommendationModel;
    private static RecommenderEvaluator averageEvaluator;
    private static RecommenderIRStatsEvaluator evaluator;
    private static RecommenderBuilder recommenderUserBased;
    private static DataModelBuilder modelBuilder;
    private static RecommenderBuilder recommenderItemBased;
    private static GenericBooleanPrefDataModel GB;
    private static Genres genres;

    public static void main(String[] args) throws Exception {
        genres = new Genres();
        afterPropertiesSet();
        testData();
        testTopItems();
    }

    public static void afterPropertiesSet() throws IOException, TasteException {

        trainingModel = new FileDataModel(new File("dati/userId-genreId-rating0-5.csv"));

        recommendationModel = new FileDataModel(new File("dati/testFilm"));

        evaluator = new GenericRecommenderIRStatsEvaluator();

        recommenderUserBased = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                UserSimilarity similarity = new PearsonCorrelationSimilarity(trainingModel);
                UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
                return new GenericUserBasedRecommender(model, neighborhood, similarity);
            }
        };

        recommenderItemBased = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                ItemSimilarity similarity = new LogLikelihoodSimilarity(trainingModel);
                return new GenericItemBasedRecommender(model, similarity);
            }
        };

        modelBuilder = new DataModelBuilder() {
            public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
                GB = new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
                return GB;
            }
        };
    }

    private static void evaluateStats() throws TasteException {

        try {
            IRStatistics stats = evaluator.evaluate(recommenderUserBased, modelBuilder, trainingModel, null, 7, 0.0, 0.8);
            System.out.println("Recall ItemBased: " + stats.getRecall());
            System.out.println("Precision ItemBased: " + stats.getPrecision());

            IRStatistics stats2 = evaluator.evaluate(recommenderItemBased, modelBuilder, recommendationModel, null, 7, 0.0, 0.8);
            System.out.println("Recall UserBased: " + stats2.getRecall());
            System.out.println("Precision UserBased: " + stats2.getPrecision());
        } catch (Throwable t) {
            System.out.println("throwing " + t);
        }
    }

    public static void testData() throws TasteException {

        evaluateStats();


        LongPrimitiveIterator iterator = trainingModel.getUserIDs();
        while (iterator.hasNext()) {
            List<RecommendedItem> recommendationsUserBased = recommenderUserBased.buildRecommender(recommendationModel).recommend(iterator.nextLong(), 3);
            List<RecommendedItem> recommendationsItemBased = recommenderItemBased.buildRecommender(recommendationModel).recommend(iterator.nextLong(), 3);
            System.out.println(iterator.nextLong());
            printUserBaseRecommendation(recommendationsUserBased);
            printItemBaseRecommendation(recommendationsItemBased);
        }

    }

    private static void printItemBaseRecommendation(List<RecommendedItem> recommendationsItemBased) {
        System.out.print("ItemBase: [ ");
        for (RecommendedItem recommendation : recommendationsItemBased) {
            System.out.print(genres.getGenreById(""+recommendation.getItemID()));
            System.out.print(" ");
        }
        System.out.println("]");
    }

    private static void printUserBaseRecommendation(List<RecommendedItem> recommendationsUserBased) {
        System.out.print("UserBase: [ ");
        for (RecommendedItem recommendation : recommendationsUserBased) {
            System.out.print(genres.getGenreById(""+recommendation.getItemID()));
            System.out.print(" ");
        }
        System.out.println("]");
    }

    public static void testTopItems() throws Exception {


    }
}
