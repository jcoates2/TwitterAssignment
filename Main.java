package TwitterAssignment;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
/**
 * @author Evan Shipman
 * Date: (4/3/16) Added pList and usernameList for LogUserIn, added methods to update the user and message files,
 * some misc. formatting stuff also put some Main functionality and a method for searching messages.
 * @author William Scheid
 * Date: (4/1/16) Added the updated input method and calls from the main method, with updated parameter read-ins.
 * Date: (4/6/16) Solved issue, learned to pull LOL
 * Date: (4/6/16) Grace is trying to test her GitHub syncing lots of stuff.
 * BLAHHHH!!!!
 * Evan Shipman:
 * Date: (4/8/16) Changed addMessage and addUser to updateMessageFile and updateUserFile. A new message or user is
 *      added outside of those methods that way we also have a way to remove messages and users without unnecessary
 *      coding. Removed or renamed main methods from other classes so they can still be called. Cleaned up the
 *      checkLoginSuccess method and added a getUser method in the LogUserIn class to help with deleting accounts.
 *      Added a second constructor in User to accept a date as a long, and also changed the data type to a long
 *      because it's much easier to convert from a long rather than back to a long. The User.getRegisterDate() now
 *      returns a long so it must be converted with the formatter if it will be displayed to the user. Added the
 *      case 4 in main (Delete user account)
 *      ** If a user is placed in the UsersFile twice by mistake, deleting the account only removes one of them **
 *      ** Sometimes I've noticed the Scanner doesn't work right after lots of inputs                           **
 *      UPDATE: I think I finished the Scanner errors by changing them to nextLine()
 */
public class Main {

    //    private static ArrayList<String> pList = new ArrayList<String>();
//    private static ArrayList<String> usernameList = new ArrayList<String>();
    protected static ArrayList<User> userList;
    protected static ArrayList<Message> messageList;
    public static SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");     //added to
    public static SimpleDateFormat sdfMessages = new SimpleDateFormat("MM/dd/yyy hh:mm a");
    public static User currentUser;
    //added username and passwd to be data memebers.
    protected static String username = "", passwd = "";

    public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
        //GUI graphical = new GUI();
        //graphical.start();
        userList = new ArrayList<User>();
        messageList = new ArrayList<Message>();
        userList = readUserInput("UsersFile.txt");
        messageList = readMessageInput("MessageFile.txt");
        Scanner in = new Scanner(System.in);

