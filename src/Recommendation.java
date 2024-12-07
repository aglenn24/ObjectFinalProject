import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Recommendation {

    private final List<String[]> recommendationTable = new ArrayList<>();

    /**
     * Generates a recommendation table. Asks user for tags, searches database for matching tags,
     * then searches the database for any matching item with the tag. Ranks the returned list
     * based on how many tags matched.
     * Previously returned String[][]. Didn't end up using. Should I go back?
     */
    public void recommendation(Database database, String username) {
        System.out.println("Let's start by getting some filter tags (hit enter to skip).\n" +
                "  Enter in as many tags as you'd like, separating by comma (ex: action,animated,old)");
        Scanner scanner = new Scanner(System.in);
        String userTagsTemp = scanner.nextLine();
        String[] userTags = userTagsTemp.split(",");
        List<String[]> filteredTags;

        if(userTagsTemp.isEmpty()) {
            filteredTags = database.noTagFilter(username);
        }
        else {
            filteredTags = database.tagFilter(userTags);
        }

        for (String[] tag : filteredTags) {
            double score = applyScores(tag);
            String[] updatedTag = Arrays.copyOf(tag, tag.length + 1);
            updatedTag[tag.length] = Double.toString(score);
            recommendationTable.add(updatedTag);
        }

        //used ChatGPT here to sort the table according to score value
        recommendationTable.sort((a, b) -> Double.compare(Double.parseDouble(b[b.length - 1]), Double.parseDouble(a[a.length - 1])));
        
        String userChoice = userChoice(recommendationTable.toArray(new String[0][0]));
        if (userChoice != null) {
            System.out.println("You chose: " + userChoice + ". I hope you enjoy it!");
            try {
                List<String> lines = Files.readAllLines(Paths.get("login.txt"));
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).startsWith(username + ":")) {
                        lines.set(i, lines.get(i) + "," + userChoice);
                    }
                }
                Files.write(Paths.get("login.txt"), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                System.out.println("Error writing to the file: " + e.getMessage());
            }
        }
        else { System.out.println("Whoops! Something went wrong."); }
        recommendationTable.toArray(new String[0][0]);
    }

    public double applyScores(String[] item) {
        double score = 0;
        int tagHits = Integer.parseInt(item[item.length - 1]);
        score = tagHits * 1.1;
        return score;
    }

    /** Used ChatGPT to help with the HashMap and how to format the parsing and storing of the recommendation
     * This function doesn't use tags in the search. May not use?*/
    public List<String[]> getRecommendations(List<String[]> items) {
        List<String[]> topRecommendations = new ArrayList<>();
        Map<String[], Double> scoresMap = new HashMap<>();

        for (String[] item : items) {
            double score = applyScores(item);
            scoresMap.put(item, score);
        }

        scoresMap.entrySet().stream() // Used ChatGPT here
                .sorted(Map.Entry.<String[], Double>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> topRecommendations.add(entry.getKey()));

        recommendationTable.addAll(topRecommendations);
        return recommendationTable;
    }

    /** returns 0 or 1 */
    public int coinFlip() {
        return new Random().nextInt(2);
    }
    
    public String userChoice(String[][] recommendations) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Here's what I'd recommend: " + java.util.Arrays.deepToString(recommendations));
        System.out.println("Choose one of the options below.\n1. I know which one I want!\n2. I'm stuck between two choices...\n3. I still don't know! You pick.");
        int choice = scanner.nextInt();
        scanner.nextLine();

        //Used ChatGPT to correct issues with the case switching and catching errors
        switch (choice) {
            case 1:
                System.out.println("Which one would you like to select? (numbers only) ");
                int index = scanner.nextInt();
                if (index >= 0 && index < recommendations.length) {
                    return recommendations[index][0];
                } else {
                    System.out.println("I'm sorry, I don't understand.");
                    return null;
                }
            case 2:
                System.out.println("What are the numbers of the two choices you'd like to choose between? (numbers only, separated by either a space or comma)");
                String userInput = scanner.nextLine();
                String[] userInputs = userInput.split("[,\\s]+"); //Used ChatGPT to split properly

                if (userInputs.length == 2) {
                    int index1 = Integer.parseInt(userInputs[0].trim());
                    int index2 = Integer.parseInt(userInputs[1].trim());

                    if ((index1 >= 0 && index1 < recommendations.length)
                            && (index2 >= 0 && index2 < recommendations.length)) {
                        int selected = coinFlip() == 0 ? index1 : index2;
                        return recommendations[selected][0];
                    }
                }
                /**
                 * If user inputs invalid data, defaults to below
                 * */
                System.out.println("I'm sorry, I don't understand.");
                return null;
            case 3:
                if (recommendations.length > 1) {
                    int selected = coinFlip() == 0 ? 0 : 1;
                    return recommendations[selected][0];
                } else {
                    System.out.println("Not enough items to choose between.");
                    return null;
                }
            default:
                System.out.println("I'm sorry >.< I didn't catch that.");
                return null;
        }
    }
}