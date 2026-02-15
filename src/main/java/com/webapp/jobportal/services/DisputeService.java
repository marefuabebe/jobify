package com.webapp.jobportal.services;

import com.webapp.jobportal.entity.Dispute;
import com.webapp.jobportal.entity.JobPostActivity;
import com.webapp.jobportal.entity.Users;
import com.webapp.jobportal.repository.DisputeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DisputeService {

    private final DisputeRepository disputeRepository;

    @Autowired
    public DisputeService(DisputeRepository disputeRepository) {
        this.disputeRepository = disputeRepository;
    }

    public Dispute createDispute(Dispute dispute) {
        return disputeRepository.save(dispute);
    }

    public List<Dispute> getAllDisputes() {
        return disputeRepository.findAll();
    }

    public List<Dispute> getPendingDisputes() {
        return disputeRepository.findByStatus("PENDING");
    }

    public List<Dispute> getDisputesByJob(JobPostActivity job) {
        return disputeRepository.findByJob(job);
    }

    public Optional<Dispute> getOne(Integer id) {
        return disputeRepository.findById(id);
    }

    public Dispute updateDispute(Dispute dispute) {
        return disputeRepository.save(dispute);
    }

    public void deleteDispute(Integer id) {
        disputeRepository.deleteById(id);
    }
}
