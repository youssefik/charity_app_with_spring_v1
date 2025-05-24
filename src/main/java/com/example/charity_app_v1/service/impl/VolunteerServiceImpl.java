package com.example.charity_app_v1.service.impl;

import com.example.charity_app_v1.model.Organization;
import com.example.charity_app_v1.repository.VolunteerRepository;
import com.example.charity_app_v1.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerRepository volunteerRepository;

    @Autowired
    public VolunteerServiceImpl(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public long countByOrganization(Organization organization) {
        return volunteerRepository.countByOrganization(organization);
    }

    @Override
    public Long countVolunteersByOrganization(Long organizationId) {
        return volunteerRepository.countByOrganizationId(organizationId);
    }
} 