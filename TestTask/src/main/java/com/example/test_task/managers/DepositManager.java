package com.example.test_task.managers;

import com.example.test_task.exception.AuthException;
import com.example.test_task.exception.DepositException;
import com.example.test_task.models.Client;
import com.example.test_task.models.Deposit;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DepositManager {
    public DepositManager()  {
    }

    private static CellProcessor[] getProcessors() {
        return new CellProcessor[]{
                new UniqueHashCode(), // для идентификатора (id)
                new NotNull(), // idClient не должно быть null
                new NotNull(), // ammount не должно быть null
                new NotNull(), // percent не должно быть null
                new NotNull(), // pretermPercent не должно быть null
                new NotNull(), // termDays не должно быть null
                new NotNull(), // startDate не должно быть null
                new NotNull() // withPercentCapitalization не должно быть null
        };
    }
    Deposit addDeposit(Client client, double ammount, double percent,
                       double pretermPercent, int termDays,
                       Date startDate, boolean withPercentCapitalization,String token) throws IOException, DepositException, AuthException {
        if (!TokenManager.getInstance().checkToken(token))
            throw new AuthException("Не обладаете правом доступа!");

        Deposit deposit = new Deposit(0,client.getId(),ammount,percent,pretermPercent,
                    termDays,startDate,withPercentCapitalization);  //инициализация вклада

        ICsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter("deposits.csv", true),
                CsvPreference.STANDARD_PREFERENCE);                 //открытие файла на запись
        String[] header = new String[]{"id","idClient","ammount","percent",
                "pretermPercent","termDays","startDate","withPercentCapitalization"};       //объявление заголовка
        if (!new File("deposits.csv").exists())
            beanWriter.writeHeader(header);
        beanWriter.write(deposit,header,getProcessors());       //запись вклада согласно заголовкам
        beanWriter.close();                                     //закрытие файла
        return deposit;
    }
//    /*
//      Метод возвращает список вкладов клиента
//    */
    List<Deposit> getClientDeposits(Client client, String token) throws IOException, AuthException {
        if (!TokenManager.getInstance().checkToken(token))
            throw new AuthException("Не обладаете правом доступа!");
        List<Deposit> clientDeposits=new ArrayList<>();           //список отвечающий за вклады клиента
        for (Deposit el:getAllDeposits()) {                       //перебор всех вкладов которые были возвращены
            if (el.getIdClient()==client.getId())                 //проверка на совпадение id клиента на совпадение id указанном во вкладе
                clientDeposits.add(el);                           //добавление вклада в список
        }
        return clientDeposits;                                    //возврат списка
    }
//    /*
//      Метод возвращает список всех вкладов принятых банком
//     */
    List<Deposit> getAllDeposits() throws IOException {
        List<Deposit> deposits = new ArrayList<>();             //список хранящий вклады
        ICsvBeanReader csvBeanReader = new CsvBeanReader(
                new FileReader("deposits.csv"), CsvPreference.STANDARD_PREFERENCE);
        CellProcessor[] procs = getProcessors();                //открытие файла на чтение
        final String[] header = csvBeanReader.getHeader(true);      //объявление заголовков файла
        Deposit deposit;                                                //переменная в которую считывается вклад из файла
        while ((deposit = csvBeanReader.read(Deposit.class, header, procs)) != null) {  //чтение файла построчно пока не считается пустая строка
            deposits.add(deposit);                              //добавление влкада в список
        }
        csvBeanReader.close();                                  //закрытие файла
        return deposits;                                        //возврат файла
    }
//    /*
//      Метод возвращает текущий доход по вкладу
//     */
    double getEarnings(Deposit deposit, Date currentDate, String token) throws AuthException, IOException {
        if (!TokenManager.getInstance().checkToken(token))
            throw new AuthException("Не обладаете правом доступа!");
        return Math.round(deposit.getAmmount()*(deposit.getPercent()/12/(365/12)/100)*(Math.floor((currentDate.getTime()-deposit.getStartDate().getTime())/86400000))*100)/100D;
    }
//    /*
//      Метод удаляет запись о вкладе и возвращает сумму к выплате в кассе. Если вклад закрывается досрочно, то сумма к выплате рассчитывается исходя из процента при досрочном изъятии.
//     */
    double removeDeposit(Deposit deposit, Date closeDate, String token) throws Exception {
        if (!TokenManager.getInstance().checkToken(token))
            throw new AuthException("Не обладаете правом доступа!");
        if((closeDate.getTime()-deposit.getStartDate().getTime())/86400000<30)
            throw new DepositException("Минимальный срок необходимый для изъятия 30 дней!");

        double toPayoff=0;
        int days= (int) Math.floor((closeDate.getTime()-deposit.getStartDate().getTime())/86400000);
        toPayoff=days< deposit.getTermDays()?
                //если досрочное то передаем в функцию сумму вклада процент досрочного изъятия, количество дней прошедших с момента октрытия и процентную капитализацию
                getDepositAmount(deposit.getAmmount(),deposit.getPretermPercent(),days,deposit.isWithPercentCapitalization())
                //передаем в функцию сумму вклада процент досрочного изъятия, количество дней прошедших с момента октрытия и процентную капитализацию
                :getDepositAmount(deposit.getAmmount(),deposit.getPercent(),deposit.getTermDays(),deposit.isWithPercentCapitalization());
        List<Deposit> allDeposits = getAllDeposits();
        allDeposits.remove(deposit);
        writeDeposits(allDeposits);
        return Math.round(toPayoff*100)/100D;
    }
    private double getDepositAmount(double ammount, double percent,int days,boolean withPercentCapitalization){
        //float persentPerDay=1/12/(365/12)/100;                   // формула высчитывает процент начисляемый за 1 день
        int countMonth = days/30;               //высчитываем количество месяцев на который был положен вклад, за расчет взялся 1 месяц = 30дней
        int remainingDays=days-countMonth*30;   //количество оставшихся дней
        if(withPercentCapitalization){
            for (int i = 0; i < countMonth; i++) {
                //увеличиваем сумму вклада согласно ежемесячным выплатам(для процентов при досрочном изъятии)
                ammount+=ammount*(percent/12/100);
            }
            //находим начисленные проценты для оставшихся дней и добавляем к прошлым
            ammount+=ammount*(percent/12/(365/12)/100)*remainingDays;
        }else {
            //без процентной капитализации
            //кладем новое значение суммы вклады по формуле: сумма вклада+проценты начисляемые ежемесячно(сумма начисленная за все месяцы)+проценты начисленные для оставшихся дней
            ammount+=ammount*(percent/12/100*countMonth)+ammount*(percent/12/(365/12)/100)*remainingDays;
        }
        return ammount;
    }
    void writeDeposits(List<Deposit> deposits) throws IOException {
        ICsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter("deposits.csv"),
                CsvPreference.STANDARD_PREFERENCE);             //открытие файла на запись
        String[] header = new String[]{"id","idClient","ammount","percent",
                "pretermPercent","termDays","startDate","withPercentCapitalization"};   //описание заголовка для файла
        beanWriter.writeHeader(header);                                 //запись заголовка в файл
        for (Deposit el:deposits) {                                 //перебор элементов в списке
            beanWriter.write(el,header,getProcessors());            //запись вклада в файл согласно заголовку и значениям
        }
        beanWriter.close();                                         //закрытие файла
    }
}