package com.example.test_task.managers;

import com.example.test_task.exception.AuthException;
import com.example.test_task.exception.DepositException;
import com.example.test_task.models.Client;
import com.example.test_task.models.Deposit;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.tokens.ScalarToken;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


class DepositManagerTest {
    DepositManager depositManager = new DepositManager();
    AccountManager accountManager = new AccountManager();
    @Test
    void addDeposit() {
        try {
            Client client = new Client(0,"Yaroslav","Kladiev");
            Date date = new Date();
            String token = accountManager.authorize("name7", "pass",date);
            depositManager.addDeposit(client, 300000, 5, 2,
                    40, new Date("October 10, 2020 15:00:00"),false,token);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DepositException e) {
            e.printStackTrace();
        } catch (AuthException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getClientDeposits() {
        Client client = new Client(0,"Yaroslav","Kladiev");
        try {
            Date date = new Date();
            String token = accountManager.authorize("name7", "pass",date);
            List<Deposit> deposits=depositManager.getClientDeposits(client,token);
            for (Deposit el:deposits) {
                System.out.println(el.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getAllDeposits() {
        try {
            List<Deposit> deposits = depositManager.getAllDeposits();
            for (Deposit el:deposits) {
                System.out.println(el.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();}
    }

    @Test
    void getEarnings() {
        //DepositManager depositManager = new DepositManager();
        try {
            Deposit deposit = null;
            Date date = new Date();
            String token = accountManager.authorize("name7", "pass",date);
            deposit = new Deposit(0,0,300000, 5, 2,
                    40, new Date("October 10, 2020 15:00:00"),false);
            Date currentDate = new Date("November 11, 2020 15:00:00");
            System.out.println(depositManager.getEarnings(deposit,currentDate,token));
        } catch (DepositException e) {
            e.printStackTrace();
        } catch (AuthException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void removeDeposit() {
        Deposit deposit = null;
        try {
            deposit = new Deposit(0,0, 300000, 5, 2,
                    40, new Date("October 10, 2020 15:00:00"),false);
//            deposit.setId(2);
        Date date = new Date("November 17, 2020 15:00:00");
            Date dateToken = new Date();
            String token = accountManager.authorize("name7", "pass",dateToken);
            System.out.println(depositManager.removeDeposit(deposit, date,token));
            getAllDeposits();
        }catch (DepositException e) {
            e.printStackTrace();
        } catch (AuthException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void writeDeposits() {
    }
}