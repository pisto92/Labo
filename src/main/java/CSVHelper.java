import java.io.*;

public class CSVHelper {


    static Genres genres;
    static String csvFile = "dati/dataset.csv"; //movieId,title,genre,userId,rating,id,avg
    static BufferedReader br = null;
    static String line = "";
    static String cvsSplitBy = ",";
    static PrintWriter pv;
    static String outputFilePath = "dati/datasetProcessed.csv";

    public static void main(String[] args) throws IOException {
        genres= new Genres();
        processCSV(outputFilePath);

        //userId-genre-rating
        generateGenreCSV("dati/userId-genreId-rating.csv", true);
        generateGenreCSV("dati/userId-genreId-rating0-1.csv", false);

        //userId-movieId-rating
        generateMovieCSV("dati/userId-movieId-rating.csv", true);
        generateMovieCSV("dati/userId-movieId-rating0-1.csv", false);

    }

    private static void processCSV(String outputFilePath) throws IOException {
        pv = new PrintWriter(outputFilePath, "UTF-8");
        br = new BufferedReader(new FileReader(csvFile));
        while ((line = br.readLine()) != null) {

            String[] lineSplitted = line.split(cvsSplitBy);
            String[] lineToWrite = new String[8];

            String[] genres = lineSplitted[2].split("\\|");

            lineToWrite[0] = lineSplitted[0];
            lineToWrite[1] = lineSplitted[1];
            lineToWrite[2] = genres[0];
            lineToWrite[3] = getCodeGenre(genres[0]);
            lineToWrite[4] = lineSplitted[3];
            lineToWrite[5] = lineSplitted[4];
            lineToWrite[6] = lineSplitted[6];
            lineToWrite[7] = getZeroOrOne(Double.parseDouble(lineSplitted[4]), Double.parseDouble(lineSplitted[6]));

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
        br = new BufferedReader(new FileReader(outputFilePath));

        while ((line = br.readLine()) != null) {

            String[] lineSplitted = line.split(cvsSplitBy);
            String[] lineToWrite = new String[3];

            lineToWrite[0] = lineSplitted[4];
            lineToWrite[1] = lineSplitted[3];
            if (ratingTypeOriginal)
                lineToWrite[2] = lineSplitted[5];
            else
                lineToWrite[2] = lineSplitted[7];

            pv.println(lineToWrite[0] + "," +
                    lineToWrite[1] + "," +
                    lineToWrite[2]);
        }
        pv.close();
        System.out.println("Done userId-genreId-rating");
    }

    private static void generateMovieCSV(String output, boolean ratingTypeOriginal) throws IOException {
        pv = new PrintWriter(output, "UTF-8");
        br = new BufferedReader(new FileReader(outputFilePath));

        while ((line = br.readLine()) != null) {

            String[] lineSplitted = line.split(cvsSplitBy);
            String[] lineToWrite = new String[3];

            lineToWrite[0] = lineSplitted[4];
            lineToWrite[1] = lineSplitted[0];
            if (ratingTypeOriginal)
                lineToWrite[2] = lineSplitted[5];
            else
                lineToWrite[2] = lineSplitted[7];

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