        boolean done = LogIn(in);
        if(done)
            WhileLoggedIn(in);
        else
            System.out.println("User is not logged in.");

    }

    //this method logs the user in
    public static boolean LogIn(Scanner in){
        boolean success = false;
        int attempts = 0;
        System.out.println("Please enter log-in information (leave blank to quit)");
        while (!success && attempts <= 2)
        {
            System.out.print("Username:");
            username = in.nextLine();
            System.out.print("Password:");
            passwd = (in.nextLine());

            if (username.equals("") || passwd.equals(""))
                success = false;
//            LogUserIn lui = new LogUserIn(username, passwd);
            success = LogUserIn.checkLoginSuccess(userList, username, passwd);
            currentUser = LogUserIn.getUser(userList, username, passwd);
            attempts++;
            if(!success && attempts > 2){
                System.out.println("Error: Incorrect username or password.");
                return false;
            }
        }
        return success;
    }

    //this method will run until the user is loged out.
    public static void WhileLoggedIn(Scanner in) throws IOException{
        boolean done = false, success = false;
        Scanner command = new Scanner(System.in);
        while(!success){
            System.out.print("What would you like to do?\n"
                    + "1.) Post Messages\n"
                    + "2.) View Messages\n"
                    + "3.) Search Messages\n"
                    + "4.) Delete account\n"
                    + "else, logout/quit\n"
                    + "command:");
            switch (Integer.parseInt(command.nextLine())) {
                case 1:
                    //adds messages
                    LogUserIn.case1AddMessage(messageList, username);
                    break;                                                                                                        //better
                case 2:
                    //view Message
                    LogUserIn.case2ShowMessages(messageList);
                    break;
                case 3:     //can be optimized later to search by relevance
                    System.out.println("Enter search terms separated by spaces:");
                    String[] terms = in.nextLine().split(" ");
                    for (Message m : messageList)
                        if (hasTerms(m, terms))
                            System.out.println(m.getUser() + "  on " + sdfMessages.format(new Date(m.getDate())) + "\n" + m.getMessage() + "\n");
                    break;
                case 4:
                    System.out.println("Are you sure you want to delete your account? (Yes/No): ");
                    if (in.nextLine().equalsIgnoreCase("Yes") && !currentUser.equals(null))
                    {
                        userList.remove(currentUser);
                        updateUserFile();
                    }
                    System.exit(0);     //until we add a log out function
                    break;
                default:
                    success = true;
                    break;
            }
        }
    }

    public static ArrayList readMessageInput(String inputName) throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
        ArrayList<Message> mList = new ArrayList<Message>();
        File inFile2 = new File(inputName);
        Scanner inTxt2 = new Scanner(inFile2);
        String user;
        int messageID;
        String message;
        long date;
        boolean privacy;
        while (inTxt2.hasNext()) {
            user = inTxt2.nextLine();
            messageID = Integer.parseInt(inTxt2.nextLine());
            message = inTxt2.nextLine();
            date = Long.parseLong(inTxt2.nextLine());
            privacy = Boolean.parseBoolean(inTxt2.nextLine());
            Message m = new Message(user, messageID, message, date, privacy);
            mList.add(m);
        }
        return mList;
    }

    public static ArrayList readUserInput(String inputName) throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
        ArrayList<User> uList = new ArrayList<User>();
        File inFile = new File(inputName);
        Scanner inTxt = new Scanner(inFile);
        String username;
        String password;
        String email;
        long dateMade;
        String description;
        int followersCount;
        int followingCount;
        String followers;
        String following;
        while (inTxt.hasNext()) {
            username = inTxt.nextLine();
            password = inTxt.nextLine();
            email = inTxt.nextLine();
            dateMade = Long.parseLong(inTxt.nextLine());
            description = inTxt.nextLine();
            followersCount = Integer.parseInt(inTxt.nextLine());
            followingCount = Integer.parseInt(inTxt.nextLine());
            followers = inTxt.nextLine();
            following = inTxt.nextLine();
            User u = new User(username, password, email, dateMade, description, followersCount, followingCount, followers, following);
            uList.add(u);
//            pList.add((password));
//            usernameList.add(username);
        }
        return uList;
    }

    public static void updateUserFile() throws IOException
    {
        FileWriter fw = new FileWriter(new File("UsersFile.txt"));
        for (User user : userList)
        {
            String followers = "", following = "";
            fw.write(user.getUsername() + "\n");
            fw.write(user.getPassword() + "\n");
            fw.write(user.getEmail() + "\n");
            fw.write(user.getRegisterDate() + "\n");
            fw.write(user.description + "\n");
            fw.write(user.getFollowers() + "\n");
            fw.write(user.getFollowing() + "\n");
            for (int i = 0; i < user.followers.length; i++)
            {
                followers += user.followers[i];
                if (i != user.followers.length - 1)
                    followers += ";";
            }
            for (int i = 0; i < user.followings.length; i++)
            {
                following += user.followings[i];
                if (i != user.followings.length - 1)
                    following += ";";
            }
            fw.write(followers + "\n");
            fw.write(following + "\n");
        }
        fw.close();
    }

    public static boolean hasTerms(Message msg, String[] terms)
    {
        for (int i = 0; i < terms.length; i++)
            if (msg.getMessage().contains(terms[i]))
                return true;
        return false;
    }
}


