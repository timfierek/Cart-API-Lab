package co.grandcircus.cartapi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import co.grandcircus.cartapi.model.CartItem;

public interface CartRepository extends MongoRepository<CartItem, String> {

	List<CartItem> findByProduct(String product);

	List<CartItem> findByProductStartingWith(String regexp);
	
	@Query("{'price' : {$lte: ?0}}") 
	List<CartItem> findByMaxPrice(double maxPrice);
	
	@Query("$query:{}, $limit:2")
	List<CartItem> findAll(int limit);

	//Non functioning methods
//	@Query("{$maxScan: ?0}")
//	List<CartItem> findAllLimit(int limit);
//
//	@Query("db.cartItems.find().limit(?0)") List<CartItem> findAllLimit(int num);
//	  
//	@Query("{'price' : {$lte: 'maxPrice'}}") List<CartItem> findByMaxPrice(double maxPrice);
//	  
//	@Query("{'product' : /^prefix/}") List<CartItem> findByProductPrefix(String prefix);
//	
//	Page<CartItem> findAll(Pageable pageable);

}
