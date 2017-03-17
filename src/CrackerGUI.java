/** This class provides a GUI interface for a program that 
 *  demonstrates different password cracking algorithms.
 *  
 *  class invariant (for each CrackerGUI object...)
 *  	- a 350 by 450 JFrame is displayed
 *      - in the JFrame is a text field that displays a user-entered password
 */

import java.security.*;
import java.util.Arrays;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.*;
public class CrackerGUI implements ActionListener { 
    // The following variables are part of the GUI
    private JFrame win;
    private Driver theDriver;
    private JButton crackBtn1, crackBtn2, crackBtn3, encryptBtn;
    private JTextField  plaintext, ciphertext;
    private JLabel msg, msg2, finalMsg, timeMsg;
    
    // savedMD stores the encrypted hash of the user's password
    private byte[] savedMD;
    // startTime is the system time (in msec) when the cracking code begins to execute. 
    private long startTime;
            
    /** pre:  d != null
     *        AND  d supports callback methods named 
     *        	   crackFromDictionary, crackTwoWords and bruteForceCrack
     *  post: the newly created JFrame contains both the password text field and a button
     *        AND  clicking the button or typing enter in the text field is an event
     *             (The event is handled by actionPerformed)
     */
    public CrackerGUI(Driver d) {
    	theDriver = d;
        win = new JFrame("Password Cracking Demo");
        win.setBounds(30, 30, 450, 350);
        win.setVisible(true);
        win.setLayout(null);
        win.getContentPane().setBackground(Color.lightGray);
        
        msg = new JLabel("Type your password here");
        msg.setBounds(30, 20, 200, 30);
        win.add(msg, 0);
        plaintext = new JTextField();
        plaintext.setBounds(200, 20, 200, 30);
        win.add(plaintext, 0);
        plaintext.addActionListener(this);
        
        encryptBtn = new JButton("Encrypt Password");
        encryptBtn.setBounds(100, 80, 250, 30);
        encryptBtn.addActionListener(this);
        win.add(encryptBtn);
        
        // The following GUI objects are created, but not yet added.
        msg2 = new JLabel("Encrypted Password (Base-64 format)");
        msg2.setBounds(30, 80, 240, 30);
        ciphertext = new JTextField("");
        ciphertext.setBounds(40, 110, 400, 30);
        crackBtn1 = new JButton("Crack (dictionary word)");
        crackBtn1.setBounds(100, 180, 250, 30);
        crackBtn1.addActionListener(this);
        crackBtn2 = new JButton("Crack (2 consecutive words)");
        crackBtn2.setBounds(100, 215, 250, 30);
        crackBtn2.addActionListener(this);
        crackBtn3 = new JButton("Crack (any lowercase text)");
        crackBtn3.setBounds(100, 250, 250, 30);
        crackBtn3.addActionListener(this);
        finalMsg = new JLabel("");
        finalMsg.setBounds(30, 160, 500, 60);
        finalMsg.setFont(new Font("Helvetica", Font.BOLD, 25));
        timeMsg = new JLabel("");
        timeMsg.setBounds(150, 220, 300, 30);
        timeMsg.setBackground(Color.red);
        win.repaint();
    }

    /** pre:  s != null
     *  post: @return is the SHA-256 encrypted hash of s 
     */
    private byte[] hash(String s) {
        MessageDigest md;
        byte[] buffer;
        byte[] digest = new byte[0];
        try {
            buffer = s.getBytes();  // getBytes translates String to byte[]
            md = MessageDigest.getInstance("SHA-256");
            md.update(buffer);
            digest = md.digest();
        } catch (Exception ex) { 
            System.out.println("MD Error!");
        }
        return digest;
    }
    
    /** post: @return is true exactly when hash(s) is identical to savedMD
     *  note: s==null or s="" will never match 
     */
    public boolean hashMatches(String s) {
    	if (s==null || s.length()==0)
    		return false;
    	else
    		return Arrays.equals(savedMD, hash(s));
    }
    
    /** note: The method below is called upon one of three events:
     *          1) The Encrypt Password button has been clicked.
     *          2) A return key is struck in the password text field (plainttext).
     *          3) One of the three crack buttons has been clicked.
     *  post: Either Event 1 or Event 2 has occurred implies
     *          savedMD == the SHA-256 encrypted hash of the user's password
     *           AND startTime == the current clock time (in msec.) 
     *           AND the encrypted password hash is displayed in a separate text field
     *           AND the three crack buttons are displayed
     *           AND the Encrypt Password button has been removed
     *        Event 3 has occurred implies
     *          if the password can be cracked, then the password and time to crack (in seconds) are displayed
     *           AND If  the password is not cracked, then a message to that effect is displayed
     *           AND the Encrypt Password button is displayed
     *           AND the three crack  buttons have been removed
     *  note: the correctness of this method relies upon the three crack methods that are callbacks to Driver
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==encryptBtn || e.getSource()==plaintext) {
            savedMD = hash(plaintext.getText());
            String str = bytesToBase64( savedMD );
            if (str.length()!=0)  {
                win.add(msg2);
                ciphertext.setText(str);
                win.add(ciphertext);
                win.add(crackBtn1);
                win.add(crackBtn2);
                win.add(crackBtn3);
                win.remove(finalMsg);
                win.remove(timeMsg);
                win.remove(encryptBtn);
            }
        } else if (e.getSource()==crackBtn1 || e.getSource()==crackBtn2 || e.getSource()==crackBtn3) {
            startTime = System.currentTimeMillis();
            if (e.getSource() == crackBtn1)
                theDriver.crackFromDictionary();
            else if (e.getSource() == crackBtn2)
                theDriver.crackTwoWords();
            else          
                theDriver.bruteForceCrack();
            win.remove(crackBtn1);
            win.remove(crackBtn2);
            win.remove(crackBtn3);
            win.remove(msg2);
            win.remove(ciphertext);
            win.add(encryptBtn);
        }
        win.repaint();
    }
        
    /** pre: s denotes the cracked password in text form
     *       s == null means the algorithm was unable to crack
     *       startTome == the clock time (in msec) when the cracking operation began
     *  post: The GUI displays a message indicating the cracked word and time (in sec.) to crack.
     */
    public void reportResults(String s) {
        long endTime = System.currentTimeMillis();
        if (s != null) {
            finalMsg.setText("Cracked Password: " + s);
            finalMsg.setForeground(Color.red);
        } else {
            finalMsg.setText("Unable to crack");
            finalMsg.setForeground(new Color(100, 255, 100));
        }
        timeMsg.setText("Elapsed Time: " + (endTime-startTime)/1000. + " sec.");
        win.add(timeMsg);
        win.add(finalMsg);
        win.repaint();
    }

    /** pre: ba != null
     *  post: result is a printable (Base-64) version
     *        of the content of ba that may have otherwise
     *        contained many non-printable characters.
     */
    private static String bytesToBase64(byte[] ba) {
        return String.valueOf(Base64Coder.encode(ba));
    }    
}
