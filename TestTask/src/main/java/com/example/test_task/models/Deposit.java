package com.example.test_task.models;

import com.example.test_task.exception.DepositException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Deposit {
    private long id;
    private long idClient;
    private double ammount;
    private double percent;
    private double pretermPercent;
    private int termDays;
    private Date startDate;
    private boolean withPercentCapitalization;

    public Deposit() {
    }

    public Deposit(long id,long idClient, double ammount, double percent,
                   double pretermPercent, int termDays,
                   Date startDate, boolean withPercentCapitalization) throws DepositException {
        if (ammount<0)
            throw new DepositException("Сумма вклада не может быть меньше 0!");
        if (percent<=0)
            throw new DepositException("Заданный процент не может быть отрицательным!");
        if (termDays<30)
            throw new DepositException("Срок размещения вклада не может быть меньше 30 дней!");
        this.id=id;
        this.idClient = idClient;
        this.ammount = ammount;
        this.percent = percent;
        this.pretermPercent = pretermPercent;
        this.termDays = termDays;
        this.startDate = startDate;
        this.withPercentCapitalization = withPercentCapitalization;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setId(String id){
        this.id = Long.parseLong(id);
    }

    public long getIdClient() {
        return idClient;
    }

    public void setIdClient(long idClient) {
        this.idClient = idClient;
    }
    public void setIdClient(String idClient) {
        this.idClient = Long.valueOf(idClient);
    }

    public double getAmmount() {
        return ammount;
    }

    public void setAmmount(double ammount) {
        this.ammount = ammount;
    }
    public void setAmmount(String ammount) {
        this.ammount = Double.valueOf(ammount);
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
    public void setPercent(String percent) {
        this.percent = Double.valueOf(percent);
    }

    public double getPretermPercent() {
        return pretermPercent;
    }

    public void setPretermPercent(double pretermPercent) {
        this.pretermPercent = pretermPercent;
    }
    public void setPretermPercent(String pretermPercent) {
        this.pretermPercent = Double.valueOf(pretermPercent);
    }

    public int getTermDays() {
        return termDays;
    }

    public void setTermDays(int termDays) {
        this.termDays = termDays;
    }
    public void setTermDays(String termDays) {
        this.termDays = Integer.valueOf(termDays);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public void setStartDate(String startDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        try {
            this.startDate = formatter.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isWithPercentCapitalization() {
        return withPercentCapitalization;
    }

    public void setWithPercentCapitalization(boolean withPercentCapitalization) {
        this.withPercentCapitalization = withPercentCapitalization;
    }
    public void setWithPercentCapitalization(String withPercentCapitalization) {
        this.withPercentCapitalization = Boolean.valueOf(withPercentCapitalization);
    }

    @Override
    public String toString() {
        return "Deposit{" +
                "id=" + id +
                ", idClient=" + idClient +
                ", ammount=" + ammount +
                ", percent=" + percent +
                ", pretermPercent=" + pretermPercent +
                ", termDays=" + termDays +
                ", startDate=" + startDate +
                ", withPercentCapitalization=" + withPercentCapitalization +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deposit deposit = (Deposit) o;
        return id == deposit.id &&
                idClient == deposit.idClient &&
                Double.compare(deposit.ammount, ammount) == 0 &&
                Double.compare(deposit.percent, percent) == 0 &&
                Double.compare(deposit.pretermPercent, pretermPercent) == 0 &&
                termDays == deposit.termDays &&
                withPercentCapitalization == deposit.withPercentCapitalization &&
                startDate.equals(deposit.startDate);
    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(id, idClient, ammount, percent, pretermPercent, termDays, startDate, withPercentCapitalization);
//    }
}