package ru.otus.dima;

import ru.otus.dima.obfuscate.me.LotteryMachine;
import ru.otus.dima.obfuscate.me.LotteryMachineOneMoreTime;

import java.util.Collections;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        System.out.println("Referencing classes" +
                LotteryMachine.class + "\n" +
                LotteryMachineOneMoreTime.class + "\n");
    }
}
