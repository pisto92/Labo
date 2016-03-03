import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class OptimazeCSV {


    static HashMap<String, String> genres = new HashMap<String, String>();
    static String csvFile = "dati/dataset.csv"; //movieId,title,genre,userId,rating,id,avg
    static BufferedReader br = null;
    static String line = "";
    static String cvsSplitBy = ",";
    static PrintWriter pv;
    static String outputFilePath = "dati/datasetProcessed.csv";

    public static void main(String[] args) throws IOException {


        populateGenres();

        processCSV(outputFilePath);

        //userId-genre-rating
        generateCSV("dati/userId-genreId-rating0-5.csv", true);
        generateCSV("dati/userId-genreId-rating0-1.csv", false);
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
        System.out.println("Done");
    }

    private static void generateCSV(String output, boolean ratingTypeOriginal) throws IOException {
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
        System.out.println("Done");
    }

    private static void populateGenres() {
        genres.put("Drama", "1");
        genres.put("Comedy", "2");
        genres.put("Crime", "3");
        genres.put("Action", "4");
        genres.put("Thriller", "5");
        genres.put("Children", "6");
        genres.put("Adventure", "7");
        genres.put("Fantasy", "8");
        genres.put("Romance", "9");
        genres.put("Horror", "10");
        genres.put("Mystery", "11");
        genres.put("Animation", "12");
        genres.put("Sci-Fi", "13");
        genres.put("Documentary", "14");
        genres.put("Western", "15");
        genres.put("Musical", "16");
        genres.put("Film-Noir", "17");
        genres.put("War", "18");
        genres.put("\'(no genres listed)\'", "19");
    }

    private static String getCodeGenre(String genre) {
        System.out.println(genre);
        return genres.get(genre);
    }

    private static String getZeroOrOne(double rating, double average) {
        if (rating < average)
            return "0";
        else
            return "1";
    }
}