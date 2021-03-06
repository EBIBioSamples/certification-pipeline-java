package uk.ac.ebi.biosamples.certservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.util.DigestUtils;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sample {

    private String accession;

    private String document;

    private String hash;

    public Sample(String accession, String document) {
        this.accession = accession;
        this.document = document;
    }

    public Sample() {
    }

    public String getAccession() {
        return accession;
    }

    @JsonIgnore
    public String getDocument() {
        return document;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "accession='" + accession + '\'' +
                ", document='" + document + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }

    public String getHash() {
        if (hash == null) {
            this.hash = DigestUtils.md5DigestAsHex(this.document.getBytes()).toUpperCase();
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sample sample = (Sample) o;
        return accession.equals(sample.accession) &&
                document.equals(sample.document) &&
                hash.equals(sample.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accession, document, hash);
    }
}
