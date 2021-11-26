package http.server;

import http.server.requests.HttpRequest;
import http.server.response.HttpResponse;
import http.server.response.HttpStatusCode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Service {
    private final List<TodoItem> todoItemList = new LinkedList<>();
    private int latestId = 0;
    private final String NEW_ITEM_KEY = "new-item";


    public HttpResponse handleAddTodoItem(Map<String, String> body) {
        if(!body.containsKey(NEW_ITEM_KEY)){
            return HttpResponse.badRequestResponse();
        }
        TodoItem newItem = new TodoItem(latestId++, body.get(NEW_ITEM_KEY));
        todoItemList.add(newItem);
        String html =generateTodoHTML();
        if (html == null){
            return HttpResponse.internalServerErrorResponse();
        }
        return new HttpResponse(HttpStatusCode.CREATED, "text/html", html);
    }

    /**
     * generateTodoHTML() get the core html file and adds todoItems to it so they can be displayed
     * @return
     */
    private String generateTodoHTML() {
        StringBuilder stringBuilder = new StringBuilder();
        String html = "";
        try{
            FileReader fileReader = new FileReader("pages/todo.html");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine() ;

            while(line !=null){
//                detect the section where we must insert the todo items
                if ("<!--ITEMS-->".equals(line)){
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
            return null;
        } catch (IOException e) {
            // return 500
            System.err.println("Couldn't generate HTML file: "+e);
            return null;
        }
        html = stringBuilder.toString();
        return html;
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

    public HttpResponse handleDeleteFile(HttpRequest request) {
        String fileDir = request.params;
        File file = new File("uploads/" + fileDir);
        if(!file.delete()) {
            System.out.println("Failed to delete the file");
            return HttpResponse.internalServerErrorResponse();
        }
        System.out.println("File "+ fileDir + " deleted successfully");
        return new HttpResponse(HttpStatusCode.OK, "text/html", getHTMLFile("delete_success.html"));
    }
    public HttpResponse handlePutFile(HttpRequest request) {
        try{
            /* For files
            OutputStream out = new FileOutputStream("a.jpg");
            // NEED TO CHANGE THIS
            out.write(request.body.toString().getBytes());
            out.flush();
            out.close();
             */
            PrintWriter out = new PrintWriter("uploads/"+ request.params);            // NEED TO CHANGE THIS
            out.println(request.body.get("content"));
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            System.err.println("Could not create or find file: "+e);
            return HttpResponse.internalServerErrorResponse();
        } catch (Exception e){
            System.err.println("Error in handlePutFile: "+e);
            return HttpResponse.internalServerErrorResponse();
        }
        return new HttpResponse(HttpStatusCode.CREATED, "text/html", getHTMLFile("put_success.html"));
    }
}
