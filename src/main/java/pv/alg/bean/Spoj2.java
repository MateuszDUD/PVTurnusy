package pv.alg.bean;

import lombok.Getter;
import lombok.Setter;
import pv.bean.Spoj;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Spoj2 extends Spoj {
    private List<Spoj2> possibleConnectionsFromThis;
    private List<Spoj2> possibleConnectionsToThis = new ArrayList<>();

    private Spoj2 connectedTo = null;
    private Spoj2 connectedFrom = null;

    public Spoj2(Spoj spoj) {
        super();
        super.id = spoj.getId();
        super.spoj = spoj.getSpoj();
        super.line = spoj.getLine();
        super.fromId = spoj.getFromId();
        super.fromName = spoj.getFromName();
        super.departure = spoj.getDeparture();
        super.toId = spoj.getToId();
        super.toName = spoj.getToName();
        super.arrival = spoj.getArrival();
        super.distanceInKm = spoj.getDistanceInKm();
    }
}
