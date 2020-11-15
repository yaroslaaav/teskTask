package com.example.test_task.managers;

import com.example.test_task.exception.AccountException;
import com.example.test_task.exception.AuthException;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.*;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class AccountManager {
    private static CellProcessor[] getProcessors() {
        return new CellProcessor[]{
                new NotNull(), // name не должно быть null
                new NotNull(), // password не должно быть null
        };
    }
    void addAccount(String userName, String password) throws IOException, AccountException {
        if (findNameDuplicate(userName))                                //проверка имени
            throw new AccountException("Данное имя уже занято, попробуйте новое!");
        CsvListWriter listWriter = new CsvListWriter(
                new FileWriter("accounts.csv",true),CsvPreference.STANDARD_PREFERENCE); //открытие файла для записи
        listWriter.write(userName,password);            //запись аккаунта в файл
        listWriter.close();                             //закрытие файла
    }

    /*
      Метод удаляет пользователя системы
     */
    void removeAccount(String userName, String password) throws IOException {
        List<String> accounts = getAllAccounts();
        int idEl=0;                 //счетчик id аккаунта
        boolean find=false;          // переменная показвает найден ли данный аккаунт
        for (String el: accounts) {     //перебор всех аккаунтов
            if (el.contains(userName) && el.contains(password)){    //проверка на наличие в строке имени и пароля аккаунта
                find=true;                  //аккаунт найден
                break;                      //выход из цикла
            }
            idEl++;                         //увеличение id на 1
        }
        if (find)
            accounts.remove(idEl);             //удаление аккаунта если он найден
        writeAccounts(accounts);                //перезапись аккаунтов в файл
    }

    /*
      Метод возвращает список всех аккаунтов
     */
   List<String> getAllAccounts() throws IOException {
       CsvListReader csvListReader = new CsvListReader(new FileReader("accounts.csv"),
               CsvPreference.STANDARD_PREFERENCE);          //открытие файла на чтение
       List<String> accounts=new LinkedList<>();            //инициализация List для записи аккаунтов туда
       List<String> acc;                                    //переменная для чтения строк файла
       while ((acc=csvListReader.read())!=null){            //чтение строк пока строка!=null
           accounts.add(String.valueOf(acc));               //добавление строки в список
       }
        return accounts;                                    //возвращение списка аккаунтов
    }
    /*
      Метод авторизирует пользователя и возвращает Token для доступа методам системы
     */
    String authorize(String userName, String password, Date currentTime) throws AuthException, IOException {
        if (!checkUser(userName,password))                                  //проверка на правильно введеные логин и пароль
            throw new AuthException("Неверно указаны данные для входа!");
        TokenManager tokenManager=TokenManager.getInstance();               //получение класса отвечающего за выдачу токена
        String token = tokenManager.generatedToken(userName,currentTime);   //получение токена
        return token;                                                       //возврат токена
    }
    void writeAccounts(List<String> accounts) throws IOException {
        CsvListWriter listWriter = new CsvListWriter(new FileWriter("accounts.csv"),
                CsvPreference.STANDARD_PREFERENCE);          //открытие файла для записи списка аккаунтов
        for (String el:accounts) {                           //перебор элементов списка
            listWriter.write(el);                            //запист в файл элемента списка
        }
        listWriter.close();                                  //закрытие файла
    }
    //Метод проверяет используется ли уже логин
    boolean findNameDuplicate(String userName) throws IOException {
        List<String> accounts=getAllAccounts();
        for (String el: accounts) {
            if (el.contains(userName)){
                return true;
            }
        }
        return false;
    }
    //метод проверяет существует ли такая вариация логина и пароля
    boolean checkUser(String userName, String password) throws IOException {
        List<String> accounts=getAllAccounts();
        for (String el: accounts) {
            if (el.contains(userName)&&el.contains(password)){
                return true;
            }
        }
        return false;
    }


}