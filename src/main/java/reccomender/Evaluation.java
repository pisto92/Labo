package reccomender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
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
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class Evaluation {

    private static RecommenderIRStatsEvaluator evaluator;
    private static RecommenderBuilder recommenderUserBased;
    private static DataModelBuilder modelBuilder;
    private static RecommenderBuilder recommenderItemBased;
    private static GenericBooleanPrefDataModel GB;
    private static FileDataModel movieModel;
    private static FileDataModel genreModel;

    public Evaluation(FileDataModel movieModel, FileDataModel genreModel) throws TasteException {
        this.movieModel = movieModel;
        this.genreModel = genreModel;

        evaluator = new GenericRecommenderIRStatsEvaluator();

        modelBuilder = new DataModelBuilder() {
            public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
                GB = new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
                return GB;
            }
        };

    }

    public void startEvaluate() throws TasteException {
        testMovies(movieModel);
        testGenre(genreModel);
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
