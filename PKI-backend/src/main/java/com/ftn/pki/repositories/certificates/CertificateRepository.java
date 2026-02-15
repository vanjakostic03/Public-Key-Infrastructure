package com.ftn.pki.repositories.certificates;

import com.ftn.pki.entities.certificates.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    boolean existsBySerialNumber(String string);
}
