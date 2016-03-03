

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.*;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AbstractDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EvaluateRecommander {

    private static DataModel trainingModel;
    private static DataModel recommendationModel;
    private static RecommenderEvaluator averageEvaluator;
    private static RecommenderIRStatsEvaluator genericEvaluator;
    private static RecommenderBuilder recommenderBuilder;
    private static DataModelBuilder modelBuilder;

    public static void main(String[] args) throws IOException, TasteException {
        afterPropertiesSet();
        testData();
    }

    public static void afterPropertiesSet() throws IOException, TasteException {

        trainingModel = new FileDataModel(new File("dati/userId-genreId-rating0-5.csv"));

        recommendationModel = new FileDataModel(new File("dati/userId-genreId-rating0-5.csv"));

        averageEvaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        genericEvaluator = new GenericRecommenderIRStatsEvaluator();


        recommenderBuilder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
//                UserSimilarity similarity = new PearsonCorrelationSimilarity(trainingModel);
//                UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
//                return new GenericUserBasedRecommender(model, neighborhood, similarity);
                ItemSimilarity similarity = new LogLikelihoodSimilarity(trainingModel);
                return new GenericItemBasedRecommender(model, similarity);
            }
        };

        modelBuilder = new DataModelBuilder() {
            public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
                return new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
            }
        };
    }

    private static void evaluateStats() throws TasteException {
        double scoreAverage = averageEvaluator.evaluate(recommenderBuilder, modelBuilder, trainingModel, 0.8, 1.0);
        System.out.println("Score AverageAbsoluteDifferenceRecommenderEvaluator: " + scoreAverage);

        try {
            IRStatistics stats = genericEvaluator.evaluate(recommenderBuilder, modelBuilder, trainingModel, null, 7, 0.0, 1.0);
            System.out.println("Recall GenericRecommenderIRStatsEvaluator: " + stats.getRecall());
            System.out.println("Precision GenericRecommenderIRStatsEvaluator: " + stats.getPrecision());
        } catch (Throwable t) {
            System.out.println("throwing " + t);
        }
    }

    public static void testData() throws TasteException {

        evaluateStats();

        List<RecommendedItem> recommendations = recommenderBuilder.buildRecommender(recommendationModel).recommend(137798, 3);
        System.out.println("user 1");
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }

//        recommendations = recommenderBuilder.buildRecommender(recommendationModel).recommend(139448, 3);
//        System.out.println("user 2");
//        for (RecommendedItem recommendation : recommendations) {
//            System.out.println(recommendation);
//        }
//
//        try {
//            recommendations = recommenderBuilder.buildRecommender(recommendationModel).recommend(138442, 3);
//            System.out.println("user 3");
//            for (RecommendedItem recommendation : recommendations) {
//                System.out.println(recommendation);
//            }
//        } catch (Throwable t) {
//            System.out.println("throwing " + t);
//        }
    }
}
