package org.firebug.spring.boot.tx.service;

import org.firebug.spring.boot.tx.repository.dao.AccountDAO;
import org.firebug.spring.boot.tx.repository.entity.AccountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sun.plugin.security.JDK11ClassFileTransformer;

import javax.annotation.Resource;
import java.io.IOException;


/**
 * @Transactional
 * transactionManager: 具体要看ORM框架：
 *                    JPA -> JpaTransactionManager
 *                    JTA -> JtaTransactionManager
 *                   JDBC -> DataSourceTransactionManager
 *              hibernate -> HibernateTransactionManager
 * read-only="false" (read-only="true"指只读事务：提升性能,读一致性,无法写入)
 * timeout="-1"      (方法超时：{timeout}秒，全部回滚与no-rollback-for无关)
 * no-rollback-for="Throwable"     ({no-rollback-for}不回滚)
 * rollback-for="RuntimeException,Error" ({rollback-for}指定异常回滚)
 * isolation="DEFAULT"	 全局设置 或 数据库默认 隔离级别(mysql: 重复读)
 * "READ_UNCOMMITTED"    未提交读
 * "READ_COMMITTED"      提交读
 * "REPEATABLE_READ"     重复读
 * "SERIALIZABLE"        序列化
 * <p>
 * propagation=
 * "REQUIRED"	   (无 => T1)  or (T1 => T1)
 * "REQUIRES_NEW"  (无 => T1)   or (T1 => T2): 同一事务内才能保证事务一致性
 * "SUPPORTS"      (无 => 无)   or (T1 => T1)
 * "NOT_SUPPORTED" (无 => 无)   or (T1 => 无)
 * "MANDATORY"     (无 => 异常) or (T1 => T1)：  org.springframework.transaction.IllegalTransactionStateException: No existing transaction found for transaction marked with propagation 'mandatory'
 * "NEVER"         (无 => 无)   or (T1 => 异常)：org.springframework.transaction.IllegalTransactionStateException: Existing transaction found for transaction marked with propagation 'never'
 * "NESTED"        (无 => T1)   or (T1 => T1): 嵌套事务回滚不影响外部事务，但外部事务回滚将导致嵌套事务回滚(非EJB标准，spring独有)
 *                                            (SavePoint实现方式，JPA与hibernate不支持：org.springframework.transaction.NestedTransactionNotSupportedException)
 */
