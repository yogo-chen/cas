package org.apereo.cas.trusted.authentication.storage;

import com.google.common.collect.Sets;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Set;

/**
 * This is {@link RestMultifactorAuthenticationTrustStorage}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public class RestMultifactorAuthenticationTrustStorage extends BaseMultifactorAuthenticationTrustStorage {

    private String endpoint;

    public RestMultifactorAuthenticationTrustStorage(final String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public Set<MultifactorAuthenticationTrustRecord> get(final String principal) {
        final String url = (!this.endpoint.endsWith("/") ? this.endpoint.concat("/") : this.endpoint).concat(principal);
        return getResults(url);
    }

    @Override
    public void expire(final LocalDate onOrBefore) {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(this.endpoint, onOrBefore, Object.class);
    }

    @Override
    public void expire(final String key) {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(this.endpoint, key, Object.class);
    }

    @Override
    public Set<MultifactorAuthenticationTrustRecord> get(final LocalDate onOrAfterDate) {
        final String url = (!this.endpoint.endsWith("/") ? this.endpoint.concat("/") : this.endpoint).concat(onOrAfterDate.toString());
        return getResults(url);
    }

    @Override
    protected MultifactorAuthenticationTrustRecord setInternal(final MultifactorAuthenticationTrustRecord record) {
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<Object> response = restTemplate.postForEntity(this.endpoint, record, Object.class);
        if (response != null && response.getStatusCode() == HttpStatus.OK) {
            return record;
        }
        return null;
    }
    
    private Set<MultifactorAuthenticationTrustRecord> getResults(final String url) {
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<MultifactorAuthenticationTrustRecord[]> responseEntity =
                restTemplate.getForEntity(url, MultifactorAuthenticationTrustRecord[].class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            final MultifactorAuthenticationTrustRecord[] results = responseEntity.getBody();
            return Sets.newHashSet(results);
        }

        return Sets.newHashSet();
    }
}
