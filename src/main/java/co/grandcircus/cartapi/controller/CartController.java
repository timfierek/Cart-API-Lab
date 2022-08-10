package co.grandcircus.cartapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.grandcircus.cartapi.exceptions.ItemNotFoundException;
import co.grandcircus.cartapi.model.CartItem;
import co.grandcircus.cartapi.repository.CartRepository;

@CrossOrigin(origins = "https://gc-express-tester.surge.sh/")
@RestController
public class CartController {
	
	@Autowired
	private CartRepository cartRepo;
	
	@ResponseBody
	@ExceptionHandler(ItemNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String itemNotFoundHandler(ItemNotFoundException ex) {
		return ex.getMessage();
	}
	
	//Set starter data, delete everything else
	@GetMapping("/reset")
	public String reset() {
		cartRepo.deleteAll();
		
		//Add products
		CartItem item = new CartItem("canned tuna", 1.25, 4);
		cartRepo.insert(item);
		
		item = new CartItem("licorice", 3.00, 1);
		cartRepo.insert(item);
		
		item = new CartItem("oreos", 4.00, 42);
		cartRepo.insert(item);
		
		item = new CartItem("sushi", 12.99, 5);
		cartRepo.insert(item);
		
		item = new CartItem("bananas", 0.60, 12);
		cartRepo.insert(item);
		
		item = new CartItem("yerba mate", 2.98, 2);
		cartRepo.insert(item);
		
		return "Data reset.";
	}
	
	//get all items
	@GetMapping("/cart-items")
	public List<CartItem> readAll(@RequestParam(required=false) String product,
								  @RequestParam(required=false) Double maxPrice,
								  @RequestParam(required=false) String prefix,
								  @RequestParam(required=false) Integer pageSize){
		if(product != null) {
			return cartRepo.findByProduct(product);
		}
		else if(maxPrice != null) {
			return cartRepo.findByMaxPrice(maxPrice);
		}
		else if(prefix != null) {
			return cartRepo.findByProductStartingWith(prefix);
		}
		else if(pageSize != null) {
			List<CartItem> results = cartRepo.findAll();
			results = results.subList(0, pageSize);
			return results; //TODO: Learn pageable interface
		}
		else {
			return cartRepo.findAll();
		}
	}
	
	//get item by id
	@GetMapping("/cart-items/{id}")
	public CartItem readOne(@PathVariable("id") String id) {
		return cartRepo.findById(id).orElseThrow(() -> new ItemNotFoundException("ID Not Found"));
	}
	
	//add one item to db
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/cart-items")
	public CartItem addOne(@RequestBody CartItem item) {
		cartRepo.insert(item);
		return item;
	}
	
	//update an item by id
	@PutMapping("/cart-items/{id}")
	public CartItem updateOne(@RequestBody CartItem item, @PathVariable("id") String id) {
		item.setId(id);
		cartRepo.save(item);
		return item;
	}
	
	//delete one item
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/cart-items/{id}")
	public void deleteOne(@PathVariable("id") String id) {
		cartRepo.deleteById(id);
	}
	
	//get total of all cart items plus tax
	@GetMapping("/cart-items/total-cost")
	public double readTotal() {
		double total = 0;
		List<CartItem> items = cartRepo.findAll();
		for(CartItem item : items) {
			total += (item.getPrice() * item.getQuantity());
		}
		
		total *= 1.06;
		return total;
	}
	
	//update quantity of one item
	@PatchMapping("/cart-items/{id}/add")
	public CartItem updateQuantity(@PathVariable("id") String id,
									@RequestParam int count) { 
		CartItem cur = cartRepo.findById(id).orElseThrow(() -> new ItemNotFoundException("ID Not Found"));
		cur.setQuantity(cur.getQuantity() + count);
		cartRepo.save(cur);
		return cur;
	}
	
}

