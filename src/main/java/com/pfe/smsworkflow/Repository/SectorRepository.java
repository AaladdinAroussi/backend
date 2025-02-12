package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.CategoryOffer;
import com.pfe.smsworkflow.Models.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorRepository extends JpaRepository<Sector,Long> {
    boolean existsByName(String name);
    List<Sector> findByCategoryOffers(CategoryOffer categoryOffer);


}
