package br.org.isvi.abraao.tweetstorm;

import java.io.File;

/**
 *	Verify if the application was called correctly and calls the App.stormIt;
 *
 *@author Abraao
 *@since 2016
 */
public class Main 
{
    public static void main( String[] args )
    {
    	
    	if(args.length > 0 && args[0] != null) {
    		
    		System.out.println("Processing.");
    		
    		new App().stormIt(new File(args[0]));
    		
    		System.out.println("Finished!");
    		
    	} else {
    		System.out.println("What file do you want to process? Please feed me one.");
    	}
        
    }
}
