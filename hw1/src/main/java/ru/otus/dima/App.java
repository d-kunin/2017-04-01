package ru.otus.dima;

import au.com.bytecode.opencsv.CSVParser;
import ru.otus.dima.obfuscate.me.LotteryMachine;
import ru.otus.dima.obfuscate.me.LotteryMachineOneMoreTime;

import java.util.Collections;

/**
 * Hello world!
 *
 */
public class App 
{
    /*
    run with: java -cp target/first-1.0-SNAPSHOT.jar ru.otus.dima.App
     */
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        System.out.println("Referencing classes:\n" +
                CSVParser.class + "\n" +
                LotteryMachine.class + "\n" +
                LotteryMachineOneMoreTime.class + "\n");
    }
}
