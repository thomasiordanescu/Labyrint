import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Replace with your JSON file path
            File file = new File("maze.json");
            MazeData mazeData = mapper.readValue(file, MazeData.class);

            // Print out some details to verify
            System.out.println("Maze width: " + mazeData.getWidth());
            System.out.println("Maze height: " + mazeData.getHeight());
            System.out.println("Number of horizontal walls: " + mazeData.getHwalls().size());
            System.out.println("Number of vertical walls: " + mazeData.getVwalls().size());
            System.out.println("Number of jumps: " + mazeData.getJumps().size());
            System.out.println("Number of quests: " + mazeData.getQuests().size());

            // Example: print the first quest
            if (!mazeData.getQuests().isEmpty()) {
                Quest firstQuest = mazeData.getQuests().get(0);
                System.out.println("First quest from (" + firstQuest.getFrom().getX() + ", " + firstQuest.getFrom().getY() + 
                                   ") to (" + firstQuest.getTo().getX() + ", " + firstQuest.getTo().getY() + ")");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
