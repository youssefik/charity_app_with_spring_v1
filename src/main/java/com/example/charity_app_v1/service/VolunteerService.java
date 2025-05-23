package com.example.charity_app_v1.service;

import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.model.Volunteer;

public interface VolunteerService {
    long countByOrganization(Organization organization);
    Long countVolunteersByOrganization(Long organizationId);
} 