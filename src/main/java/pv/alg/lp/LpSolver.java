package pv.alg.lp;

import lombok.extern.slf4j.Slf4j;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

@Slf4j
public class LpSolver {

    public void solve(String filePath) {
        try {
            LpSolve solver = LpSolve.readXLI("xli_CPLEX", filePath, null, "", LpSolve.NORMAL);
            if (solver != null) {
                solver.setScaling(LpSolve.SCALE_EXTREME );
                solver.solve();
            } else {
                log.error("Solver = null");
            }
        } catch (LpSolveException e) {
            log.error("Error", e);
        }
    }
}
