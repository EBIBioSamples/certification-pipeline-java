package uk.ac.ebi.biosamples.certservice.service;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.biosamples.certservice.Application;
import uk.ac.ebi.biosamples.certservice.model.*;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, properties = {"job.autorun.enabled=false"})
public class FileRecorderTest {

    @Qualifier("fileRecorder")
    @Autowired
    private Recorder recorder;

    @Test
    public void given_certificate_save_to_file() throws IOException {
        String data = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("json/ncbi-SAMN03894263.json"), "UTF8");
        Sample sample = new Sample("test-uuid", data);
        Checklist checklist = new Checklist("ncbi", "0.0.1", "schemas/ncbi-candidate-schema.json");
        Certificate certificate = new Certificate(sample, Collections.emptyList(), checklist);
        CertificationResult certificationResult = new CertificationResult(sample.getAccession());
        certificationResult.add(certificate);
        RecorderResult recorderResult = recorder.record(Collections.singleton(certificationResult));
        assertNotNull(recorderResult);
        assertEquals(certificate, recorderResult.getCertificates().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_null_certificate_throw_exception() throws IOException {
        recorder.record(null);
    }
}
