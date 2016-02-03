package br.org.isvi.abraao.tweetstorm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Process the file passed to the application and update the twitter status configured in the tweetstorm.cfg.
 * 
 * @author Abraao
 * @since 2016
 */
public class App {

	private static final int _MAX_CHARS = 140;
	
	private Properties configuration = new Properties();
	
	/**
	 * Update twitter status with the passed file.
	 * 
	 * @param file with the content to update.
	 */
	public void stormIt(File file) {
		
		try {
			configuration.load(new FileReader(new File("tweetstorm.cfg")));
			
			processFile(file);
			
		} catch (FileNotFoundException e) {
			System.out.println("The configuration file: tweetstorm.cfg could not be found, please provide one.");
		} catch (IOException e) {
			System.out.println("Oops! something wrong happened when trying to read tweetstorm.cfg.");
		}
    		
	}

	/**
	 * Process file updating the twitter status with its content.
	 * 
	 * @param file with the content.
	 */
	private void processFile(File file) {
		
		FileReader reader = null;
		
		try {
			reader = new FileReader(file);
			char buff[] = new char[140];
			int chars = countChars(file);
			int tweets = countTweets(chars)+1;
			
			Twitter twitter = authenticate();
			
			if(twitter == null) {
				return;
			}
			
			System.out.println("OK. Twitting...");
			
			int index = 1;
			String indexString = index + "/" + tweets + " ";
			while(index <= tweets) {
				buff = new char[_MAX_CHARS - indexString.length()];
				reader.read(buff, 0, _MAX_CHARS - indexString.length());
				
				StringBuilder build = new StringBuilder();
				build.append(indexString).append(buff);
				
				//tweeting
				twitter.updateStatus(build.toString());
				
				index++;
				indexString = index + "/" + tweets + " ";
						
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("The file " + file.getName() + " does not exist.");
		} catch (IOException e) {
			System.out.println("Oops! something wrong happened when trying to read " + file.getName());
		} catch (TwitterException e) {
			System.out.println("Oops! something wrong happened when trying to send a tweet: " + e.getMessage());
		}
		
	}

	private Twitter authenticate() {
		Twitter twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer(configuration.getProperty("oauth.consumerkey"), 
				configuration.getProperty("oauth.consumerSecret"));
		AccessToken accessToken = new AccessToken(configuration.getProperty("accessToken.key"),configuration.getProperty("accessToken.secret"));
		twitter.setOAuthAccessToken(accessToken);
		
		try {
			System.out.println("Trying to authenticate.");
			twitter.verifyCredentials();
		} catch(TwitterException ex) {
			if(ex.getStatusCode() == 401) {
				System.out.println("Failed to authenticate, please grant access for the application in your twitter account creating an Access Token and putting it int the tweetstorm.cfg.");
			}
			
			return null;
		}
		
		
		return twitter;
	}

	/**
	 * Calculate the max amount of twitts
	 * @param chars total of chars int the file.
	 * 
	 * @return max amount of twitts.
	 */
	private int countTweets(int chars) {
		int tws = chars/140;
		return tws;
	}
	
	/**
	 * Count total of chars in the file.
	 * 
	 * @param file to count chars.
	 * @return total of chars in the file.
	 * 
	 * @throws IOException
	 */
	private int countChars(File file) throws IOException {
		
		FileReader in = new FileReader(file);
		char buff[] = new char[140];
	    int ret = 0;
	    
		while(in.read(buff) > 0)  {
	        ret += new String(buff).length();
	        buff = new char[140];
	    }
	    
		return ret;
	}
	
}
