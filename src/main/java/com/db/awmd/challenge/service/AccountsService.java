package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.tasks.DepositeTask;
import com.db.awmd.challenge.service.tasks.WithdrawTask;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	public Boolean transferOfMoney(String accountFromId, String accountToId, BigDecimal fund) {
		if (fund.doubleValue() < 0 || fund.doubleValue() > getAccount(accountFromId).getBalance().doubleValue()
				|| fund.doubleValue() > getAccount(accountToId).getBalance().doubleValue()) {
			return false;
		}else {
			ExecutorService service = Executors.newFixedThreadPool(2);
			service.submit(new DepositeTask(accountToId, fund, accountsRepository));
			service.submit(new WithdrawTask(accountFromId, fund, accountsRepository));
			
			NotificationService emailService = new EmailNotificationService();
			emailService.notifyAboutTransfer(getAccount(accountFromId), fund+" transfered to "+accountToId);
			emailService.notifyAboutTransfer(getAccount(accountToId), fund+" transfered from "+accountFromId);
			return true;
		}	
	}
}
