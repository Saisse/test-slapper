package jp.saisse;

import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class JavaServerResource extends ServerResource {
	
	public static void start() throws Exception {
		new Server(Protocol.HTTP, 8182, JavaServerResource.class).start();
	}

//	public static void main(String[] args) throws Exception {
//		start();
//	}
	
	@Get
	public String toString() {
		return "hello. world!";
	}
}
