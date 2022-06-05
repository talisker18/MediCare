package com.henz.data_access;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.henz.entity.Category;
import com.henz.entity.Product;
import com.henz.model.ProductCategoryModel;

/*
 * using EntityManager in Spring (source: Baeldung)
 * 
 * It seems that one EntityManager instance should be shared for all operations. However, the container (JakartaEE or Spring) injects a special proxy instead of a simple EntityManager here. Spring, for example, injects a proxy of type SharedEntityManagerCreator. 

Every time we use the injected EntityManager, this proxy will either reuse the existing EntityManager or create a new one. Reuse usually occurs when we enable something like Open Session/EntityManager in View. 

Either way, the container ensures that each EntityManager is confined to one thread.
 * 
 * */

@Repository
@Transactional
public class ProductRepository {

	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	public Product findById(Long id) {
		Product p = this.entityManager.find(Product.class, id);
		
		if(p != null) {
			return p;
		}else {
			throw new EntityNotFoundException("product with id: "+id+" not found");
		}
	}
	
	public ProductCategoryModel findByIdAndReturnProductCategoryModel(Long id) {
		Product p = this.entityManager.find(Product.class, id);
		
		if(p != null) {
			ProductCategoryModel pcm = new ProductCategoryModel();
			pcm.setProductId(p.getId());
			pcm.setCategoryId(p.getCategory().getId());
			pcm.setCategoryText(p.getCategory().getCategory().toString());
			pcm.setProductDescription(p.getDescription());
			pcm.setProductImgSource(p.getImgSource());
			pcm.setProductPrice(p.getPrice());
			pcm.setEnabled(p.isEnabled());
			return pcm;
		}else {
			throw new EntityNotFoundException("product with id: "+id+" not found");
		}
	}
	
	public ProductCategoryModel findNewestProductAndReturnProductCategoryModel() {
		
		TypedQuery<Product> query = this.entityManager.createQuery("Select p from Product p order by p.id desc",Product.class);
		query.setMaxResults(1);
		Product p = query.getSingleResult();
		
		System.out.println(p.getId());
		
		ProductCategoryModel pcm = new ProductCategoryModel();
		pcm.setProductId(p.getId());
		pcm.setCategoryId(p.getCategory().getId());
		pcm.setCategoryText(p.getCategory().getCategory().toString());
		pcm.setProductDescription(p.getDescription());
		pcm.setProductImgSource(p.getImgSource());
		pcm.setProductPrice(p.getPrice());
		pcm.setEnabled(p.isEnabled());
		return pcm;
	}
	
	//uses a native sql query to get the top five sellers
	public List<ProductCategoryModel> getTopFiveSellersAndReturnProductCategoryModel() {
		Query query = entityManager.createNativeQuery("select p.product_id, p.description, p.enabled, p.img_source, p.price, p.category_id from product p inner join order_product op on p.product_id = op.product_id group by p.product_id order by count(p.product_id) desc limit 5;", Product.class);
		
		@SuppressWarnings("unchecked")
		List<Product> productList = query.getResultList();
		
		List<ProductCategoryModel> listToReturn = new ArrayList<ProductCategoryModel>();
		
		for(Product p: productList) {
			ProductCategoryModel pcm = new ProductCategoryModel();
			pcm.setProductId(p.getId());
			pcm.setCategoryId(p.getCategory().getId());
			pcm.setCategoryText(p.getCategory().getCategory().toString());
			pcm.setProductDescription(p.getDescription());
			pcm.setProductImgSource(p.getImgSource());
			pcm.setProductPrice(p.getPrice());
			pcm.setEnabled(p.isEnabled());
			
			listToReturn.add(pcm);
		}
		
		return listToReturn;
	}
	
	public void disableById(Long id) {
		Product p = this.findById(id);
		
		if(p != null) {
			p.setEnabled(false);
			this.entityManager.merge(p);
		}else {
			throw new EntityNotFoundException("product with id: "+id+" not found");
		}
	}
	
	public void enableById(Long id) {
		Product p = this.findById(id);
		
		if(p != null) {
			p.setEnabled(true);
			this.entityManager.merge(p);
		}else {
			throw new EntityNotFoundException("product with id: "+id+" not found");
		}
	}
	
	public void saveNewProduct(@NotNull Product p) {
		this.entityManager.persist(p);
	}
	
	public void updateProduct(@NotNull Product p) {
		this.entityManager.merge(p);
	}
	
	//description of product is unique
	public Optional<Product> findByDescription(String description) {
		TypedQuery<Product> query = this.entityManager.createQuery("Select p from Product p where p.description =:description",Product.class);
		query.setParameter("description", description);
		Optional<Product> p = Optional.ofNullable(query.getSingleResult());
		return p;
	}
	
