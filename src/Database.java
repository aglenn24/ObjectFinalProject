import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Database holds 5 items: Name, Media Type, Genre, Completion, and Rating.
 * Tried to hold tags in the database, but had immense issues.
 */
public class Database {

    private static final String FILE_NAME = "database.txt";

    static class Item {
        String name;
        String mediaType;
        String genre;
        boolean completed;
        float rating;
        String[] tags;

        /**
         * Constructors
         */
        public Item() {
            this.name = "Default Name";
            this.mediaType = "Default Media Type";
            this.genre = "Default Genre";
            this.completed = false;
            this.rating = 0;
            this.tags = new String[0];
        }

        /**
         * No rating or tags
         */
        public Item(String name, String mediaType, String genre, boolean completed) {
            this.name = name;
            this.mediaType = mediaType;
            this.genre = genre;
            this.completed = completed;
            this.tags = new String[0];
        }

        /**
         * No tags
         */
        public Item(String name, String mediaType, String genre, boolean completed, float rating) {
            this.name = name;
            this.mediaType = mediaType;
            this.genre = genre;
            this.completed = completed;
            this.rating = rating;
            this.tags = new String[0];
        }

        /**
         * Full Constructor
         */
        public Item(String name, String mediaType, String genre, boolean completed, float rating, String[] tags) {
            this.name = name;
            this.mediaType = mediaType;
            this.genre = genre;
            this.completed = completed;
            this.rating = rating;
            this.tags = tags;
        }

        /**
         * Getter and setter for Name
         */
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        /**
         * Getter and setter for Media Type
         */
        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        /**
         * Getter and setter for Genre
         */
        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        /**
         * Getter and setter for Completion
         */
        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        /**
         * Getter and Setter for Rating
         */
        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        /**
         * Getter and Setter for Tags
         */
        public String[] getTags() {
            return tags;
        }

        public void setTags(String[] tags) {
            this.tags = tags;
        }

        /**
         * Add a new tag to the end of the tags array
         */
        public void addTag(String newTag) {
            String[] newTags = new String[tags.length + 1];
            System.arraycopy(tags, 0, newTags, 0, tags.length);
            newTags[tags.length] = newTag;
            this.tags = newTags;
        }

        public String toString() {
            return name + ", " + mediaType + ", " + genre + ", " + completed + ", " + rating;
        }
    }

    /**
     * Writes the database to the file
     */
    public void writeToFile(List<Item> items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Item item : items) {
                writer.write(item.name + "," + item.mediaType + "," + item.genre + "," + item.completed + "," + item.rating + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace(); // What is a better option for this?
        }
    }

