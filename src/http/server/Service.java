package http.server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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

    public int handleGet(BufferedOutputStream out, String filename){
//        filename = treatFilename(filename);

//        String extension = filename.substring(filename.lastIndexOf('.')).toLowerCase(Locale.ROOT);

        try{

            File file = getFile(filename);

            // Read the file as binary allows same manipulation for every type of file
            // At this point all the headers have been sent
            // Send the body of the response; The bodies here treated are "Single-resource bodies"
            BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(file));
            byte[] fileBuffer = new byte[256];
            int contentLength;
            while((contentLength = fileStream.read(fileBuffer)) != -1) {
                out.write(fileBuffer, 0,contentLength );
            }

            fileStream.close();
            out.flush();
        }catch(Exception e){
            System.err.println("Error in Handle Get " + e);
        }

        return Integer.parseInt(status.split(" ")[0]);
    }



    public static String getHTMLFile(String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        String html = "";
        try{
            FileReader fileReader = new FileReader("src/pages/"+ fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine() ;

            while(line !=null){
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
        }catch(FileNotFoundException e){
            System.out.println("File Not Found, POST a 404: " + e);
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

    public String handleDeleteFile(String param) {
        return getHTMLFile("delete_success.html");
    }
    public String handlePutFile(String param) {
        return getHTMLFile("put_success.html");
    }
}
