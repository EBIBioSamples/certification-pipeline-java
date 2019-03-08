package uk.ac.ebi.biosamples.certpipeline.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.certpipeline.model.CurationResult;
import uk.ac.ebi.biosamples.certpipeline.model.HasCuratedSample;
import uk.ac.ebi.biosamples.certpipeline.model.Sample;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Applicator {

    private static Logger LOG = LoggerFactory.getLogger(Certifier.class);

    public Sample apply(HasCuratedSample curationApplicable) {
        if (curationApplicable == null) {
            throw new IllegalArgumentException("cannot apply a null curation applyable");
        }
        Sample sample = curationApplicable.getSample();
        String document = makePretty(sample.getDocument());
        String updatedDocument = document;
        for (CurationResult curationResult : curationApplicable.getCurationResults()) {
            String pattern = String.format("\\\"%s\\\"\\s?[:]\\s?\\[\\W+?text\\\"\\s?[:]\\s?\\s\\\"(%s)\\\"", curationResult.getCharacteristic(), curationResult.getBefore());
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(updatedDocument);
            if (m.find()) {
                updatedDocument = updatedDocument.replace(m.group(1), curationResult.getAfter());
            } else {
                LOG.warn(String.format("%s failed to apply %s to sample", sample.getAccession(), curationResult.getCharacteristic()));
            }
        }
        Sample curatedSample = new Sample(sample.getAccession(), updatedDocument);
        return curatedSample;
    }

    private String makePretty(String document) {
        JSONObject json = new JSONObject(document);
        return json.toString(2);
    }
}
