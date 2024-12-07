import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Database database = new Database();
        System.out.println("Hello! Have I met you before? What's your name?");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        userLogin(username);
        System.out.println("Let's get into it!");
        menu(database, username);
        // Could clean this up? Would I include the entire username as part of the userLogin?
    }

    /** Used chatGPT to help properly split the values in the file */
    public static void userLogin(String username) {
        File file = new File("login.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            boolean userExists = false;
            for (String line : Files.readAllLines(Paths.get("login.txt"))) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) { //Used ChatGPT here
                    System.out.println("Welcome back, " + username + "!");
                    System.out.println("It looks you really like " + parts[1] + "s");
                    System.out.println("and your favorite genre is " + parts[2]);
                    if (parts.length > 3){
                        System.out.println("Last time you picked " + parts[3]);
                    }
                    userExists = true;
                    break;
                }
            }
            /** Asks the user for their favorite activity (media type) and genre */
            if (!userExists) {
                System.out.println("Nice to meet you, " + username + "! Let's get some information real quick!");
                Scanner scanner = new Scanner(System.in);
                System.out.print("Do you like Movies or TV shows more? ");
                String activity = scanner.nextLine();
                System.out.print("What is your favorite genre? ");
                String genre = scanner.nextLine();
                Files.write(Paths.get("login.txt"), (username + "," + activity + "," + genre + System.lineSeparator()).getBytes(), java.nio.file.StandardOpenOption.APPEND); //Used ChatGPT here
                System.out.println("Profile created. Welcome, " + username + "!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void menu(Database database, String username) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Please choose an option:");
            System.out.println("1. List all Activities");
            System.out.println("2. Set values in Database");
            System.out.println("3. Get a recommendation");
            System.out.println("4. Add a new Activity");
            System.out.println("5. Delete an Activity");
            System.out.println("6. Exit");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    database.showAllActivities();
                    break;
                case "2":
                    System.out.println("Setting values in Database...");
                    database.editDatabase();
                    break;
                case "3":
                    System.out.println("Let's get a recommendation!");
                    Recommendation recommendation = new Recommendation(); // Is it better to instantiate here?
                    recommendation.recommendation(database, username);
                    break;
                case "4":
                    database.addActivity();
                    break;
                case "5":
                    database.deleteActivity();
                case "6":
                    System.out.println("Exiting");
                    return;
                default:
                    System.out.println("BEEEEEP BOOP! Error.");
            }
        }
    }
}