package com.henz.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="product")
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="product_id")
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String description;
	
	@Column(name="img_source")
	private String imgSource;
	
	@ManyToOne(fetch = FetchType.LAZY) //product is the owning side, meaning that the product table will have a column with category_id
	@JoinColumn(name="category_id", nullable = false)
	private Category category;
	
	@Column(nullable = false)
	private double price;

	@Override
	public String toString() {
		return "Product [id=" + id + ", description=" + description + ", imgSource=" + imgSource + ", category="
				+ category + ", price=" + price + "]";
	}
}
