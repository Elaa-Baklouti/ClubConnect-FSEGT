import java.util.ArrayList;
import java.util.List;

public class Post {
    int id;
    String title, content;
    User author;
    List<String> comments = new ArrayList<>();
    int likes = 0;

    public Post(int id, String title, String content, User author) {
        this.id      = id;
        this.title   = title;
        this.content = content;
        this.author  = author;
    }

    public String toString() {
        return "Post#" + id + " [" + title + "] par " + author.getUsername()
             + " | likes=" + likes + " | comments=" + comments.size();
    }
}
