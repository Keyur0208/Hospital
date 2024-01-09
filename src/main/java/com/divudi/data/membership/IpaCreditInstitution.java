/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.divudi.data.membership;

import com.divudi.entity.Institution;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author safrin
 */
public class IpaCreditInstitution {

    Institution institution;
    List<IpaPaymentMethod> ipaPaymentMethods;

    public List<IpaPaymentMethod> getIpaPaymentMethods() {
        if (ipaPaymentMethods == null) {
            ipaPaymentMethods = new ArrayList<>();
        }
        return ipaPaymentMethods;
    }

    public void setIpaPaymentMethods(List<IpaPaymentMethod> ipaPaymentMethods) {
        this.ipaPaymentMethods = ipaPaymentMethods;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

}