    /**
     * Reads the database file
     */
    public List<Item> readFromFile() {
        List<Item> activities = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String ln;
            while ((ln = reader.readLine()) != null) {
                String[] parts = ln.split(",");
                if (parts.length == 5) {
                    Item item = new Item(parts[0], parts[1], parts[2], Boolean.parseBoolean(parts[3]), Float.parseFloat(parts[4])); //Used ChatGPT here
                    activities.add(item);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return activities;
    }

    /**
     * Filters the items in the database by the provided values
     * Used ChatGPT to help returning the filtered item
     * Use this or tagFilter???
     */
    public List<Item> filterItems(List<Item> items, String tag, String value) {
        return items.stream()
                .filter(item -> {
                    switch (tag.toLowerCase()) {
                        case "name":
                            return item.name.equals(value);
                        case "media type":
                            return item.mediaType.equals(value);
                        case "genre":
                            return item.genre.equals(value);
                        case "completed":
                            return Boolean.toString(item.completed).equals(value);
                        case "rating":
                            return Float.toString(item.rating).equals(value);
                        default:
                            return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public void addActivity() {
        List<Item> activities = readFromFile();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Let's get some information about this new activity!\n");
        System.out.print("Enter Activity Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter the Media Type: ");
        String mediaType = scanner.nextLine();

        System.out.print("Enter its Genre: ");
        String genre = scanner.nextLine();

        System.out.print("Have you watched it before? (true/false): ");
        boolean completed = Boolean.parseBoolean(scanner.nextLine());

        System.out.print("Enter Your Rating (0.0-10.0): ");
        float rating = Float.parseFloat(scanner.nextLine());

        System.out.print("Enter any tags you would like (separating by comma): ");
        String[] tags = scanner.nextLine().split(",");

        Item item = new Item(name, mediaType, genre, completed, rating, tags);

        activities.add(item);
        writeToFile(activities);
    }

    public void deleteActivity() {
        List<Item> activities = readFromFile();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select an activities to delete:");
        for (int i = 0; i < activities.size(); i++) {
            System.out.println((i + 1) + ". " + activities.get(i));
        }
        int choice = scanner.nextInt();
        activities.remove(choice);
        writeToFile(activities);
    }

    public void showAllActivities() {
        File file = new File("database.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Database file created as it did not exist.");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            if (Files.size(Paths.get("database.txt")) == 0) {
                System.out.println("Database is empty.");
            } else {
                System.out.println("Database Contents:");
                for (String ln : Files.readAllLines(Paths.get("database.txt"))) {
                    System.out.println(ln);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editDatabase() {
        File file = new File("database.txt");
        Scanner scanner = new Scanner(System.in);
        if (!file.exists()) {
            System.out.println("Database does not exist.");
            return;
        }
        try {
            java.util.List<String> ln = Files.readAllLines(Paths.get("database.txt"));
            if (ln.isEmpty()) {
                System.out.println("Database is empty.");
                return;
            }
            System.out.println("Select an activity to edit:");
            for (int i = 0; i < ln.size(); i++) {
                System.out.println((i + 1) + ". " + ln.get(i));
            }
            int choice = scanner.nextInt();
            if (choice > 0 && choice <= ln.size()) {
                System.out.println("Editing activity: " + ln.get(choice - 1));
                List<Item> items = readFromFile();
                if (choice > 0 && choice <= items.size()) {
                    Item selectedItem = items.get(choice - 1);
                    updateItemFields(selectedItem);
                    writeToFile(items);
                } else {
                    System.out.println("Invalid selection.");
                }

            } else {
                System.out.println("Invalid selection.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> tagFilter(String[] tags) {
        List<Item> items = readFromFile();
        List<String[]> resultList = new ArrayList<>();

        for (Item item : items) {
            int tagHits = 0;
            for (String tag : tags) {
                for (String itemTag : item.getTags()) {
                    if (itemTag.equalsIgnoreCase(tag)) {
                        tagHits++;
                    }
                }
            }
            resultList.add(new String[]{item.toString(), String.valueOf(tagHits)});
        }

        return resultList;
    }

    public List<String[]> noTagFilter(String username) {
        List<String[]> matchingItems = new ArrayList<>();
        List<Item> items = readFromFile();
        try (BufferedReader reader = new BufferedReader(new FileReader("login.txt"))) {
            String ln;
            while ((ln = reader.readLine()) != null) {
                String[] parts = ln.split(",");
                if (parts[0].equals(username)) {
                    String mediaType = parts[1]; // Can improve by making username into an Array and passing?
                    String genre = parts[2];
                    for (Item item : items) {
                        if (item.getMediaType().equals(mediaType) && item.getGenre().equals(genre)) {
                            matchingItems.add(new String[]{item.getName(), item.getMediaType(), item.getGenre(),
                                    String.valueOf(item.isCompleted()), String.valueOf(item.getRating())});
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matchingItems;
    }

    /**
     * Used ChatGPT to show the if(!value.isEmpty()) function is better than if{} else{}
     */
    private void updateItemFields(Item item) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter new Activity Name (current: " + item.getName() + "): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            item.setName(name);
        }

        System.out.print("Enter new Media Type (current: " + item.getMediaType() + "): ");
        String mediaType = scanner.nextLine();
        if (!mediaType.isEmpty()) {
            item.setMediaType(mediaType);
        }

        System.out.print("Enter new Genre (current: " + item.getGenre() + "): ");
        String genre = scanner.nextLine();
        if (!genre.isEmpty()) {
            item.setGenre(genre);
        }

        System.out.print("Have you completed it? (true/false, current: " + item.isCompleted() + "): ");
        String completedInput = scanner.nextLine();
        if (!completedInput.isEmpty()) {
            boolean completed = Boolean.parseBoolean(completedInput);
            item.setCompleted(completed);
        }

        System.out.print("Enter new Rating (current: " + item.getRating() + "): ");
        String ratingInput = scanner.nextLine();
        if (!ratingInput.isEmpty()) {
            float rating = Float.parseFloat(ratingInput);
            item.setRating(rating);
        }

        System.out.print("Enter new tags (comma separated, current: " + Arrays.toString(item.getTags()) + "): ");
        String tagsInput = scanner.nextLine();
        if (!tagsInput.isEmpty()) {
            String[] tags = tagsInput.split(",");
            item.setTags(tags);
        }
    }



    /** Potentially remove this code */
    /*
    public List<String> tagFilter(String[] tags) {

        List<Item> items = readFromFile();
        List<String> filteredList = new ArrayList<>();
        List<String> finalList = new ArrayList<>();
        int tagHits = 0;

        for (Item item : items) {
            for (String tag : tags) {
                for (String itemTag : item.getTags()) {
                    if (itemTag.equalsIgnoreCase(tag)) {
                        tagHits++;
                        filteredList.add(item.toString());
                    }
                }
                finalList.add(filteredList.toString());
                finalList.add(String.valueOf(tagHits));
            }
        }
        return finalList;

        /*
        Map<Item, Integer> itemTagHitsMap = new HashMap<>();

        for (Item item : items) {
            int tagHits = 0;

            for (String tag : tags) {
                for (String itemTag : item.getTags()) {
                    if (itemTag.equalsIgnoreCase(tag)) {
                        tagHits++;
                        filteredList.add(item.toString());
                    }
                }
            }

            itemTagHitsMap.put(item, tagHits);
        }

        for (Map.Entry<Item, Integer> entry : itemTagHitsMap.entrySet()) {
            System.out.println("Item: " + entry.getKey().toString() + ", Tag Hits: " + entry.getValue());
        }

        return filteredList;
         */


}

