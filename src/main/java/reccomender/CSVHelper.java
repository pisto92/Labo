package reccomender;

import java.io.*;

public class CSVHelper {


    static Genres genres;
    static String DATASET = "dati/dataset.csv";
    static BufferedReader br = null;
    static String line = "";
    static String cvsSplitBy = ",";
    static PrintWriter pv;
    static String DATASET_PROCESSED = "dati/datasetProcessed.csv";

    static int MOVIE_ID = 0;
    static int TITLE = 1;
    static int GENRE = 2;
    static int USER_ID = 3;
    static int RATING = 4;
    static int AVG = 6;

    public static void main(String[] args) throws IOException {
        genres= new Genres();
        generateDatasetProcessed(DATASET_PROCESSED);

        //userId-genre-rating
        generateGenreCSV("dati/userId-genreId-rating.csv", true);
        generateGenreCSV("dati/userId-genreId-ratingZeroOne.csv", false);

        //userId-movieId-rating
        generateMovieCSV("dati/userId-movieId-rating.csv", true);
        generateMovieCSV("dati/userId-movieId-ratingZeroOne.csv", false);

    }

    private static void generateDatasetProcessed(String outputFilePath) throws IOException {
        pv = new PrintWriter(outputFilePath, "UTF-8");
        br = new BufferedReader(new FileReader(DATASET));
        while ((line = br.readLine()) != null) {

            String[] lineSplitted = line.split(cvsSplitBy);
            String[] lineToWrite = new String[8];


            String[] genres = lineSplitted[GENRE].split("\\|");

            lineToWrite[0] = lineSplitted[MOVIE_ID];
            lineToWrite[1] = lineSplitted[TITLE];
            lineToWrite[2] = genres[MOVIE_ID];
            lineToWrite[3] = getCodeGenre(genres[MOVIE_ID]);
            lineToWrite[4] = lineSplitted[USER_ID];
            lineToWrite[5] = lineSplitted[RATING];
            lineToWrite[6] = lineSplitted[AVG];
            lineToWrite[7] = getZeroOrOne(Double.parseDouble(lineSplitted[RATING]), Double.parseDouble(lineSplitted[AVG]));

            pv.println(lineToWrite[0] + "," +
                    lineToWrite[1] + "," +
                    lineToWrite[2] + "," +
                    lineToWrite[3] + "," +
                    lineToWrite[4] + "," +
                    lineToWrite[5] + "," +
                    lineToWrite[6] + "," +
                    lineToWrite[7]);
        }

        pv.close();
        System.out.println("Done datasetProcessed");
    }

    private static void generateGenreCSV(String output, boolean ratingTypeOriginal) throws IOException {
        pv = new PrintWriter(output, "UTF-8");
        br = new BufferedReader(new FileReader(DATASET_PROCESSED));

        while ((line = br.readLine()) != null) {

            String[] lineSplitted = line.split(cvsSplitBy);
            String[] lineToWrite = new String[3];

            //per migliore leggibilità (sono diversi da quelli del dataset originale
            int GENRE_ID = 3;
            int USER_ID = 4;
            int RATING = 5;
            int AVG = 7;

            lineToWrite[0] = lineSplitted[USER_ID];
            lineToWrite[1] = lineSplitted[GENRE_ID];
            if (ratingTypeOriginal)
                lineToWrite[2] = lineSplitted[RATING];
            else
                lineToWrite[2] = lineSplitted[AVG];

            pv.println(lineToWrite[0] + "," +
                    lineToWrite[1] + "," +
                    lineToWrite[2]);
        }
        pv.close();
        System.out.println("Done userId-genreId-rating");
    }

    private static void generateMovieCSV(String output, boolean ratingTypeOriginal) throws IOException {
        pv = new PrintWriter(output, "UTF-8");
        br = new BufferedReader(new FileReader(DATASET_PROCESSED));

        while ((line = br.readLine()) != null) {

            String[] lineSplitted = line.split(cvsSplitBy);
            String[] lineToWrite = new String[3];

            //per migliore leggibilità (sono diversi da quelli del dataset originale
            int MOVIE_ID = 0;
            int USER_ID = 4;
            int RATING = 5;
            int AVG = 7;

            lineToWrite[0] = lineSplitted[USER_ID];
            lineToWrite[1] = lineSplitted[MOVIE_ID];
            if (ratingTypeOriginal)
                lineToWrite[2] = lineSplitted[RATING];
            else
                lineToWrite[2] = lineSplitted[AVG];

            pv.println(lineToWrite[0] + "," +
                    lineToWrite[1] + "," +
                    lineToWrite[2]);
        }
        pv.close();
        System.out.println("Done userId-movieId-rating");
    }



    private static String getCodeGenre(String genre) {
        return genres.getIdByGenre(genre);
    }

    private static String getZeroOrOne(double rating, double average) {
        if (rating < average)
            return "0";
        else
            return "1";
    }
}