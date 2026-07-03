package com.hospital;

import com.hospital.model.Department;
import com.hospital.model.Hospital;
import com.hospital.model.Ward;
import com.hospital.service.AdmissionService;
import com.hospital.service.ReportService;
import com.hospital.service.StaffService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class HospitalConfig implements WebMvcConfigurer {

    @Bean
    public Hospital hospital() {
        Hospital hospital = new Hospital("City General Hospital");
        // Pre-load some wards and departments as per the original requirements
        try {
            hospital.addDepartment(new Department("Cardiology"));
            hospital.addDepartment(new Department("Neurology"));
            hospital.addDepartment(new Department("Orthopedics"));
            hospital.addDepartment(new Department("Emergency"));

            hospital.addWard(new Ward("General Ward A", 20));
            hospital.addWard(new Ward("ICU", 10));
            hospital.addWard(new Ward("Maternity", 15));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hospital;
    }

    @Bean
    public StaffService staffService(Hospital hospital) {
        return new StaffService(hospital);
    }

    @Bean
    public ReportService reportService(Hospital hospital) {
        return new ReportService(hospital);
    }

    @Bean
    public AdmissionService admissionService(Hospital hospital) {
        return new AdmissionService(hospital);
    }

    // CORS config for Vite React Frontend (runs on 5173 usually)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
