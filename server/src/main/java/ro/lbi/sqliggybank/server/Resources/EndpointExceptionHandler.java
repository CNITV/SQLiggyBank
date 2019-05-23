package ro.lbi.sqliggybank.server.Resources;

import ro.lbi.sqliggybank.server.Responses.NotFoundResponse;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

class EndpointExceptionHandler {
	static Response parseBankNotFound(String groupName, String bankName, NotFoundException e) {
		if (e.getMessage().split(" ")[0].equals("Group")) {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The group \"" + groupName + "\" could not be found!"))
					.build();
		} else {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The piggy bank \"" + bankName + "\" could not be found!"))
					.build();
		}
	}

	static Response parseGoalNotFound(String groupName, String bankName, String goalName, NotFoundException e) {
		if (e.getMessage().split(" ")[0].equals("Group")) {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The group \"" + groupName + "\" could not be found!"))
					.build();
		} else if (e.getMessage().split(" ")[0].equals("Piggy")) {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The piggy bank \"" + bankName + "\" could not be found!"))
					.build();
		} else {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The goal \"" + goalName + "\" could not be found!"))
					.build();
		}
	}
}
