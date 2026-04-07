package es.urjc.daw04.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.urjc.daw04.model.Order;
import es.urjc.daw04.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByUser(User user, Pageable pageable);
    List<Order> findByUser(User user);

    @Query("SELECT p.category.name, SUM(i.quantity) FROM Order o JOIN o.items i JOIN i.product p GROUP BY p.category.name")
    List<Object[]> findUnitsSoldByCategory();

    @Query("SELECT FUNCTION('MONTH', o.orderDate), SUM(o.totalPrice) FROM Order o WHERE FUNCTION('YEAR', o.orderDate) = FUNCTION('YEAR', CURRENT_DATE) GROUP BY FUNCTION('MONTH', o.orderDate) ORDER BY FUNCTION('MONTH', o.orderDate)")
    List<Object[]> findMonthlySales();

    @Query("SELECT t, SUM(i.quantity) FROM Order o JOIN o.items i JOIN i.product p JOIN p.tags t GROUP BY t")
    List<Object[]> findUnitsSoldByTag();

    @Query("SELECT FUNCTION('MONTH', o.orderDate), COUNT(o) FROM Order o WHERE FUNCTION('YEAR', o.orderDate) = FUNCTION('YEAR', CURRENT_DATE) GROUP BY FUNCTION('MONTH', o.orderDate) ORDER BY FUNCTION('MONTH', o.orderDate)")
    List<Object[]> findOrdersCountByMonth();

    List<Order> findByOrderDateAfter(Date date);

}
