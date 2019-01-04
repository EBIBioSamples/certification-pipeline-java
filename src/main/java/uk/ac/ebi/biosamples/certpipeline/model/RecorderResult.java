package uk.ac.ebi.biosamples.certpipeline.model;

import java.util.ArrayList;
import java.util.List;

public class RecorderResult {

    private final List<Certificate> certificates = new ArrayList<>();

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void add(Certificate certificate) {
        certificates.add(certificate);
    }

}