@Service
public class TransactionService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private TransactionService transactionService;
    @Resource
    private AccountDAO accountDAO;

    // ---华丽的分割线-------------------------------------------------------------------------
    public void transaction_0() {
//        this.transaction_0_0();
        transactionService.transaction_0_0();
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_0_0() {
        incrementBalance(1);
        logger.info("此处是否事务执行：");
    }

    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_1() {
        transaction_1_0();
    }
    public void transaction_1_0() {
        incrementBalance(1);
        logger.info("此处是否事务执行：");
    }

    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED)
    public void transaction_2() {
        // select * from information_schema.innodb_trx;
        logger.info("此处事务是否打开：");
        incrementBalance(1);
        logger.info("此处事务是否关闭：");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected void transaction_3() {
        logger.info("此方法的有什么问题：");
        incrementBalance(1);
    }

    // ---华丽的分割线-------------------------------------------------------------------------
    // https://www.cnblogs.com/jpfss/p/9151797.html
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public void readOnly() {
        Integer id = 1;
        System.out.println("balance_0: " + accountDAO.findById(id).get().getBalance());
        transactionService.readOnly_0(id);
        System.out.println("balance_1: " + accountDAO.findById(id).get().getBalance());
        // 运行结果数据库是否更新成功:
        // select * from t_account where id=1;
    }
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void readOnly_0(Integer id) {
        incrementBalance(id);
    }

    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, timeout = 10)
    public void timeout() throws Throwable {
        Thread.sleep(3000L);
        for (int i = 0; i < 11; i++) {
            System.out.println("sleep: " + i);
            Thread.sleep(1000L); // 哪个sleep 会 timeout:
            System.out.println("id=" + accountDAO.findById(i+1).get().getId());
        }
        // timeout针对 方法
    }

    // ---华丽的分割线-------------------------------------------------------------------------
    public void isolation() {
//        TRANSACTION_READ_UNCOMMITTED
//        TRANSACTION_READ_COMMITTED
//        TRANSACTION_REPEATABLE_READ
//        TRANSACTION_SERIALIZABLE

        // SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
        // set autocommit=0;
        // start transaction;
        System.out.println(accountDAO.findById(1).get().getBalance()); // 0
        // update t_account set balance=balance+1 where id=1;
        System.out.println(accountDAO.findById(1).get().getBalance()); // ?
        // commit;
        System.out.println(accountDAO.findById(1).get().getBalance()); // ?
        logger.info("不开事务前提下是否有隔离级别：");
        logger.info("如有隔离级别是哪个：");
    }

    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public void isolation(Integer id) {
        accountDAO.findById(id);
        logger.info("entrance_e 隔离级别：");
        // 4种隔离级别差异见excel
    }

    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class})
    public void propagation_0() throws Throwable {
        accountDAO.findById(1);
        propagation_0_0();
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Throwable.class})
    public void propagation_0_0() throws Throwable {
        accountDAO.findById(1);
        propagation_0_1();
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Throwable.class})
    public void propagation_0_1() throws Throwable {
        accountDAO.findById(1);
        logger.info("这里数据库一共开几个事务: ");
//        select * from information_schema.innodb_trx;
    }

    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void propagation_1() throws Throwable {
        incrementBalance(1);              //      执行成功, 是否回滚：N
        transactionService.propagation_1_0(); //      执行成功, 是否回滚：N
        transactionService.propagation_1_1(); // 抛出Throwable, 是否回滚：Y
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Throwable.class})
    public void propagation_1_0() throws Throwable {
        incrementBalance(2);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Throwable.class})
    public void propagation_1_1() throws Throwable {
        incrementBalance(3);
        throw new Throwable();
    }
    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class})
    public void entrance_j() throws Throwable {
        incrementBalance(1);         // 是否回滚: Y
        transactionService.called_j_0(); // 是否回滚: Y
        transactionService.called_j_1(); // 是否回滚: Y
        logger.info("entrance_j 是否回滚：");
        logger.info("called_j_0 是否回滚：");
        logger.info("called_j_1 是否回滚：");
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class}, noRollbackFor = {Exception.class})
    public void called_j_0() throws Throwable {
        incrementBalance(2);
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void called_j_1() throws Throwable {
        incrementBalance(3);
        throw new Exception();
    }
    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {}, noRollbackFor = {})
    public void rollback() throws Throwable {
        incrementBalance(1);
        // 1. throw new Throwable()            是否回滚：N
        // 2. throw new Exception()            是否回滚：N
        // 3. throw new IOException()          是否回滚：N
        // 4. throw new RuntimeException()     是否回滚：Y
        // 5. throw new NullPointerException() 是否回滚：Y
        // 6. throw new Error()                是否回滚：Y
    }

        // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class}, noRollbackFor = {Throwable.class})
    public void rollback_tt(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class}, noRollbackFor = {Exception.class})
    public void rollback_te(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class}, noRollbackFor = {IOException.class})
    public void rollback_ti(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class}, noRollbackFor = {RuntimeException.class})
    public void rollback_tr(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class}, noRollbackFor = {NullPointerException.class})
    public void rollback_tn(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class}, noRollbackFor = {})
    public void rollback_td(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class}, noRollbackFor = {Throwable.class})
    public void rollback_et(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class}, noRollbackFor = {Exception.class})
    public void rollback_ee(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class}, noRollbackFor = {IOException.class})
    public void rollback_ei(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class}, noRollbackFor = {RuntimeException.class})
    public void rollback_er(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class}, noRollbackFor = {NullPointerException.class})
    public void rollback_en(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class}, noRollbackFor = {})
    public void rollback_ed(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {IOException.class}, noRollbackFor = {Throwable.class})
    public void rollback_it(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {IOException.class}, noRollbackFor = {Exception.class})
    public void rollback_ie(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {IOException.class}, noRollbackFor = {IOException.class})
    public void rollback_ii(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {IOException.class}, noRollbackFor = {RuntimeException.class})
    public void rollback_ir(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {IOException.class}, noRollbackFor = {NullPointerException.class})
    public void rollback_in(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {IOException.class}, noRollbackFor = {})
    public void rollback_id(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class}, noRollbackFor = {Throwable.class})
    public void rollback_rt(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class}, noRollbackFor = {Exception.class})
    public void rollback_re(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class}, noRollbackFor = {IOException.class})
    public void rollback_ri(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class}, noRollbackFor = {RuntimeException.class})
    public void rollback_rr(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class}, noRollbackFor = {NullPointerException.class})
    public void rollback_rn(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class}, noRollbackFor = {})
    public void rollback_rd(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {NullPointerException.class}, noRollbackFor = {Throwable.class})
    public void rollback_nt(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {NullPointerException.class}, noRollbackFor = {Exception.class})
    public void rollback_ne(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {NullPointerException.class}, noRollbackFor = {IOException.class})
    public void rollback_ni(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {NullPointerException.class}, noRollbackFor = {RuntimeException.class})
    public void rollback_nr(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {NullPointerException.class}, noRollbackFor = {NullPointerException.class})
    public void rollback_nn(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {NullPointerException.class}, noRollbackFor = {})
    public void rollback_nd(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {}, noRollbackFor = {Throwable.class})
    public void rollback_dt(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {}, noRollbackFor = {Exception.class})
    public void rollback_de(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {}, noRollbackFor = {IOException.class})
    public void rollback_di(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {}, noRollbackFor = {RuntimeException.class})
    public void rollback_dr(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {}, noRollbackFor = {NullPointerException.class})
    public void rollback_dn(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {}, noRollbackFor = {})
    public void rollback_dd(Integer id, Throwable t) throws Throwable {
        incrementBalance(id);
        throw t;
    }
    // ---华丽的分割线-------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class})
    public void reset() {
        accountDAO.reset();
    }

    public void incrementBalance(Integer id) {
        AccountEntity entity = accountDAO.findById(id).get();
        entity.setBalance(entity.getBalance() + 1);
        accountDAO.saveAndFlush(entity);
    }
//    总结：
//      1. 数据库存储引擎支持事务：InnoDB
//      2. 隔离级别：4种
//         DEFAULT
//         READ_UNCOMMITTED    未提交读
//         READ_COMMITTED      提交读
//         REPEATABLE_READ     重复读
//         SERIALIZABLE        序列化
//      3. 代理对象 + @Transactional
//      4. 回滚事务
}
