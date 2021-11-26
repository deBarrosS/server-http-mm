# server-http-mm

Http Server proof of concept for network programming.

## Javadoc
 You can consult the javadoc by [clicking this link](https://debarross.github.io/server-http-mm/)
 
## Usage

### `GET`
To test get requests you can consult the URL http://localhost:3000/pages/todo.html which features a page showcasing some features of the server.

### `POST`
You can test post requests by adding elements to the todo list on the html page

### `PUT`
Our put request allows you to save text files on the server. You need to provide the name of the file and its text content.

Example curl request for testing:

```
curl -XPUT -H 'Accept: text/html' -d 'content=Ceci est un test du PUT!' 'http://localhost:3000/myfile.txt'
```

You can test it by curling

### `DELETE`
Our delete method allows you to delete files on the server. You just have to provide the name of the file.

Example curl request for testing (deletes the file that was created using the previous PUT request)
```
curl -XDELETE -H 'Accept: text/html' 'http://localhost:3000/myfile.txt'
```


