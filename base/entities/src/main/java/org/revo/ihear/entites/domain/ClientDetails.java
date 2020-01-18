package org.revo.ihear.entites.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Document
public class ClientDetails extends BaseClientDetails implements org.springframework.security.oauth2.provider.ClientDetails {
    @Transient
    @Override
    public boolean isSecretRequired() {
        return super.getClientSecret() != null;
    }


    @Transient
    @Override
    public boolean isScoped() {
        return super.getScope() != null && !super.getScope().isEmpty();
    }


    @Transient
    @Override
    public boolean isAutoApprove(String scope) {
        if (super.getAutoApproveScopes() == null) {
            return false;
        } else {
            Iterator var2 = super.getAutoApproveScopes().iterator();

            String auto;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                auto = (String) var2.next();
            } while (!auto.equals("true") && !scope.matches(auto));

            return true;
        }
    }

    @Transient
    @Override
    public Map<String, Object> getAdditionalInformation() {
        return new HashMap<>();
    }

}
