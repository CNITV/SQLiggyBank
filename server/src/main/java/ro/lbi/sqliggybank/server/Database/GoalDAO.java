package ro.lbi.sqliggybank.server.Database;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ro.lbi.sqliggybank.server.Core.Goal;
import ro.lbi.sqliggybank.server.Core.PiggyBank;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GoalDAO extends AbstractDAO<Goal> {
	/**
	 * The constructor for the goal DAO.
	 *
	 * @param factory The session factory required. Handed in by Dropwizard.
	 */
	public GoalDAO(SessionFactory factory) {
		super(factory);
	}

	/**
	 * Finds goal by ID.
	 *
	 * @param id The UUID of the goal.
	 * @return Optional of any found goal.
	 */
	public Optional<Goal> findByID(UUID id) {
		return Optional.ofNullable(get(id));
	}

	/**
	 * Finds all the goals belonging to a specific piggy bank.
	 *
	 * @param bank The piggy bank to look for goals in.
	 * @return A List of all the goals belonging to the bank provided.
	 */
	@SuppressWarnings("unchecked")
	public List<Goal> findByBank(PiggyBank bank) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.Group.findByName");
		query.setParameter("bank", bank);
		return list((Query<Goal>) query);
	}

	/**
	 * Finds a goal belonging to a specific piggy bank by name.
	 *
	 * @param bank     The piggy bank to look for the goal in.
	 * @param goalName The name of the goal.
	 * @return An Optional of any found goal.
	 */
	public Optional<Goal> findByNameAndBank(PiggyBank bank, String goalName) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.Goal.findByBankAndName");
		query.setParameter("bank", bank);
		query.setParameter("passed_goal", goalName);
		Goal goal = (Goal) query.getSingleResult();
		return Optional.of(goal);
	}

	/**
	 * Creates a goal in the database.
	 *
	 * @param goal The goal to be created.
	 */
	public void create(Goal goal) {
		persist(goal);
	}

	/**
	 * Updates a goal from the database.
	 *
	 * @param goal The goal to be updated.
	 */
	public void update(Goal goal) {
		currentSession().update(goal);
	}

	/**
	 * Deletes a goal from the database.
	 *
	 * @param goal The goal to be deleted.
	 */
	public void delete(Goal goal) {
		currentSession().delete(goal);
	}

	/**
	 * Finds all goals from the database. Equivalent to "SELECT * FROM goals".
	 * <p>
	 * WARNING: At large scales, this can crash the server. There might be MANY goals in the database.
	 *
	 * @return A List of all the goals.
	 */
	@SuppressWarnings("unchecked")
	public List<Goal> findAll() {
		return list((Query<Goal>) namedQuery("ro.lbi.sqliggybank.server.Core.Goal.findAll"));
	}
}
