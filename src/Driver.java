/** Programmer:
 *  Date: September, 2015
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

public class Driver {
	private CrackerGUI gui;
    private String fileName = "OWL.txt";
    private char[] alph;

    
	/* The method below is complete. */
	public Driver() {
		gui = new CrackerGUI(this);
	}

    /** pre:  gui already contains a hashed password (call it savedMD)
     *  post: (savedMD matches a word in the OWL.txt file)
     *            implies the cracked word is displayed along with time to crack
     *        and (savedMD doesn't match) implies a no crack message is displayed
     *  note: This crack consists of comparing against the OWL.txt dictionary using lowercase only.
     */
    public void crackFromDictionary() {
    	String currLine, currWord;
    	boolean matchFound = false;
    	
    	try{
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			currLine = reader.readLine();
			while (currLine != null){
				currWord = currLine.split("\\ ")[0].toLowerCase();
				System.out.println(currWord);
				matchFound = gui.hashMatches(currWord);
				if (matchFound == true){
					gui.reportResults(currWord);
					return;
				}
				currLine = reader.readLine();
			}
			reader.close();
			gui.reportResults(null);
		}
		catch(Exception e){
			System.out.println("Error: Could not crack from dictionary " + fileName);
			e.printStackTrace();
		}
    }

    
    /** pre:  gui already contains a hashed password (call it savedMD)
     *  post: (savedMD matches two consecutive words from the OWL.txt file)
     *            implies the cracked word is displayed along with time to crack
     *        and (savedMD doesn't match) implies a no crack message is displayed
     *  note: This crack consists of comparing against the OWL.txt dictionary using lowercase only.
     */
    public void crackTwoWords(){
    	String currLine, currWord, nestLine, nestWord;
    	boolean matchFound = false;
    	
    	try{
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			currLine = reader.readLine();
			//Outer Loop Begins
			while (currLine != null){
				//Read word 
				currWord = currLine.split("\\ ")[0].toLowerCase();
				BufferedReader nestedReader = new BufferedReader(new FileReader(fileName));
				nestLine = nestedReader.readLine();
				System.out.println(currWord);
				//Inner Loop Begins
				while(nestLine != null){
					nestWord = currWord + nestLine.split("\\ ")[0].toLowerCase();
					System.out.println("-" + nestWord);
					matchFound = gui.hashMatches(nestWord);
					if (matchFound == true){
						gui.reportResults(nestWord);
						return;
					}
					nestLine = nestedReader.readLine();
				}
				nestedReader.close();
				currLine = reader.readLine();
			}
			reader.close();
			gui.reportResults(null);
		}
		catch(Exception e){
			System.out.println("Error: Could not crack from dictionary " + fileName);
			e.printStackTrace();
		}
    }

    /** pre:  gui already contains a hashed password (call it savedMD)
     *  post: (savedMD matches some string of lowercase letters (up to 8 characters long)
     *            implies the cracked word is displayed along with time to crack
     *        and (savedMD doesn't match) implies a no crack message is displayed
     *  note: This crack attempts a brute force crack, assuming only lowercase letters
     *        and starting with the shortest possible passwords.
     */
    public void bruteForceCrack() {
    	alph = new char[26];
    	char password[] = new char[8];
    	
    	//initialize alphabet array.
    	char aLetter = 'a';
    	for( int i = 0; i < 26; i++){
    		alph[i] = aLetter;
    		aLetter++;
    		//System.out.println(alph[i]);
    	}
    	
    	for(int i = 1; i <= 8; i++){
    		if(bruteForce(0, i, password) == true){
    			return;
    		}
    		System.out.println("test: " + i);
    	}
    	gui.reportResults(null);
    }
    
    private boolean bruteForce(int position, int size, char[] password){
    	for(int i = 0; i < 26; i++){
    		//System.out.println(new String(password) + ":" + size + ":" + position);
    		password[position] = alph[i];
    		if(position < size - 1){
    			if(bruteForce(++position, size, password) == true){
    				return true;
    			}
    			position--;
    		}
    		else{
    			String word = new String(password);
    			System.out.println("-" + word.trim());
    			boolean matchFound = gui.hashMatches(word.trim());
				if (matchFound == true){
					gui.reportResults(word.trim());
					return true;
				}
    		}
    	}
    	return false;
    }
    
	/**
	 * This method below is complete.
	 */
	public static void main(String[] args) {
		new Driver();
	}

}
