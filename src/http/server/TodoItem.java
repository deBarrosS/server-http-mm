package http.server;

public class TodoItem {
    private int id;
    private String content;

    public TodoItem(int id, String content) {
        this.id = id;
        this.content = content.replaceAll("\\+", " ");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toHTMLCode(){
        String element = "<p>"+ this.content+"</p>";
        return element;
    }
}
