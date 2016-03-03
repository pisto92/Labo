import java.util.HashMap;

public class Genres {

    static HashMap<String, String> genres = new HashMap<String, String>();

    public Genres() {
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

        genres.put("1", "Drama");
        genres.put("2", "Comedy");
        genres.put("3", "Crime");
        genres.put("4", "Action");
        genres.put("5", "Thriller");
        genres.put("6", "Children");
        genres.put("7", "Adventure");
        genres.put("8", "Fantasy");
        genres.put("9", "Romance");
        genres.put("10", "Horror");
        genres.put("11", "Mystery");
        genres.put("12", "Animation");
        genres.put("13", "Sci-Fi");
        genres.put("14", "Documentary");
        genres.put("15", "Western");
        genres.put("16", "Musical");
        genres.put("17", "Film-Noir");
        genres.put("18", "War");
        genres.put("19", "\'(no genres listed)\'");
    }

    public String getGenreById(String id) {
        return genres.get(id);
    }

    public String getIdByGenre(String genre) {
        return genres.get(genre);
    }
}
