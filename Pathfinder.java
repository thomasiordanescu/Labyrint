import java.util.*;

/**
 * Pathfinder using Dijkstra. Depends on:
 * - MazeData (with getWidth(), getHeight(), getHwalls(), getVwalls(), getJumps())
 * - Wall (getX(), getY(), getCost() -> Double or double)
 * - Jump (getFrom(), getTo(), getCost())
 * - Coordinate (equals/hashCode)
 * - Edge (to, cost)
 */
public class Pathfinder {

    private final MazeData maze;
    private final int width;
    private final int height;

    // Optional: quick jump map from "x,y" -> List<Jump>
    private final Map<String, List<Jump>> jumpsFrom = new HashMap<>();

    public Pathfinder(MazeData maze) {
        this.maze = maze;
        this.width = maze.getWidth();
        this.height = maze.getHeight();

        if (maze.getJumps() != null) {
            for (Jump j : maze.getJumps()) {
                String k = key(j.getFrom());
                jumpsFrom.computeIfAbsent(k, kk -> new ArrayList<>()).add(j);
            }
        }
    }

    /**
     * Dijkstra from start to end. Returns a PathResult with path and cost.
     */
    public PathResult dijkstra(Coordinate start, Coordinate end) {
        String startKey = key(start);
        String endKey = key(end);

        // priority queue of nodes to visit
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.dist));
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();

        dist.put(startKey, 0.0);
        pq.add(new Node(startKey, 0.0));

        Set<String> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            if (visited.contains(node.id)) continue; // lazy deletion
            visited.add(node.id);

            if (node.id.equals(endKey)) break;

            Coordinate cur = parseCoord(node.id);

            for (Edge e : getNeighbors(cur)) {
                String nid = key(e.getTo());
                if (visited.contains(nid)) continue;

                double nd = node.dist + e.getCost();
                Double known = dist.get(nid);
                if (known == null || nd < known) {
                    dist.put(nid, nd);
                    prev.put(nid, node.id);
                    pq.add(new Node(nid, nd));
                }
            }
        }

        // if unreachable
        if (!dist.containsKey(endKey)) {
            return new PathResult(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }

        // reconstruct path
        List<Coordinate> path = new ArrayList<>();
        String cur = endKey;
        while (cur != null) {
            path.add(parseCoord(cur));
            cur = prev.get(cur);
        }
        Collections.reverse(path);
        return new PathResult(path, dist.get(endKey));
    }

    /**
     * Build neighbors (edges) for a coordinate.
     * Uses helper wall-cost methods defined below.
     * Each neighbor has cost >= 0. If helper returns -1 => blocked.
     */
    private List<Edge> getNeighbors(Coordinate c) {
        List<Edge> edges = new ArrayList<>();
        int x = c.getX();
        int y = c.getY();

        // RIGHT: (x+1, y)  -> need to check vertical wall at (x+1, y)
        if (x + 1 < width) {
            double cost = getWallCostHorizontal(x + 1, y, true); // moving left->right
            if (cost >= 0) edges.add(new Edge(new Coordinate(x + 1, y), cost));
        }

        // LEFT: (x-1, y) -> check vertical wall at (x, y)
        if (x - 1 >= 0) {
            double cost = getWallCostHorizontal(x, y, false); // moving right->left
            if (cost >= 0) edges.add(new Edge(new Coordinate(x - 1, y), cost));
        }

        // UP: (x, y+1) -> check horizontal wall at (x, y+1)
        if (y + 1 < height) {
            double cost = getWallCostVertical(x, y + 1, true); // moving bottom->top
            if (cost >= 0) edges.add(new Edge(new Coordinate(x, y + 1), cost));
        }

        // DOWN: (x, y-1) -> check horizontal wall at (x, y)
        if (y - 1 >= 0) {
            double cost = getWallCostVertical(x, y, false); // moving top->bottom
            if (cost >= 0) edges.add(new Edge(new Coordinate(x, y - 1), cost));
        }

        // Jumps: one-way; find jumps that start at this coordinate
        String k = key(c);
        List<Jump> js = jumpsFrom.get(k);
        if (js != null) {
            for (Jump j : js) {
                edges.add(new Edge(j.getTo(), j.getCost()));
            }
        }

        return edges;
    }

    /**
     * Helper: vertical barrier check (for moves up/down).
     * Interpretation:
     *  - hwalls at (x,y) block movement between (x, y-1) and (x, y).
     *  - If a wall entry exists AND wall.getCost() is null -> blocked (return -1).
     *  - If a wall entry exists with numeric cost -> return that cost.
     *  - If no wall entry -> return 1.0 (normal step cost).
     *
     * @param x x coordinate of the cell that contains the horizontal wall (the "upper" cell)
     * @param y y coordinate of the cell that contains the horizontal wall
     * @param movingBottomToTop true if moving from (x,y-1) -> (x,y)
     */
    private double getWallCostVertical(int x, int y, boolean movingBottomToTop) {
        if (maze.getHwalls() != null) {
            for (Wall w : maze.getHwalls()) {
                if (w.getX() == x && w.getY() == y) {
                    // treat missing/null cost as blocked
                    Double wc = getNullableCost(w);
                    if (wc == null) return -1;
                    return wc;
                }
            }
        }
        return 1.0;
    }

    /**
     * Helper: horizontal barrier check (for moves left/right).
     * Interpretation:
     *  - vwalls at (x,y) block movement between (x-1, y) and (x, y).
     *  - If a wall entry exists AND wall.getCost() is null -> blocked (return -1).
     *  - If a wall entry exists with numeric cost -> return that cost.
     *  - If no wall entry -> return 1.0 (normal step cost).
     *
     * @param x x coordinate of the cell that contains the vertical wall (the "right" cell)
     * @param y y coordinate
     * @param movingLeftToRight true if moving from (x-1,y) -> (x,y)
     */
    private double getWallCostHorizontal(int x, int y, boolean movingLeftToRight) {
        if (maze.getVwalls() != null) {
            for (Wall w : maze.getVwalls()) {
                if (w.getX() == x && w.getY() == y) {
                    Double wc = getNullableCost(w);
                    if (wc == null) return -1;
                    return wc;
                }
            }
        }
        return 1.0;
    }

    /**
     * Helper to read Wall cost in a null-safe way.
     * Works whether Wall.getCost() returns Double or double (primitive).
     */
    private Double getNullableCost(Wall w) {
        try {
            // if getCost() returns Double, this yields that value (may be null)
            return (Double) w.getCost();
        } catch (ClassCastException e) {
            // if getCost() returns primitive double, then it was autoboxed to Double implicitly
            // but JVM won't throw here normally. Just in case, use reflection fallback:
            try {
                // reflection: call getCost() and box to Double
                Object val = Wall.class.getMethod("getCost").invoke(w);
                if (val == null) return null;
                return ((Number) val).doubleValue();
            } catch (Exception ex) {
                // worst case: return null (treat as blocked)
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    // -- Utilities --

    private static class Node {
        final String id;
        final double dist;
        Node(String id, double dist) { this.id = id; this.dist = dist; }
    }

    public static class PathResult {
        public final List<Coordinate> path;
        public final double cost;
        public PathResult(List<Coordinate> path, double cost) { this.path = path; this.cost = cost; }
    }

    private static String key(Coordinate c) { return c.getX() + "," + c.getY(); }
    private static Coordinate parseCoord(String s) {
        String[] p = s.split(",");
        return new Coordinate(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
    }
}
