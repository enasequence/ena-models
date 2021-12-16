/*
 * Copyright 2018-2021 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.ac.ebi.ena.webin.cli.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import uk.ac.ebi.ena.webin.cli.service.exception.ServiceException;
import uk.ac.ebi.ena.webin.cli.service.exception.ServiceMessage;
import uk.ac.ebi.ena.webin.cli.validator.reference.Sample;

public class 
SampleService extends WebinService
{
    protected 
    SampleService( AbstractBuilder<SampleService> builder )
    {
        super( builder );
    }

    public static class
    Builder extends AbstractBuilder<SampleService> {
        @Override
        public SampleService
        build() {
            return new SampleService(this);
        }
    }

    private static class SampleResponse {
        public int taxId;
        public String id;
        public String organism;
        public String bioSampleId;
        public boolean canBeReferenced;
    }

    
    public Sample getSample(String sampleId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<SampleResponse> response = executeHttpGet( restTemplate ,  getAuthHeader(),  sampleId,  getTest());
        SampleResponse sampleResponse = response.getBody();
        if (sampleResponse == null || !sampleResponse.canBeReferenced) {
            throw new ServiceException(ServiceMessage.SAMPLE_SERVICE_VALIDATION_ERROR.format(sampleId));
        }
        Sample sample = new Sample();
        sample.setBioSampleId(sampleResponse.bioSampleId);
        sample.setTaxId(sampleResponse.taxId);
        sample.setOrganism(sampleResponse.organism);
        sample.setSraSampleId(sampleResponse.id);
        
        return sample;
    }

    private ResponseEntity<SampleResponse> executeHttpGet(RestTemplate restTemplate , HttpHeaders headers, String sampleId, boolean test){
        return restTemplate.exchange(
                getWebinRestUri("cli/reference/sample/{id}", test),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                SampleResponse.class,
                sampleId.trim());
    }
}