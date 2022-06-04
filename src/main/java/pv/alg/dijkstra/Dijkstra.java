package pv.alg.dijkstra;

import lombok.Builder;
import lombok.Data;
import pv.bean.Edge;
import pv.bean.Node;

import java.util.*;

public class Dijkstra {

    public Map<Integer,Map<Integer, Integer>> solve(List<Node> nodes, List<Edge> edges) {
        Map<Integer,Map<Integer, Integer>> distances = new HashMap<>();

        nodes.forEach(n -> {
            distances.put(n.getId(), calculateDistancesFromNode(n.getId(), nodes, edges));
        });

        return distances;
    }

    private Map<Integer, Integer> calculateDistancesFromNode(int nodeId, List<Node> nodes, List<Edge> edges) {
        PriorityQueue<DNode> unProcessedNodes = new PriorityQueue<>((o1, o2) -> Integer.compare(o1.seconds, o2.seconds));
        Map<Integer, Integer> processedNodes = new HashMap<>();

        nodes.forEach(n -> {

            if (n.getId() == nodeId) {
                unProcessedNodes.add(DNode.builder()
                        .id(n.getId())
                        .seconds(0)
                        .build());
            } else {
                unProcessedNodes.add(DNode.builder()
                        .id(n.getId())
                        .seconds(Integer.MAX_VALUE)
                        .build());
            }
        });

        while (!unProcessedNodes.isEmpty()) {
            DNode current = unProcessedNodes.poll();

            processedNodes.put(current.id, current.seconds);

            edges.stream()
                    .filter(e -> e.getFromId() == current.id)
                    .forEach(e -> {
                        Optional<DNode> dn = unProcessedNodes.stream().filter(ee -> {return ee.id == e.getToId();}).findFirst();
                        if (dn.isPresent()) {
                            int dist = e.getSeconds() + current.getSeconds();

                            if (dn.get().seconds > dist) {
                                unProcessedNodes.remove(dn.get());
                                dn.get().seconds = dist;
                                unProcessedNodes.add(dn.get());
                            }
                        }
                    });
        }

        return processedNodes;
    }

    @Data
    @Builder
    private static class DNode {
        private int id;
        private int seconds;
    }
}
