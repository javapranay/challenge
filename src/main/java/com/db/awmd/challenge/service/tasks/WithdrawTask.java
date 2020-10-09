package com.db.awmd.challenge.service.tasks;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.Callable;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;

public class WithdrawTask implements Callable<Boolean>{
	AccountsRepository accountsRepository;
	String accountFromId;
	BigDecimal fund;
	
	public WithdrawTask(String accountFromId, BigDecimal fund, AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
		this.accountFromId = accountFromId;
		this.fund = fund;
	}

	
	@Override
	public Boolean call() {
		Map<String, Account> accounts = ((AccountsRepositoryInMemory)accountsRepository).getAccounts();
		Account oldAccount = null;
		Account newAccount = null;
		if(accounts.containsKey(accountFromId)) {
			oldAccount = accounts.get(accountFromId);
			newAccount = new Account(accountFromId, oldAccount.getBalance().subtract(fund));
		}
		return accounts.replace(accountFromId, oldAccount, newAccount);
	}

}
