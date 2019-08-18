package ro.lbi.sqliggybank.server.Resources;

import ro.lbi.sqliggybank.server.Responses.NotFoundResponse;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

/**
 * EndpointExceptionHandler is a helper class which processes exceptions sent by
 * various endpoints in the server.
 *
 * Most exceptions thrown by the server differentiate themselves using the
 * message, which requires parsing if we receives exceptions of the same kind.
 * As such, this helper class offers options to differentiate.
 *
 * @author StormFireFox1
 * @since 2019-05-23
 *
 */
class EndpointExceptionHandler {
	/**
	 * Parses any NotFoundException thrown when querying for a piggy bank.
	 *
	 * When querying for a bank, a NotFoundException can be thrown whenever
	 * looking for a group or for the bank itself. This parses whichever
	 * exception is thrown and returns an appropriate Response to be shown
	 * to the client.
	 *
	 * @param groupName The name of the group we queried for.
	 * @param bankName  The name of the bank we queried for.
	 * @param e         The NotFoundException thrown.
	 *
	 * @return A Response reflecting the NotFoundException.
	 */
	static Response parseBankNotFound(String groupName, String bankName, NotFoundException e) {
		if (e.getMessage().split(" ")[0].equals("Group")) {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The group " + groupName + " could not be found!"))
					.build();
		} else {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The piggy bank " + bankName + " could not be found!"))
					.build();
		}
	}

	/**
	 * Parses any NotFoundException thrown when querying for a goal.
	 *
	 * When querying for a bank, a NotFoundException can be thrown whenever
	 * looking for a group, a bank, or the goal itself. This parses
	 * whichever exception is thrown and returns an appropriate Response to
	 * be shown to the client.
	 *
	 * @param groupName The name of the group we queried for.
	 * @param bankName  The name of the bank we queried for.
	 * @param goalName  The name of the goal we queried for.
	 * @param e         The NotFoundException thrown.
	 *
	 * @return A Response reflecting the NotFoundException.
	 */
	static Response parseGoalNotFound(String groupName, String bankName, String goalName, NotFoundException e) {
		if (e.getMessage().split(" ")[0].equals("Group")) {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The group " + groupName + " could not be found!"))
					.build();
		} else if (e.getMessage().split(" ")[0].equals("Piggy")) {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The piggy bank " + bankName + " could not be found!"))
					.build();
		} else {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The goal " + goalName + " could not be found!"))
					.build();
		}
	}
}
