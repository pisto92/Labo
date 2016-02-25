

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.*;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EvaluateRecommander {

    private static DataModel trainingModel;
    private static DataModel recommendationModel;
    private static RecommenderEvaluator evaluator;
    private static RecommenderIRStatsEvaluator evaluator2;
    private static RecommenderBuilder recommenderBuilder;
    private static DataModelBuilder modelBuilder;

    public static void main(String[] args) throws IOException, TasteException {

//        try{
//            //Creating data model
//            DataModel datamodel = new FileDataModel(new File("dati/data")); //data
//            //Creating UserSimilarity object.
//            UserSimilarity usersimilarity = new PearsonCorrelationSimilarity(datamodel);
//            //Creating UserNeighbourHHood object.
//            UserNeighborhood userneighborhood = new ThresholdUserNeighborhood(1.0, usersimilarity, datamodel);
//            //Create UserRecomender
//            UserBasedRecommender recommender = new GenericUserBasedRecommender(datamodel, userneighborhood, usersimilarity);
//            List<RecommendedItem> recommendations = recommender.recommend(2, 3);
//            for (RecommendedItem recommendation : recommendations) {
//                System.out.println(recommendation);
//            }
//        }catch(Exception e){}
        afterPropertiesSet();
        testData();
    }

    public static void afterPropertiesSet() throws IOException, TasteException {

        trainingModel = new GenericBooleanPrefDataModel(
                GenericBooleanPrefDataModel.toDataMap(new FileDataModel(new File("dati/datasetFilm")))
        );

        recommendationModel = new GenericBooleanPrefDataModel(
                GenericBooleanPrefDataModel.toDataMap(new FileDataModel(new File("dati/testFilm")))
        );

        evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        evaluator2 = new GenericRecommenderIRStatsEvaluator();


        recommenderBuilder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
//                UserSimilarity similarity = new PearsonCorrelationSimilarity(trainingModel);
//                UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, trainingModel);
//                return new GenericUserBasedRecommender(trainingModel, neighborhood, similarity);
                UserSimilarity similarity = new LogLikelihoodSimilarity(trainingModel);
                UserNeighborhood neighborhood = new NearestNUserNeighborhood(1, 0.7, similarity, model);
                return new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarity);
            }
        };

        modelBuilder = new DataModelBuilder() {
            public DataModel buildDataModel( FastByIDMap<PreferenceArray> trainingData ) {
                return new GenericBooleanPrefDataModel( GenericBooleanPrefDataModel.toDataMap(trainingData) );
            }
        };
    }

    public static void testData() throws TasteException {

        double score = evaluator.evaluate(recommenderBuilder, modelBuilder, trainingModel, 0.9, 1.0);
        System.out.println("calculated score: " + score);

        try {
            IRStatistics stats = evaluator2.evaluate(
                    recommenderBuilder, modelBuilder, trainingModel, null, 2,
                    0.0,
                    1.0
            );
            System.out.println("recall: " + stats.getRecall());
            System.out.println("precision: " + stats.getPrecision());
        } catch (Throwable t) {
            System.out.println("throwing " + t);
        }

        List<RecommendedItem> recommendations = recommenderBuilder.buildRecommender(recommendationModel).recommend(1,2);
        System.out.println("user 1");
        for (RecommendedItem recommendation : recommendations) { System.out.println(recommendation);}

        recommendations = recommenderBuilder.buildRecommender(recommendationModel).recommend(2,2);
        System.out.println("user 2");
        for (RecommendedItem recommendation : recommendations) { System.out.println(recommendation);}

        try {
            recommendations = recommenderBuilder.buildRecommender(recommendationModel).recommend(3,2);
            System.out.println("user 3");
            for (RecommendedItem recommendation : recommendations) { System.out.println(recommendation);}
        } catch (Throwable t) {
            System.out.println("throwing " + t);
        }
    }
}
