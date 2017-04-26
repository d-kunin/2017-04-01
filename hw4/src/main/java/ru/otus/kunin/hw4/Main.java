package ru.otus.kunin.hw4;

public class Main {
/*
    ДЗ4
    Написать приложение, следит за сборками мусора и пишет в
    лог количество сборок каждого типа (young, old) и время которое ушло на сборки в минуту.
    Добиться OutOfMemory в этом приложении через медленное
    подтекание по памяти (например добавлять желементы в LIst и удалять только половину)
    Настроить приложение (можно добавлять Thread.sleep()) так чтобы
    оно падало с OOM примерно через 5 минут после начала работы.
    Собрать статистику (количество сборок, время на сборрки) по разным типам GC.
 */
    public static void main(String[] args) throws Exception {
        final GCBenchmark gcBenchmark = new GCBenchmark();
        gcBenchmark.start();
    }

}
