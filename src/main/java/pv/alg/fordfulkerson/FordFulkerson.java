package pv.alg.fordfulkerson;

import lombok.Data;
import pv.alg.bean.Spoj2;
import pv.alg.others.NodesConnectionsCreator;
import pv.bean.Spoj;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Data
public class FordFulkerson {
    private Map<Integer, Map<Integer, Integer>> distances;
    private List<Spoj2> spoj2List;

    public void solve(Map<Integer, Map<Integer, Integer>> distances, List<Spoj> spoje) {
        this.distances = distances;
        this.spoj2List = NodesConnectionsCreator.createPossibleConnections(spoje, distances);

        alg();
    }

    private void alg() {
        int connectionsCount = 0;

        for (int i = 0; i < spoj2List.size(); i++) {
            Set<Spoj2> set = new TreeSet<Spoj2>((o1, o2) -> Integer.compare(o1.getId(), o2.getId()));

            if (al(set, spoj2List.get(i))) {
                connectionsCount++;
            }
        }
    }

    private boolean al(Set<Spoj2> set, Spoj2 n) {

        for (int i = 0; i < n.getPossibleConnectionsFromThis().size(); i++) {
            Spoj2 noderFrom = n.getPossibleConnectionsFromThis().get(i);
            if (!set.contains(noderFrom)) {
                set.add(noderFrom);

                if (noderFrom.getConnectedTo() == null || al(set, noderFrom.getConnectedTo())) {
                    noderFrom.setConnectedTo(n);
                    return true;
                }

            }

        }
        return false;
    }
}
