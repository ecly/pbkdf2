import java.util.HashMap;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Scanner;

//a shitty and probably incorrect program to try out pbkdf2 encryption
//mostly using java's own libraries for the purposes
public class PBKDF2{

    private HashMap<String, String> salts;       //map username to salt
    private HashMap<String, String> passwords;   //map username to password
    private final SecureRandom rnd;
    private static int totalHashes;

    //instance of pbkdf2 repeating hashes 10000 times
    public PBKDF2(){
        salts = new HashMap<>();
        passwords = new HashMap<>();
        rnd = new SecureRandom();
        totalHashes = 10000;
    }

    //public method for user creation
    public void createUser(String userName, String password){
        byte[] salt = new byte[32];
        rnd.nextBytes(salt);
        String saltString = new String(salt);
        salts.put(userName, saltString);
        passwords.put(userName, encrypt(password, saltString));
        System.out.println("User \"" + userName +"\" succesfully created");
    }

    //internal encryption method
    private String encrypt(String password, String salt){
        String sha = password + salt;
        for(int i = 0; i < totalHashes; i++) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.reset();
                digest.update(sha.getBytes("UTF-8"));
                sha = new String(digest.digest());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return sha;
    }

    //public login method
    public void login(String userName, String password){
        if(salts.containsKey(userName)) {
            String salt = salts.get(userName);
            String encryptedPassword = encrypt(password, salt);
            if (passwords.get(userName).equals(encryptedPassword))
                System.out.println("Login successful for user: " + userName);
            else
                System.out.println("Wrong password for user: " + userName);
        } else {
            System.out.println("User: \"" + userName + "\" does not exist");
        }
    }

    //Test method for passWords;
    public String passwordForUser(String userName){
        return passwords.get(userName);
    }

    public static void main(String[] args){
        //Testing
        PBKDF2 crypto = new PBKDF2();
        crypto.createUser("Lethly", "password123");
        crypto.login("Lethly", "password123");

        boolean isFinished = false;
        Scanner scanner = new Scanner(System.in);

        //a shitty login/create user loop
        while(!isFinished){
            System.out.println("Create user? (yes/no)");
            boolean creatingUser = scanner.next().equalsIgnoreCase("yes");
            if (!creatingUser)
                System.out.println("Login:");

            System.out.println("Username:");
            String userName = scanner.next();

            System.out.println("Password:");
            String password = scanner.next();

            if(!creatingUser)
                crypto.login(userName, password);
            else {
                crypto.createUser(userName, password);
            }

            System.out.println("Exit? (yes/no)");
            if (scanner.next().equalsIgnoreCase("yes"))
                isFinished = true;
        }
    }
}