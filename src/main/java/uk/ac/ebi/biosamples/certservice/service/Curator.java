package uk.ac.ebi.biosamples.certservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.certservice.model.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Curator {

    private static Logger LOG = LoggerFactory.getLogger(Curator.class);
    private static Logger EVENTS = LoggerFactory.getLogger("events");

    private ConfigLoader configLoader;

    private Map<String, Plan> plansByCandidateChecklistID = new HashMap<>();

    public Curator(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    public List<PlanResult> runCurationPlans(InterrogationResult interrogationResult) {
        List<PlanResult> planResults = new ArrayList<>();
        if (interrogationResult == null) {
            String message = "cannot run curation plans on null interrogation result";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        if (interrogationResult.getSample() == null) {
            String message = "cannot run curation plans on null sample";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
        for (Checklist checklist : interrogationResult.getChecklists()) {
            PlanResult planResult = runCurationPlan(checklist, interrogationResult.getSample());
            if (planResult != null) {
                planResults.add(planResult);
            }
        }
        return planResults;
    }

    private PlanResult runCurationPlan(Checklist checklist, Sample sample) {
        Plan plan = plansByCandidateChecklistID.get(checklist.getID());
        PlanResult planResult = new PlanResult(sample, plan);
        if (plan == null) {
            EVENTS.info(String.format("%s plan not found for %s", sample.getAccession(), checklist.getID()));
            return planResult;
        }
        if (plansByCandidateChecklistID.containsKey(checklist.getID())) {
            for (Curation curation : plan.getCurations()) {
                CurationResult curationResult = plan.applyCuration(sample, curation);
                if (curationResult != null) {
                    planResult.addCurationResult(curationResult);
                }
            }
        }
        EVENTS.info(String.format("%s plan %s run", sample.getAccession(), plan.getID()));
        return planResult;
    }

    @PostConstruct
    public void init() {
        for (Plan plan : configLoader.config.getPlans()) {
            plansByCandidateChecklistID.put(plan.getCandidateChecklistID(), plan);
        }
    }
}
