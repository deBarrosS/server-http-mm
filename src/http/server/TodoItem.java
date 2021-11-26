package http.server;

/**
 * An item of the Todo List treated by the POST requests
 */
public class TodoItem {
    private int id;
    private String content;

    public TodoItem(int id, String content) {
        this.id = id;
        setContent(content);
    }

    public void setContent(String content) {
        this.content = content.replaceAll("\\+", " ");
    }

    public String toHTMLCode(){
        return "<p>"+ this.content+"</p>";
    }
}
