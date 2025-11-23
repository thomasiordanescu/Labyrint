import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        File f = null;
        if (args != null && args.length > 0) {
            f = new File(args[0]);
            if (!f.exists()) {
                System.out.println("WARNING: Can't find file " + args[0]);
                f = new File("maze.json");
                if (!f.exists()) {
                    System.out.println("WARNING: Can't find default file maze.json");
                    System.exit(1);
                }
            }
        } else {
            System.out.println("WARNING: no file passed as arg. Falling back to default maze.json");
            f = new File("maze.json");
            if (!f.exists()) {
                System.out.println("WARNING: Can't find default file maze.json");
                System.exit(1);
            }
        }
         
        MazeData data = mapper.readValue(f, MazeData.class);

        Pathfinder pf = new Pathfinder(data);

        for (Quest q : data.getQuests()) {
            printDistance(q.getFrom(), q.getTo(), pf);
        }
    }

    public static void printDistance(Coordinate from, Coordinate to, Pathfinder pf) {
        Pathfinder.PathResult result = pf.dijkstra(from, to);
        System.out.println("distance from (" + from.getX() + ", " + from.getY() +
                           ") to (" + to.getX() + ", " + to.getY() + "): " + result.cost);
    }
}
