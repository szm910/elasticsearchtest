package shizm.elasticsearchtest;

import java.io.Serializable;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * 查询条件参数
 * 
 * @author chen
 *
 */
public class QueryCondition implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -8314198299201893177L;
	private QueryTypeEnum queryType;// 查询类型
	private String conColumn;// 列 名
	private String conValue;// 列 值
	private String min;// 下界（用于范围查询）
	private String max;// 上界（用于范围查询）
	private LogicalRelationEnum includeMin;// 是否包含下界（用于范围查询）
	private LogicalRelationEnum includeMax;// 是否包含上界（用于范围查询）

	public QueryTypeEnum getQueryType() {
		return queryType;
	}

	public void setQueryType(QueryTypeEnum queryType) {
		this.queryType = queryType;
	}

	public String getConColumn() {
		return conColumn;
	}

	public void setConColumn(String conColumn) {
		this.conColumn = conColumn;
	}

	public String getConValue() {
		return conValue;
	}

	public void setConValue(String conValue) {
		this.conValue = conValue;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public LogicalRelationEnum getIncludeMin() {
		return includeMin;
	}

	public void setIncludeMin(LogicalRelationEnum includeMin) {
		this.includeMin = includeMin;
	}

	public LogicalRelationEnum getIncludeMax() {
		return includeMax;
	}

	public void setIncludeMax(LogicalRelationEnum includeMax) {
		this.includeMax = includeMax;
	}

}

	
