package ru.otus.kunin.hw4;

public class Main {
/*
    ДЗ 04: Написать приложение которое "подтекает" по памяти.
    Логгировать активность GC (количетство сборок в минуту,
    время паузы в минуту). Написать скрипт, который проведет
    измерение активности GC для разных параметров GC.
 */
    public static void main(String[] args) throws Exception {
        MemoryLeak.leak();
    }

}
