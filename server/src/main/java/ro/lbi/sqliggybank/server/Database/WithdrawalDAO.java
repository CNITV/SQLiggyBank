package ro.lbi.sqliggybank.server.Database;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ro.lbi.sqliggybank.server.Core.Withdrawal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WithdrawalDAO extends AbstractDAO<Withdrawal> {
	/**
	 * The constructor for the withdrawal DAO.
	 *
	 * @param factory The session factory required. Handed in by Dropwizard.
	 */
	public WithdrawalDAO(SessionFactory factory) {
		super(factory);
	}

	/**
	 * Finds withdrawal by ID.
	 *
	 * @param id The UUID of the withdrawal.
	 * @return Optional of any found withdrawal.
	 */
	public Optional<Withdrawal> findByID(UUID id) {
		return Optional.ofNullable(get(id));
	}

	/**
	 * Finds withdrawals by group and bank.
	 *
	 * @param group The name of the group to look for piggy banks in.
	 * @param bank  The name of the piggy bank to look for withdrawals in.
	 *
	 * @return A List of Withdrawal objects. An empty List is returned if 
	 * 	   no withdrawals are found in the provided piggy bank.
	 */
	@SuppressWarnings("unchecked")
	public List<Withdrawal> findByGroupAndBank(String group, String bank) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.Withdrawal.findByGroupAndBank");
		query.setParameter("group", group);
		query.setParameter("bank", bank);
		return query.getResultList();
	}

	/**
	 * Checks the presence of a withdrawal in a database. Used to differentiate transactions.
	 *
	 * @param id The UUID of the withdrawal.
	 * @return True if present, false if otherwise.
	 */
	public boolean isWithdrawal(UUID id) {
		return findByID(id).isPresent();
	}

	/**
	 * Creates a withdrawal in the database.
	 *
	 * @param withdrawal The withdrawal to be created.
	 */
	public void create(Withdrawal withdrawal) {
		persist(withdrawal);
	}

	/**
	 * Updates a withdrawal from the database.
	 *
	 * @param withdrawal The withdrawal to be updated.
	 */
	public void update(Withdrawal withdrawal) {
		currentSession().update(withdrawal);
	}

	/**
	 * Finds all withdrawals from the database. Equivalent to "SELECT * FROM withdrawals".
	 * <p>
	 * WARNING: At large scales, this can crash the server. There might be MANY withdrawals in the database.
	 *
	 * @return A List of all the withdrawals.
	 */
	@SuppressWarnings("unchecked")
	public List<Withdrawal> findAll() {
		return list((Query<Withdrawal>) namedQuery("ro.lbi.sqliggybank.server.Core.Withdrawal.findAll"));
	}
}
