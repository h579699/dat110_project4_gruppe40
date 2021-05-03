package no.hvl.dat110.ac.restservice;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;
import static spark.Spark.post;
import static spark.Spark.delete;

import com.google.gson.Gson;

/**
 * Hello world!
 *
 */
public class App {
	
	static AccessLog accesslog = null;
	static AccessCode accesscode = null;
	
	public static void main(String[] args) {

		if (args.length > 0) {
			port(Integer.parseInt(args[0]));
		} else {
			port(8080);
		}

		// objects for data stored in the service
		accesslog = new AccessLog();
		accesscode  = new AccessCode();
		
		//blir utført etter hver request, og setter response type
		after((req, res) -> {
  		  res.type("application/json");
  		});
		
		// for basic testing purposes
		get("/accessdevice/hello", (req, res) -> {
			
		 	Gson gson = new Gson();
		 	
		 	return gson.toJson("IoT Access Control Device");
		});
		
		// TODO: implement the routes required for the access control service
		// as per the HTTP/REST operations described in the project description
		
		//record an access attempt by storing the log-message contained in the body of the HTTP request in the cloud service
		post("/accessdevice/log", (req, res) -> {
			Gson gson = new Gson();
			AccessMessage msg = gson.fromJson(req.body(), AccessMessage.class);
			int id = accesslog.add(msg.getMessage());
			AccessEntry entry = accesslog.get(id);
			
			return gson.toJson(entry);
		});
		
		//return a JSON-representation of all access log entries in the system
		get("/accessdevice/log", (req, res) -> {
			return accesslog.toJson();
		});
		
		//return a JSON representation of the access entry identified by :id
		get("/accessdevice/log/:id", (req, res) -> {
			String idStr = req.params(":id");
			int id = Integer.parseInt(idStr);
			
			AccessEntry entry = accesslog.get(id);
			Gson gson = new Gson();
			if(entry != null) {
				return gson.toJson(entry);
			}
			
			return gson.toJson("No entry in accesslog with given id!");
			
		});
		
		//update the access code stored in the cloud service to a combination of the 1 and 2 buttons.
		put("/accessdevice/code", (req, res) -> {
			Gson gson = new Gson();
        	
        	accesscode = gson.fromJson(req.body(), AccessCode.class);
        
            return gson.toJson(accesscode);
		});
		
		//return a JSON-representation of the current access code stored in the server
		get("/accessdevice/code", (req, res) -> {
			Gson gson = new Gson();
        
            return gson.toJson(accesscode);
		});
		
		// delete all entries in the access log and a return a JSON-representation of the empty access log in the body of the HTTP response.
		delete("/accessdevice/log", (req, res) -> {
			accesslog.clear();
			return accesslog.toJson();
		});
		
    }
    
}
