package com.platzi.pizza.service;

import com.platzi.pizza.persitence.repository.PizzaPagSortRepository;
import com.platzi.pizza.persitence.repository.PizzaRepository;
import com.platzi.pizza.service.dto.UpdatePizzaPriceDTO;
import com.platzi.pizza.service.exception.EmailAPIException;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import com.platzi.pizza.persitence.entity.PizzaEntity;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PizzaService {

    private final PizzaRepository pizzaRepository;
    private final PizzaPagSortRepository pizzaPagSortRepository;

    @Autowired
    public PizzaService(PizzaRepository pizzaRepository, PizzaPagSortRepository pizzaPagSortRepository) {
        this.pizzaRepository = pizzaRepository;
        this.pizzaPagSortRepository = pizzaPagSortRepository;
    }

    public Page<PizzaEntity> getAll(int page, int elements) {
        Pageable pageRequest = PageRequest.of(page, elements);
        return this.pizzaPagSortRepository.findAll(pageRequest);
    }

    public Page<PizzaEntity> getAvailable(int page, int elements, String sortBy, String sortDirection) {
        this.pizzaRepository.countByVeganTrue();
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageRequest = PageRequest.of(page, elements, sort);
        return this.pizzaPagSortRepository.findByAvailableTrue(pageRequest);
    }

    public PizzaEntity getByName(String name) {
        return this.pizzaRepository.findFirstByAvailableTrueAndNameIgnoreCase(name).orElseThrow(() -> new RuntimeException("La pizza no existe"));
    }

    public List<PizzaEntity> getWith(String ingredient) {
        return this.pizzaRepository.findAllByAvailableTrueAndDescriptionContainingIgnoreCase(ingredient);
    }

    public List<PizzaEntity> getWithout(String ingredient) {
        return this.pizzaRepository.findAllByAvailableTrueAndDescriptionNotContainingIgnoreCase(ingredient);
    }

    public List<PizzaEntity> getCheapest(double price) {
        return this.pizzaRepository.findTop3ByAvailableTrueAndPriceLessThanOrderByPriceAsc(price);
    }

    private void sendEmail() {
        throw new EmailAPIException();
    }
    @Transactional(noRollbackFor = EmailAPIException.class)
    public void updatePrice(UpdatePizzaPriceDTO updatePizzaPriceDTO) {
        this.pizzaRepository.updatePrice2(updatePizzaPriceDTO);
        this.sendEmail();
    }

    public PizzaEntity get(Integer id) {
        return this.pizzaRepository.findById(id).orElse(null);
    }

    public PizzaEntity save(PizzaEntity pizza) {
        return this.pizzaRepository.save(pizza);
    }

    public void delete(Integer id) {
        this.pizzaRepository.deleteById(id);
    }

    public boolean exists(Integer id) {
        return this.pizzaRepository.existsById(id);
    }

}
