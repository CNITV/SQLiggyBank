package ro.lbi.sqliggybank.server.Database;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ro.lbi.sqliggybank.server.Core.Deposit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DepositDAO extends AbstractDAO<Deposit> {
	/**
	 * The constructor for the deposit DAO.
	 *
	 * @param factory The session factory required. Handed in by Dropwizard.
	 */
	public DepositDAO(SessionFactory factory) {
		super(factory);
	}

	/**
	 * Finds deposit by ID.
	 *
	 * @param id The UUID of the deposit.
	 * @return Optional of any found deposit.
	 */
	public Optional<Deposit> findByID(UUID id) {
		return Optional.ofNullable(get(id));
	}

	/**
	 * Checks the presence of a deposit in a database. Used to differentiate transactions.
	 *
	 * @param id The UUID of the deposit.
	 * @return True if present, false if otherwise.
	 */
	public boolean isDeposit(UUID id) {
		return findByID(id).isPresent();
	}

	/**
	 * Creates a deposit in the database.
	 *
	 * @param deposit The deposit to be created.
	 */
	public void create(Deposit deposit) {
		persist(deposit);
	}

	/**
	 * Updates a deposit from the database.
	 *
	 * @param deposit The deposit to be updated.
	 */
	public void update(Deposit deposit) {
		currentSession().update(deposit);
	}

	/**
	 * Finds all deposits from the database. Equivalent to "SELECT * FROM deposits".
	 * <p>
	 * WARNING: At large scales, this can crash the server. There might be MANY deposits in the database.
	 *
	 * @return A List of all the deposits.
	 */
	@SuppressWarnings("unchecked")
	public List<Deposit> findAll() {
		return list((Query<Deposit>) namedQuery("ro.lbi.sqliggybank.server.Core.deposit.findAll"));
	}
}
