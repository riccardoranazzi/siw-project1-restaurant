package it.uniroma3.siw.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Product {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String name;
	    
	    private float price;
	    
	    private String description;
	    
	    @OneToOne(cascade = CascadeType.ALL)
	    @JoinColumn(name="image_id")
	    private Image image;

		public float getPrice() {
			return price;
		}

		public void setPrice(float price) {
			this.price = price;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			this.image = image;
		}

		public Product() {
			super();
			// TODO Auto-generated constructor stub
		}

		public Product(Long id, String name, Image image, float price, String descrption) {
			super();
			this.id = id;
			this.name = name;
			this.image = image;
			this.price=price;
			this.description=descrption;
		}
	    
	    
}
