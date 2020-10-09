package com.db.awmd.challenge.service.tasks;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.Callable;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;

public class DepositeTask implements Callable<Boolean>{
	AccountsRepository accountsRepository;
	String accountToId;
	BigDecimal fund;
	
	public DepositeTask(String accountToId, BigDecimal fund, AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
		this.accountToId = accountToId;
		this.fund = fund;
	}

	@Override
	public Boolean call() {
		Map<String, Account> accounts = ((AccountsRepositoryInMemory)accountsRepository).getAccounts();
		Account oldAccount = null;
		Account newAccount = null;
		if(accounts.containsKey(accountToId)) {
			oldAccount = accounts.get(accountToId);
			newAccount = new Account(accountToId, oldAccount.getBalance().add(fund));
		}
		return accounts.replace(accountToId, oldAccount, newAccount);
	}
}
