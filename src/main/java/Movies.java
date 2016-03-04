import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Movies {

    static HashMap<String, String[]> movies = new HashMap<String, String[]>();
    static String csvFile = "dati/movies.csv";
    static BufferedReader br = null;
    static String line = "";
    static String cvsSplitBy = ",";
    int ID = 0;
    int GENRE = 1;
    int TITLE = 2;

    public Movies() throws IOException {
        readGenreFromFile();
    }

    private void readGenreFromFile() throws IOException {
        br = new BufferedReader(new FileReader(csvFile));
        while ((line = br.readLine()) != null) {
            String[] lineSplitted = line.split(cvsSplitBy);
            movies.put(lineSplitted[ID], new String[]{lineSplitted[GENRE], lineSplitted[TITLE]});
        }
        System.out.println("Movies loaded");
    }

    public String getGenreById(String id) {
        return movies.get(id)[0];
    }

    public String getTitleById(String id) {
        return movies.get(id)[1];
    }
}


