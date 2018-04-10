package shizm.model;

public class Product {
	@Override
	public String toString() {
		return "Product [id=" + id + ", cName=" + cName + ", cCode=" + cCode + ", cClassName=" + cClassName
				+ ", cSpecItems=" + cSpecItems + ", cAuth=" + cAuth + "]";
	}

	public Long id;
	public String cName;
	public String cCode;
	public String cClassName;
	public String cSpecItems;
	public String cAuth;
	public String cTitle;

	public Product() {
	}

	public Product(Long id, String cName, String cCode, String cClassName, String cSpecItems, String cAuth,String cTitle) {
		this.id = id;
		this.cName = cName;
		this.cCode = cCode;
		this.cClassName = cClassName;
		this.cSpecItems = cSpecItems;
		this.cAuth = cAuth;
		this.cTitle=cTitle;
	}

}
