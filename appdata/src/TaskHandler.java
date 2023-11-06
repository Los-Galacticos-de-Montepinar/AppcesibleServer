import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;
import java.io.IOException;

public class TaskHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Get the request method (POST, GET, etc.)
        String requestMethod = exchange.getRequestMethod();

        UrlOperation operation = analizeUrl(exchange.getRequestURI().getPath());
        Map<String, String> jsonMap = Server.requestJson(exchange);
        if ("POST".equals(requestMethod)) {
            if(operation.action==UrlAction.TASK){
                Server.response(exchange,200,"");
            }else if(operation.action==UrlAction.NEW_TASK){
                String title = jsonMap.get("title");
                String desc = jsonMap.get("desc");
                int id = Server.createTask(title,desc);
                Server.response(exchange,200,""+id);
            }else if(operation.action==UrlAction.TASKSTEP){
                Server.response(exchange,200,"");
            }else if (operation.action==UrlAction.NEW_TASKSTEP){
                String desc = jsonMap.get("desc");
                String media = jsonMap.get("media");
                int order = Server.string2id(jsonMap.get("order"));
                int idTask = Server.string2id(jsonMap.get("taskId"));
                Server.createTaskStep(idTask,desc,order,media);
                Server.response(exchange,200,"Created step");
            }else{
                // Send a response 
                Server.response(exchange,400,"Received POST request at /task with invalid format");
            }
        }else if("GET".equals(requestMethod)){
            if(operation.action==UrlAction.TASK){
                String task = Server.taskToJson(Server.getTask(operation.id));
                Server.response(exchange,200,task);
            }else if(operation.action==UrlAction.ALL_TASKS){
                String allTasks = Server.multipleTasksToJson(Server.getAllTasks());
                Server.response(exchange,200,allTasks);
            }else if(operation.action==UrlAction.TASKSTEP){
                String step = Server.taskStepToJson(Server.getTaskStep(operation.id));
                Server.response(exchange,200,step);
            }else if(operation.action==UrlAction.ALL_TASKSTEPS){
                String allSteps = Server.multipleTaskStepsToJson(Server.getTaskStepsFromTask(operation.id));
                Server.response(exchange,200,allSteps);
            }else{
                // Send a response 
               Server.response(exchange,400,"Received GET request at /task with invalid format");                
            }
        }else {
            // Handle other HTTP methods or provide an error response
            Server.response(exchange,405,"Unsupported HTTP method");
        }
    }

    private UrlOperation analizeUrl(String path){
        UrlOperation operation = new UrlOperation(0, UrlAction.ERROR);
        try {
            String[] parts = path.split("/");
            int size = parts.length;
            String idString = parts[size-1];

            if(size==2){
                if(idString.equals("task")){
                    operation.set(-1,UrlAction.ALL_TASKS);
                }
            } else if(size==3){
                if(parts[1].equals("task")){
                    if(idString.equals("new")){
                        operation.set(-1,UrlAction.NEW_TASK);
                    }else{
                        operation.set(Integer.parseInt(idString),UrlAction.TASK);
                    }
                }
            } else if(size==4){
                if(parts[1].equals("task")){
                    if(parts[2].equals("step")){
                        if(idString.equals("new")){
                            operation.set(-1,UrlAction.NEW_TASKSTEP);
                        }else{
                            operation.set(Integer.parseInt(idString),UrlAction.TASKSTEP);
                        }
                    }else{
                        if(idString.equals("steps")){
                            operation.set(Integer.parseInt(parts[2]),UrlAction.ALL_TASKSTEPS);
                        }
                    }
                }
            }

        }catch(NumberFormatException e){
            return operation; //Invalid URL format
        }
        return operation;
    }
}