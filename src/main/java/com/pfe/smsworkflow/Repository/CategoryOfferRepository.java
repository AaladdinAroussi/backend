package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.CategoryOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryOfferRepository extends JpaRepository<CategoryOffer,Long> {
    CategoryOffer findByName(String name);
    boolean existsByName(String name);

}
