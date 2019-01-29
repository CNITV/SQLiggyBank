package ro.lbi.sqliggybank.server.Database;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ro.lbi.sqliggybank.server.Core.Group;
import ro.lbi.sqliggybank.server.Core.PiggyBank;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PiggyBankDAO extends AbstractDAO<PiggyBank> {
	/**
	 * The constructor for the piggy bank DAO.
	 *
	 * @param factory The session factory required. Handed in by Dropwizard.
	 */
	public PiggyBankDAO(SessionFactory factory) {
		super(factory);
	}

	/**
	 * Finds piggy bank by ID.
	 *
	 * @param id The UUID of the piggy bank.
	 * @return Optional of any found piggy bank.
	 */
	public Optional<PiggyBank> findByID(UUID id) {
		return Optional.ofNullable(get(id));
	}

	/**
	 * Finds piggy bank by name.
	 *
	 * @param name The name of the piggy bank.
	 * @return Optional of any found piggy bank.
	 */
	public Optional<PiggyBank> findByName(String name) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.PiggyBank.findByName");
		query.setParameter("name", name);
		PiggyBank piggyBank = (PiggyBank) query.getSingleResult();
		return Optional.of(piggyBank);
	}

	public Optional<PiggyBank> findByNameAndGroup(Group group, String name) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.PiggyBank.findByGroupAndName");
		query.setParameter("name", name);
		query.setParameter("passed_group", group);
		PiggyBank piggyBank = (PiggyBank) query.getSingleResult();
		return Optional.of(piggyBank);
	}

	public List findAllBanksInGroup(Group group) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.PiggyBank.findByGroup");
		query.setParameter("passed_group", group);
		return query.getResultList();
	}

	/**
	 * Creates a piggy bank in the database.
	 *
	 * @param piggyBank The piggy bank to be created.
	 */
	public void create(PiggyBank piggyBank) {
		persist(piggyBank);
	}

	/**
	 * Deletes a piggy bank from the database.
	 *
	 * @param piggyBank The piggy bank to be deleted.
	 */
	public void delete(PiggyBank piggyBank) {
		currentSession().delete(piggyBank);
	}

	/**
	 * Updates a piggy bank from the database.
	 *
	 * @param piggyBank The piggy bank to be updated.
	 */
	public void update(PiggyBank piggyBank) {
		currentSession().update(piggyBank);
	}

	/**
	 * Finds all piggy banks from the database. Equivalent to "SELECT * FROM banks".
	 * <p>
	 * WARNING: At large scales, this can crash the server. There might be MANY piggy banks in the database.
	 *
	 * @return A List of all the piggy banks.
	 */
	@SuppressWarnings("unchecked")
	public List<PiggyBank> findAll() {
		return list((Query<PiggyBank>) namedQuery("ro.lbi.sqliggybank.server.Core.PiggyBank.findAll"));
	}
}