	public boolean saveNewProductByUsingProductCategoryModel(ProductCategoryModel productCategoryModel, MultipartFile file) {
		Category c = this.categoryRepository.findById(productCategoryModel.getCategoryId());
		
		//copy the img and save in src/main/resources
		Path path = null;
		try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            //save relative path so we can use it directly in the view
            
            if(!file.getOriginalFilename().equals("")) {
            	path = Paths.get("src/main/resources/static/img/" + file.getOriginalFilename());
                Files.write(path, bytes);
            }

            Product p;
            
            if(productCategoryModel.getProductId() ==  null) {
            	//save new product
            	p = new Product();
            	p.setDescription(productCategoryModel.getProductDescription());
        		p.setCategory(c);
        		p.setPrice(productCategoryModel.getProductPrice());
        		p.setEnabled(productCategoryModel.isEnabled());
        		
        		if(!file.getOriginalFilename().equals("")) {
        			p.setImgSource("../img/"+file.getOriginalFilename());
        		}else {
        			p.setImgSource(""); //empty String when user chooses not to upload an img
        		}
        		
        		this.entityManager.persist(p);
            }else {
            	//update product
            	p = this.findById(productCategoryModel.getProductId());
            	p.setDescription(productCategoryModel.getProductDescription());
        		p.setCategory(c);
        		p.setPrice(productCategoryModel.getProductPrice());
        		p.setEnabled(productCategoryModel.isEnabled());
        		
        		if(!file.getOriginalFilename().equals("")) {
        			p.setImgSource("../img/"+file.getOriginalFilename());
        		}else {
        			p.setImgSource(""); //empty String when user chooses not to upload an img
        		}
        		
        		this.entityManager.merge(p);
            }
           		
    		return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
	}
	
	public List<Product> getAllProducts(){
		return this.entityManager.createQuery("Select p from Product p",Product.class).getResultList();
	}
	
	public List<ProductCategoryModel> getAllProductsByProductCategoryModel(){
		List<Product> productList = this.getAllProducts();
		List<ProductCategoryModel> listWithProductCategoryModel = new ArrayList<ProductCategoryModel>();
		
		for(Product p: productList) {
			ProductCategoryModel pcm = new ProductCategoryModel();
			pcm.setProductId(p.getId());
			pcm.setCategoryId(p.getCategory().getId());
			pcm.setCategoryText(p.getCategory().getCategory().toString());
			pcm.setProductDescription(p.getDescription());
			pcm.setProductImgSource(p.getImgSource());
			pcm.setProductPrice(p.getPrice());
			pcm.setEnabled(p.isEnabled());
			listWithProductCategoryModel.add(pcm);
		}
		
		return listWithProductCategoryModel;
	}
	
	public List<ProductCategoryModel> getAllEnabledProductsByProductCategoryModel(){
		List<Product> productList = this.getAllProducts();
		List<ProductCategoryModel> listWithProductCategoryModel = new ArrayList<ProductCategoryModel>();
		
		for(Product p: productList) {
			ProductCategoryModel pcm = new ProductCategoryModel();
			pcm.setProductId(p.getId());
			pcm.setCategoryId(p.getCategory().getId());
			pcm.setCategoryText(p.getCategory().getCategory().toString());
			pcm.setProductDescription(p.getDescription());
			pcm.setProductImgSource(p.getImgSource());
			pcm.setProductPrice(p.getPrice());
			pcm.setEnabled(p.isEnabled());
			
			if(pcm.isEnabled()) {
				listWithProductCategoryModel.add(pcm);
			}
		}
		
		return listWithProductCategoryModel;
	}
	
	public List<Product> convertListWithModelObjectsToListWithProductEntities(List<ProductCategoryModel> listWithModels){
		List<Product> listWithEntities = new ArrayList<Product>();
		
		for(ProductCategoryModel pcm: listWithModels) {
			listWithEntities.add(this.findById(pcm.getProductId()));
		}
		
		return listWithEntities;
	}
	
	public List<ProductCategoryModel> convertListWithProductEntitiesToListWithModelObjects(List<Product> listWithEntities){
		List<ProductCategoryModel> listWithModels = new ArrayList<ProductCategoryModel>();
		
		for(Product p: listWithEntities) {
			ProductCategoryModel pcm = new ProductCategoryModel();
			pcm.setProductId(p.getId());
			pcm.setProductImgSource(p.getImgSource());
			pcm.setProductPrice(p.getPrice());
			pcm.setCategoryId(p.getCategory().getId());
			pcm.setCategoryText(p.getCategory().getCategory().toString());
			pcm.setProductDescription(p.getDescription());
			pcm.setEnabled(p.isEnabled());
			
			listWithModels.add(pcm);
		}
		
		return listWithModels;
	}
}
