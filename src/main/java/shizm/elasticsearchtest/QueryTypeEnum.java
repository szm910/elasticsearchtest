package shizm.elasticsearchtest;

/**
 * 查询类型
 * 
 * @author chen
 *
 */
public enum QueryTypeEnum {
	QUERYBYID("queryById", "根据id查询"), 
	TERMQUERY("termQuery", "精确匹配字段查询"), 
	LIKEQUERY("likeQuery", "多词模糊查询"), 
	RANGEQUERY( "rangeQuery", "当前字段值范围查询"), 
	QUERYNOT("queryNot", "匹配非当前值的查询"), 
	OTHERS("others", "其他类型查询");
	
	private String value;
	private String describe;

	QueryTypeEnum(String value, String describe) {
		this.value = value;
		this.describe = describe;
	}

	public String getValue() {
		return value;
	}

	public String getDescribe() {
		return describe;
	}
}