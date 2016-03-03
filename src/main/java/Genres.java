import java.util.HashMap;

public class Genres{

    static HashMap<String, String> genres = new HashMap<String, String>();

    public Genres(){
        putKeyGenreAndId();
    }

    private void putKeyGenreAndId() {
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

    public String getIdByGenre(String genre){
        return genres.get(genre);
    }
}
