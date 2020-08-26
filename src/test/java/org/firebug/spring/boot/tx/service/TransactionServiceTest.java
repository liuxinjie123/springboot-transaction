package org.firebug.spring.boot.tx.service;

import org.firebug.spring.boot.tx.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TransactionServiceTest {
    @Resource
    private TransactionService transactionService;

    @Test
    public void test() throws Throwable {
        transactionService.reset();
//        transactionService.transaction_0();
//        transactionService.transaction_1();
//        transactionService.transaction_2();
//        transactionService.readOnly();
        transactionService.timeout();
//        transactionService.isolation();
//        transactionService.isolation(1);
//        transactionService.propagation_0();
    }
    @Test
    public void testRollback() throws Throwable {
        transactionService.reset();
        try {
            transactionService.rollback_dd(1, new Throwable());
        }catch (Throwable t){}
        try {
            transactionService.rollback_dd(2, new Exception());
        }catch (Throwable t){}
        try {
            transactionService.rollback_dd(3, new IOException());
        }catch (Throwable t){}
        try {
            transactionService.rollback_dd(4, new SQLException());
        }catch (Throwable t){}
        try {
            transactionService.rollback_dd(5, new RuntimeException());
        }catch (Throwable t){}
        try {
            transactionService.rollback_dd(6, new NullPointerException());
        }catch (Throwable t){}
        try {
            transactionService.rollback_dd(7, new ArithmeticException());
        }catch (Throwable t){}
        try {
            transactionService.rollback_dd(8, new Error());
        }catch (Throwable t){}
    }
}