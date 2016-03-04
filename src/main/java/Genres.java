import java.io.*;
import java.util.HashMap;

public class Genres{

    static HashMap<String, String> genres = new HashMap<String, String>();
    static String csvFile = "dati/genres.csv";
    static BufferedReader br = null;
    static String line = "";
    static String cvsSplitBy = ",";
    int KEY = 0;
    int VALUE = 1;

    public Genres() throws IOException {
       readGenreFromFile();
    }

    private void readGenreFromFile() throws IOException {
        br = new BufferedReader(new FileReader(csvFile));
        while ((line = br.readLine()) != null) {
            String[] lineSplitted = line.split(cvsSplitBy);
            genres.put(lineSplitted[KEY], lineSplitted[VALUE]);
        }
        System.out.println("Genres loaded");
    }


    public String getGenreById(String id) {
        return genres.get(id);
    }

    public String getIdByGenre(String genre) {
        return genres.get(genre);
    }
}
