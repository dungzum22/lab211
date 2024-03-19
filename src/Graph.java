
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

class Graph {
    public static final double MAX_VALUE = Double.POSITIVE_INFINITY; // Giá trị đại diện cho khoảng cách vô cùng
    private int V; // Số đỉnh
    private List<List<Node>> adj; // Danh sách cạnh kề

    // Lớp Node đại diện cho một cạnh có trọng số
    private static class Node implements Comparable<Node> {
        int v;
        double weight = 0;

        Node(int _v, double _w) {
            v = _v;
            weight = _w;
        }

        @Override
        public int compareTo(Node other) {
            // So sánh trọng số của các cạnh
            return Double.compare(this.weight, other.weight);
        }
    }

    // Hàm khởi tạo đồ thị từ ma trận khoảng cách
    public Graph(double[][] distances) {
        V = distances.length;
        adj = new ArrayList<>(V);
        for (int i = 0; i < V; i++)
            adj.add(new ArrayList<>());

        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                // Kiểm tra giá trị khoảng cách vô cùng
                if (distances[i][j] != MAX_VALUE)
                    adj.get(i).add(new Node(j, distances[i][j]));
            }
        }
    }

    // Thuật toán Dijkstra để tìm đường đi ngắn nhất từ một đỉnh đến tất cả các đỉnh khác
    public void dijkstra(int source) {
        double[] dist = new double[V];
        boolean[] visited = new boolean[V];
        Arrays.fill(dist, MAX_VALUE);
        dist[source] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.offer(new Node(source, 0));

        while (!pq.isEmpty()) {
            Node curr = pq.poll();
            int u = curr.v;
            if (visited[u])
                continue;
            visited[u] = true;

            for (Node neighbor : adj.get(u)) {
                int v = neighbor.v;
                double weight = neighbor.weight;
                if (dist[v] > dist[u] + weight) {
                    dist[v] = dist[u] + weight;
                    pq.offer(new Node(v, dist[v]));
                }
            }
        }

        System.out.println("Khoảng cách ngắn nhất từ đỉnh " + source + ":");
        for (int i = 0; i < V; i++)
            System.out.println("Đến đỉnh " + i + ": " + dist[i]);
    }

    // Thuật toán Prim để tìm cây khung nhỏ nhất
    public void prim() {
        double[] key = new double[V];
        int[] parent = new int[V];
        boolean[] inMST = new boolean[V];
        Arrays.fill(key, MAX_VALUE);
        key[0] = 0;

        for (int count = 0; count < V - 1; count++) {
            int u = minKey(key, inMST);
            inMST[u] = true;

            for (Node neighbor : adj.get(u)) {
                int v = neighbor.v;
                double weight = neighbor.weight;
                if (!inMST[v] && weight < key[v]) {
                    parent[v] = u;
                    key[v] = weight;
                }
            }
        }

        System.out.println("Cây khung nhỏ nhất:");
        double totalWeight = 0;
        for (int i = 1; i < V; i++) {
            System.out.println("(" + parent[i] + ", " + i + ")");
            totalWeight += key[i];
        }
        System.out.println("Trọng lượng tổng cộng: " + totalWeight);
    }

    private int minKey(double[] key, boolean[] inMST) {
        double min = MAX_VALUE;
        int minIndex = -1;
        for (int v = 0; v < V; v++) {
            if (!inMST[v] && key[v] < min) {
                min = key[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    public void kruskal() {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (int i = 0; i < V; i++) {
            for (Node neighbor : adj.get(i)) {
                pq.offer(neighbor);
            }
        }

        UnionFind uf = new UnionFind(V);
        List<Node> mst = new ArrayList<>();
        double totalWeight = 0; // Kiểu dữ liệu double cho totalWeight

        while (!pq.isEmpty() && mst.size() < V - 1) {
            Node curr = pq.poll();
            int u = curr.v;
            int v = uf.find(u);
            int w = uf.find(curr.v);
            if (v != w) {
                mst.add(curr);
                totalWeight += curr.weight; // Cộng trọng lượng của cạnh vào tổng
                uf.union(u, curr.v); // Cập nhật UnionFind
            }
        }

        System.out.println("Cây khung nhỏ nhất:");
        for (Node edge : mst)
            System.out.println("(" + edge.v + ", " + uf.find(edge.v) + ")");
        System.out.println("Trọng lượng tổng cộng: " + totalWeight);
    }
}
class UnionFind {
    private int[] parent;
    private int[] rank;

    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }
}


//    public static Graph readFromExcel(String excelFilePath) throws IOException {
//        FileInputStream file = new FileInputStream(excelFilePath);
//        HSSFWorkbook workbook = new HSSFWorkbook(file);
//        HSSFSheet sheet = workbook.getSheetAt(0);
//
//        int numRows = sheet.getPhysicalNumberOfRows();
//        int numCols = sheet.getRow(0).getPhysicalNumberOfCells();
//
//        int[][] distances = new int[numRows][numCols];
//
//        for (int i = 0; i < numRows; i++) {
//            Row row = sheet.getRow(i);
//            for (int j = 0; j < numCols; j++) {
//                Cell cell = row.getCell(j);
//                if (cell == null) {
//                    distances[i][j] = Graph.MAX_VALUE;
//                } else {
//                    distances[i][j] = (int) cell.getNumericCellValue();
//                }
//            }
//        }
//
//        file.close();
//        return new Graph(distances);
//    }
//}