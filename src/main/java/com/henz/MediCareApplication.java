package com.henz;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import com.henz.data_access.OrderRepository;
import com.henz.data_access.ProductRepository;
import com.henz.data_access.UserRepository;
import com.henz.entity.Order;
import com.henz.entity.Product;
import com.henz.entity.User;

@SpringBootApplication
@Transactional
public class MediCareApplication implements CommandLineRunner{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	public static void main(String[] args) {
		SpringApplication.run(MediCareApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
		/*
		 * use these lines to add some orders
		 * 
		 * */
		
		/*User u = this.userRepository.findById(2L).get();
		
		//order no 1
		
		Order o1 = new Order();
		
		List<Product> productListOrder1 = new ArrayList<Product>();
		productListOrder1.add(this.productRepository.findById(1L));
		productListOrder1.add(this.productRepository.findById(2L));
		productListOrder1.add(this.productRepository.findById(3L));
		productListOrder1.add(this.productRepository.findById(4L));
		productListOrder1.add(this.productRepository.findById(5L));
		productListOrder1.add(this.productRepository.findById(6L));
		productListOrder1.add(this.productRepository.findById(7L));
		
		double total = 0;
		
		for(Product p: productListOrder1) {
			total += p.getPrice();
		}
		
		o1.setUser(u);
		o1.setProducts(productListOrder1);
		o1.setTotal(total);
		
		//order no 2
		Order o2 = new Order();
		
		List<Product> productListOrder2 = new ArrayList<Product>();
		productListOrder2.add(this.productRepository.findById(1L));
		productListOrder2.add(this.productRepository.findById(2L));
		productListOrder2.add(this.productRepository.findById(3L));
		productListOrder2.add(this.productRepository.findById(4L));
		productListOrder2.add(this.productRepository.findById(5L));
		productListOrder2.add(this.productRepository.findById(6L));
		
		total = 0;
		
		for(Product p: productListOrder2) {
			total += p.getPrice();
		}
		
		o2.setUser(u);
		o2.setProducts(productListOrder2);
		o2.setTotal(total);
		
		//order no 3
		Order o3 = new Order();
		
		List<Product> productListOrder3 = new ArrayList<Product>();
		productListOrder3.add(this.productRepository.findById(1L));
		productListOrder3.add(this.productRepository.findById(2L));
		productListOrder3.add(this.productRepository.findById(3L));
		productListOrder3.add(this.productRepository.findById(4L));
		productListOrder3.add(this.productRepository.findById(5L));
		
		total = 0;
		
		for(Product p: productListOrder3) {
			total += p.getPrice();
		}
		
		o3.setUser(u);
		o3.setProducts(productListOrder3);
		o3.setTotal(total);
		
		//order no 4
		Order o4 = new Order();
		
		List<Product> productListOrder4 = new ArrayList<Product>();
		productListOrder4.add(this.productRepository.findById(1L));
		productListOrder4.add(this.productRepository.findById(2L));
		productListOrder4.add(this.productRepository.findById(3L));
		productListOrder4.add(this.productRepository.findById(4L));
		
		total = 0;
		
		for(Product p: productListOrder4) {
			total += p.getPrice();
		}
		
		o4.setUser(u);
		o4.setProducts(productListOrder4);
		o4.setTotal(total);
		
		//order no 5
		Order o5 = new Order();
		
		List<Product> productListOrder5 = new ArrayList<Product>();
		productListOrder5.add(this.productRepository.findById(1L));
		productListOrder5.add(this.productRepository.findById(2L));
		productListOrder5.add(this.productRepository.findById(3L));
		
		total = 0;
		
		for(Product p: productListOrder5) {
			total += p.getPrice();
		}
		
		o5.setUser(u);
		o5.setProducts(productListOrder5);
		o5.setTotal(total);
		
		//order no 6
		Order o6 = new Order();
		
		List<Product> productListOrder6 = new ArrayList<Product>();
		productListOrder6.add(this.productRepository.findById(1L));
		productListOrder6.add(this.productRepository.findById(2L));
		
		total = 0;
		
		for(Product p: productListOrder6) {
			total += p.getPrice();
		}
		
		o6.setUser(u);
		o6.setProducts(productListOrder6);
		o6.setTotal(total);
		
		//order no 7
		Order o7 = new Order();
		
		List<Product> productListOrder7 = new ArrayList<Product>();
		productListOrder7.add(this.productRepository.findById(1L));
		
		total = 0;
		
		for(Product p: productListOrder7) {
			total += p.getPrice();
		}
		
		o7.setUser(u);
		o7.setProducts(productListOrder7);
		o7.setTotal(total);
		
		//save the orders
		this.orderRepository.saveNewOrder(o1);
		this.orderRepository.saveNewOrder(o2);
		this.orderRepository.saveNewOrder(o3);
		this.orderRepository.saveNewOrder(o4);
		this.orderRepository.saveNewOrder(o5);
		this.orderRepository.saveNewOrder(o6);
		this.orderRepository.saveNewOrder(o7);*/
	}

}
