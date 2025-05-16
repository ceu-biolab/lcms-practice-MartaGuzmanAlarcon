package lipid;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

import java.util.HashSet;

public class LipidScoreUnit implements RuleUnitData {

    // !TODO insert here the code to store the data structures containing the facts where the rules will be applied


    private final DataStore<Annotation> annotations; // almaceno facts

    public LipidScoreUnit() {
        this(DataSource.createStore());
    }

    public LipidScoreUnit(DataStore<Annotation> annotations) {
        this.annotations = annotations;

    }

    public DataStore<Annotation> getAnnotations() {
        return annotations;
    }

}


// NOTES:
//1. Más carbonos (lípido más grande) -> debería tener más RT (porque tarda más en moverse por el sistema).