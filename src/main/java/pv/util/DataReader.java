package pv.util;

import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import pv.bean.Edge;
import pv.bean.Node;
import pv.bean.Spoj;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DataReader {

    private final static String NODES_P = "src/main/resources/liptov/vrcholy.csv";
    private final static String EDGES_P = "src/main/resources/liptov/hrany.csv";
    private final static String SPOJE_P = "src/main/resources/liptov/spoje.csv";

    public static List<Spoj> readSpoje() {
        List<Spoj> spoje = new ArrayList<>();

        //TODO ...
//        int max= Integer.MAX_VALUE;
        int max= 10222;

        try (CSVReader csvReader = new CSVReader(new FileReader(SPOJE_P), ';');) {
            String[] values = null;
            csvReader.readNext();

            int id = 1;

            for (int i = 0;(values = csvReader.readNext()) != null && (i < max); i++) {
                String departure = values[4].length() == 8 ? values[4] : "0" + values[4];
                String arrival = values[7].length() == 8 ? values[7] : "0" + values[7];

                spoje.add(
                        Spoj.builder()
                                .id(id++)
                                .line(values[0])
                                .spoj(values[1])
                                .fromId(Integer.parseInt(values[2]))
                                .fromName(values[3])
                                .departure(LocalTime.parse(departure))
                                .toId(Integer.parseInt(values[5]))
                                .toName(values[6])
                                .arrival(LocalTime.parse(arrival))
                                .distanceInKm(Integer.parseInt(values[8]))
                                .build()
                );
            }
        } catch (IOException e) {
            log.error("Error ", e);
        }

        return spoje;
    }

    public static List<Edge> readEdges() {
        List<Edge> edges = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(EDGES_P));) {
            String[] values = null;
            csvReader.readNext();
            while ((values = csvReader.readNext()) != null) {
                edges.add(
                        Edge.builder()
                                .fromId(Integer.parseInt(values[0]))
                                .fromName(values[1])
                                .toId(Integer.parseInt(values[2]))
                                .toName(values[3])
                                .seconds(Integer.parseInt(values[4]))
                                .meters((int) (Double.parseDouble(values[5]) * 1000))
                                .build()
                );
            }
        } catch (IOException e) {
            log.error("Error ", e);
        }

        return edges;
    }

    public static List<Node> readNodes() {
        List<Node> nodes = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(NODES_P));) {
            String[] values = null;
            csvReader.readNext();
            while ((values = csvReader.readNext()) != null) {
                nodes.add(
                        Node.builder()
                                .id(Integer.parseInt(values[0]))
                                .name(values[1])
                                .build()
                );
            }
        } catch (IOException e) {
            log.error("Error ", e);
        }

        return nodes;
    }
}
