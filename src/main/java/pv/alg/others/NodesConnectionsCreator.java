package pv.alg.others;

import pv.alg.bean.Spoj2;
import pv.bean.Spoj;
import pv.config.GlobalConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class NodesConnectionsCreator {

    public static List<Spoj2> createPossibleConnections(List<Spoj> spojeSimple, Map<Integer, Map<Integer, Integer>> distances) {
        List<Spoj2> list = new ArrayList<>();

        spojeSimple.forEach(s -> {
            list.add(new Spoj2(s));
        });

        for (int i = 0; i < list.size(); i++) {
            List<Spoj2> possibleConnections = new ArrayList<>();

            for (int j = 0; j < list.size(); j++) {
                // from first Node
                Spoj2 a = list.get(i);
                Spoj2 b = list.get(j);

                int dist = distances.get(list.get(i).getToId()).get(list.get(j).getFromId());

                dist += GlobalConfig.M;

                int arrivalSec = list.get(i).getArrival().toSecondOfDay();
                int departureSec = list.get(j).getDeparture().toSecondOfDay();

                if (arrivalSec + dist < departureSec && departureSec - arrivalSec < 28*60*60) {
                    possibleConnections.add(list.get(j));
                    list.get(j).getPossibleConnectionsToThis().add(list.get(i));
                }
            }

            list.get(i).setPossibleConnectionsFromThis(possibleConnections);
            list.get(i).getPossibleConnectionsFromThis().sort(Comparator.comparing(Spoj::getDeparture));
            list.get(i).getPossibleConnectionsToThis().sort((o2, o1) -> o1.getArrival().compareTo(o2.getArrival()));
        }

        return list;
    }
}
