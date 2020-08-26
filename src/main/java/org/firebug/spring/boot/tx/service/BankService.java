package org.firebug.spring.boot.tx.service;

import org.firebug.spring.boot.tx.repository.dao.AccountDAO;
import org.firebug.spring.boot.tx.repository.entity.AccountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BankService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private BankService bankRequiredService;
    @Resource
    private AccountDAO accountDAO;
//    @Resource
//    private JpaTransactionManager transactionManager;
//
//    @PostConstruct
//    public void init() {
//        transactionManager.setNestedTransactionAllowed(true);
//    }

//    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
//    @Transactional(propagation = Propagation.REQUIRED, timeout = 10)
    public AccountEntity findById(Integer id) throws Throwable {
        return accountDAO.findById(id).get();
    }

    /**
     * 存款
     */
//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.NEVER, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.NESTED, rollbackFor = {Throwable.class})
    public void deposit(AccountEntity account, int amount) throws Throwable {
        account.setBalance(account.getBalance() + amount);
        accountDAO.save(account);
    }

    /**
     * 取款
     */
//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.NEVER, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.NESTED, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = {Throwable.class})
    public void withdraw(AccountEntity account, int amount) throws Throwable {
        if (account.getBalance() < amount) {
            throw new RuntimeException("余额不足:" + account.getBalance());
        }
        account.setBalance(account.getBalance() - amount);
        accountDAO.save(account);
    }

    /**
     * 转帐
     */
//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.NEVER, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.NESTED, rollbackFor = {Throwable.class})
//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class})
    public void transfer(Integer withdrawId, Integer depositId, int amount) throws Throwable {
        AccountEntity depositAccount = bankRequiredService.findById(depositId);
        bankRequiredService.deposit(depositAccount, amount);   // 存
        AccountEntity withdrawAccount = bankRequiredService.findById(withdrawId);
        bankRequiredService.withdraw(withdrawAccount, amount); // 取
        logger.info("转出人：{}, 转入人：{}, 金额：{}", withdrawAccount, depositAccount, amount);
    }
}
