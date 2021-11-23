package http.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Service {
    private List<TodoItem> todoItemList = new LinkedList<>();
    private int latestId = 0;
    private final String NEW_ITEM_KEY = "new-item";


    public String handleAddTodoItem(Map<String, String> body) {
        TodoItem newItem = new TodoItem(latestId++, body.get(NEW_ITEM_KEY));
        todoItemList.add(newItem);

        return generateTodoHTML();
    }

    /**
     * generateTodoHTML() get the core html file and adds todoItems to it so they can be displayed
     * @return
     */
    private String generateTodoHTML() {
        StringBuilder stringBuilder = new StringBuilder();
        String html = "";
        try{
            FileReader fileReader = new FileReader("src/pages/todo.html");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine() ;

            while(line !=null){
//                detect the section where we must insert the todo items
                if ("<!--    ITEMS-->".equals(line)){
                    for (TodoItem todoItem:todoItemList) {
                        stringBuilder.append(todoItem.toHTMLCode());
                    }
                } else {
                    stringBuilder.append(line);
                }
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
        }catch(FileNotFoundException e){
            // return 500
            System.err.println("Couldn't find load the core HTML file todo.html: "+e);
        } catch (IOException e) {
            // return 500
            System.err.println("Couldn't generate HTML file: "+e);
            html = "";
        }
        html = stringBuilder.toString();
        return html;
    }

    public static String getHTMLFile(String url){
        StringBuilder stringBuilder = new StringBuilder();
        String html = "";
        try{
            FileReader fileReader = new FileReader("src/pages/todo.html");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine() ;

            while(line !=null){
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
        }catch(FileNotFoundException e){
            System.out.println("File Not Found, POST a 404: "+e);
            html = "";
        } catch (IOException e) {
            e.printStackTrace();
            html = "";
        }
        html = stringBuilder.toString();
        System.out.println("> html");;
        System.out.println(html);
        return html;
    }

}
