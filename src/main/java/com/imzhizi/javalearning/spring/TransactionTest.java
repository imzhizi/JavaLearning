package com.imzhizi.javalearning.spring;

import org.junit.Test;

/**
 * created by zhizi
 * on 3/17/20 23:09
 */
public class TransactionTest {
    /**
     * Spring 主要有两种实现事务的方式，是编程式和声明式
     * 编程式意味着需要手动开始事务，手动关闭事务，在应当的时候commit 或 rollback
     * 声明式则不同，声明式基于AOP，只需要告知哪些方法需要事务保护，即可达到事务的效果
     */
    @Test
    public void 编程式事务() {
        //        TransactionDefinition def = new DefaultTransactionDefinition();
        //        TransactionStatus status = transactionManager.getTransaction(def);
        //        try {
        //             blabla....
        //            transactionManager.commit(status);
        //        } catch (DataAccessException e) {
        //            transactionManager.rollback(status);
        //        }
    }

    /**
     * 通过AOP实现，定义好通知和切面，然后在方法执行异常时就会调用
     */
    @Test
    public void 声明式事务() {
        //<tx:advice id="txAdvice"  transaction-manager="transactionManager">
        //      <tx:attributes>
        //      <tx:method name="create"/>
        //      </tx:attributes>
        //   </tx:advice>
        //
        //   <aop:config>
        //      <aop:pointcut id="createOperation"
        //        expression="execution(* com.tutorialspoint.StudentJDBCTemplate.create(..))"/>
        //      <aop:advisor advice-ref="txAdvice" pointcut-ref="createOperation"/>
        //   </aop:config>
    }
}