package net.viperfish.journal2.transaction;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.viperfish.journal2.core.AuthenticationManager;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalDatabase;
import net.viperfish.journal2.core.JournalEncryptor;
import net.viperfish.journal2.core.JournalIndexer;
import net.viperfish.journal2.core.JournalService;
import net.viperfish.journal2.crypt.TextIndexFieldEncryptor;
import net.viperfish.journal2.error.FailToStoreCredentialException;

public class TransactionalJournalService implements JournalService {

	private JournalIndexer indexer;
	private JournalEncryptor enc;
	private JournalDatabase db;
	private TransactionalUtils utils;
	private AuthenticationManager auth;
	private TextIndexFieldEncryptor indexCrypt;

	public TransactionalJournalService() {

	}

	@Override
	public synchronized Journal get(Long id) throws ExecutionException {
		GetJournalTransaction trans = new GetJournalTransaction(id, db, enc);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Journal add(Journal t) throws ExecutionException {
		AddJournalTransaction trans = new AddJournalTransaction(t, db, indexer, enc, indexCrypt);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Journal remove(Long id) throws ExecutionException {
		RemoveJournalTransaction trans = new RemoveJournalTransaction(id, db, indexer, enc);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Journal update(Long id, Journal updated) throws ExecutionException {
		UpdateJournalTransaction trans = new UpdateJournalTransaction(db, indexer, enc, indexCrypt, updated);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Collection<Journal> getAll() throws ExecutionException {
		GetAllTransaction trans = new GetAllTransaction(enc, db);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized List<Journal> search(String keyword) throws ExecutionException {
		SearchTransaction trans = new SearchTransaction(indexer, enc, db, indexCrypt, keyword);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Collection<Journal> getRange(Date lower, Date upper) throws ExecutionException {
		GetDateRangeTransaction trans = new GetDateRangeTransaction(lower, upper, db, enc);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Collection<Journal> searchWithinRange(Date lower, Date upper, String keyword)
			throws ExecutionException {
		SearchDateRangeTransaction trans = new SearchDateRangeTransaction(db, indexer, enc, indexCrypt, keyword, lower,
				upper);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized void reCrypt(String password) throws FailToStoreCredentialException {
		List<Journal> decrypted = new LinkedList<>();
		for (Journal i : db.findAll()) {
			decrypted.add(enc.decryptJournal(i));
		}
		auth.setPassword(password);
		for (Journal j : decrypted) {
			enc.encryptJournal(j);
		}
		db.save(decrypted);
		return;
	}

	@Override
	public synchronized void reCrypt() {
		for (Journal i : db.findAll()) {
			db.save(enc.encryptJournal(enc.decryptJournal(i)));
		}

	}

}
