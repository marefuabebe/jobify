package com.webapp.jobportal.services;

import com.webapp.jobportal.entity.Testimonial;
import com.webapp.jobportal.repository.TestimonialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestimonialsService {

    private final TestimonialRepository testimonialRepository;

    @Autowired
    public TestimonialsService(TestimonialRepository testimonialRepository) {
        this.testimonialRepository = testimonialRepository;
    }

    public List<Testimonial> getAllTestimonials() {
        return testimonialRepository.findAll();
    }

    public void saveTestimonial(Testimonial testimonial) {
        testimonialRepository.save(testimonial);
    }

    public void deleteTestimonial(Integer id) {
        testimonialRepository.deleteById(id);
    }

    public Testimonial getTestimonialById(Integer id) {
        return testimonialRepository.findById(id).orElse(null);
    }
}
