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
                int type = 0;
                int id = Server.createTask(title,desc,type);
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
            }else if(operation.action==UrlAction.NEW_PETITION){
                String title = jsonMap.get("title");
                String desc = jsonMap.get("desc");
                int type = 1;
                int id = Server.createTask(title,desc,type);
                Server.response(exchange,200,""+id);
            }else if(operation.action==UrlAction.NEW_TASKITEM){
                int item = Server.string2id(jsonMap.get("item"));
                int count = Server.string2id(jsonMap.get("count"));
                int id = Server.createTaskItem(operation.id,item,count);
                Server.response(exchange,200,""+id);
            }else{
                // Send a response 
                Server.response(exchange,400,"Received POST request at /task with invalid format");
            }
        }else if("GET".equals(requestMethod)){
            if(operation.action==UrlAction.TASK){
                String task = Utils.taskToJson(Server.getTask(operation.id));
                Server.response(exchange,200,task);
            }else if(operation.action==UrlAction.ALL_TASKS){
                String allTasks = Utils.multipleTasksToJson(Server.getAllTasks());
                Server.response(exchange,200,allTasks);
            }else if(operation.action==UrlAction.TASKSTEP){
                String step = Utils.taskStepToJson(Server.getTaskStep(operation.id));
                Server.response(exchange,200,step);
            }else if(operation.action==UrlAction.ALL_TASKSTEPS){
                String allSteps = Utils.multipleTaskStepsToJson(Server.getTaskStepsFromTask(operation.id));
                Server.response(exchange,200,allSteps);
            }else if(operation.action==UrlAction.TASKITEM){
                String item = Utils.taskItemToJson(Server.getTaskItemFromTask(operation.id));
                Server.response(exchange,200,item);
            }else if(operation.action==UrlAction.ALL_TASKITEMS){
                String allItems = Utils.multipleTaskItemsToJson(Server.getTaskItemFromTask(operation.id));
                Server.response(exchange,200,allItems);
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

        int n = -2;

        if(Utils.compareURL(path, "/task")!=-2) operation.set(-1,UrlAction.ALL_TASKS);
        if(Utils.compareURL(path, "/task/new")!=-2) operation.set(-1,UrlAction.NEW_TASK);
        n = Utils.compareURL(path, "/task/?"); if(n!=-2) operation.set(n,UrlAction.TASK);
        if(Utils.compareURL(path, "/task/step/new")!=-2) operation.set(-1,UrlAction.NEW_TASKSTEP);
        n = Utils.compareURL(path, "/task/step/?"); if(n!=-2) operation.set(n,UrlAction.TASKSTEP);
        n = Utils.compareURL(path, "/task/?/steps"); if(n!=-2) operation.set(n,UrlAction.ALL_TASKSTEPS);
        if(Utils.compareURL(path, "/task/petition/new")!=-2) operation.set(-1,UrlAction.NEW_PETITION);
        n = Utils.compareURL(path, "/task/petition/?/item/new"); if(n!=-2) operation.set(n,UrlAction.NEW_TASKITEM);
        n = Utils.compareURL(path, "/task/petition/?/item"); if(n!=-2) operation.set(n,UrlAction.ALL_TASKITEMS);
        n = Utils.compareURL(path, "/task/petition/item/delete/?"); if(n!=-2) operation.set(n,UrlAction.DELETE_TASKITEM);//TODO
        
        return operation;
    }
}