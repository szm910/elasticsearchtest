package shizm.elasticsearchtest;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class QueryContionBuilder {
	/**
	 * 准确查询 构造 列 conColumn 值为 conValue 查询条件
	 * 
	 * @param conColumn
	 * @param conValue
	 * @return
	 */
	public static QueryBuilder buildTermQuery(String conColumn, String conValue) {
		return QueryBuilders.termQuery(conColumn, conValue);// 精确
	}

	/**
	 * 模糊查询，匹配列conColumn，包含conValue的值， 支持通配符匹配
	 * 
	 * @param conColumn
	 * @param conValue
	 * @return
	 */
	public static QueryBuilder buildLikeQuery(String conColumn, String conValue) {
		return QueryBuilders.wildcardQuery(conColumn, "*" + conValue + "*");
	}

	/**
	 * 非查询 构造 列 conColumn 值不为 conValue 查询条件
	 * 
	 * @param conColumn
	 * @param conValue
	 * @return
	 */
	public static QueryBuilder buildTermNotQuery(String conColumn, String conValue) {
		QueryBuilder queryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(conColumn, conValue));
		return queryBuilder;

	}

	/**
	 * 范围查询 当前字段范围查询，默认包含上下界
	 * 
	 * @param conColumn
	 *            待查询列名
	 * @param min
	 *            下界
	 * @param max
	 *            上界
	 * @param includeMin
	 *            与下界的关系 >, >=
	 * @param includeMax
	 *            与上界的关系 <, <=
	 * @return
	 */
	public static QueryBuilder buildRangeQuery(String conColumn, String min, String max, LogicalRelationEnum includeMin,
			LogicalRelationEnum includeMax) {
		boolean minflag = true;
		boolean maxflag = true;
		if (includeMin != null && includeMin.equals(LogicalRelationEnum.GT)) {
			minflag = false;
		}
		if (includeMax != null && includeMax.equals(LogicalRelationEnum.LT)) {
			maxflag = false;
		}
		QueryBuilder queryBuilder = QueryBuilders.rangeQuery(conColumn).from(min).to(max).includeLower(minflag)
				.includeUpper(maxflag);
		return queryBuilder;
	}

	/**
	 * 单个条件查询 建立查询条件 根据查询条件 QueryCondition 中指定 的查询类型进行查询，
	 * 
	 * @param queryCondtion
	 * @return
	 */
	public static QueryBuilder buildQueryContion(QueryCondition queryCondtion) {
		QueryTypeEnum queryType = queryCondtion.getQueryType();// 查询类型
		String conColumn = queryCondtion.getConColumn();
		String conValue = queryCondtion.getConValue();

		String min = queryCondtion.getMin();
		String max = queryCondtion.getMax();
		LogicalRelationEnum includeMin = queryCondtion.getIncludeMin();
		LogicalRelationEnum includeMax = queryCondtion.getIncludeMax();
		QueryBuilder queryBuilder = null;

		switch (queryType) {
		case TERMQUERY:
			queryBuilder = buildTermQuery(conColumn, conValue);
			break;
		case LIKEQUERY:
			queryBuilder = buildLikeQuery(conColumn, conValue);
			break;
		case QUERYNOT:
			queryBuilder = buildTermNotQuery(conColumn, conValue);
			break;
		case RANGEQUERY:
			queryBuilder = buildRangeQuery(conColumn, min, max, includeMin, includeMax);
			break;
		default:
			break;
		}

		return queryBuilder;
	}

	/**
	 * must 条件绝对想等 多条件查询 建立多个查询条件 根据查询条件 QueryCondition 中指定 的查询类型进行查询，
	 * 
	 * @param list
	 * @return
	 */
	public static QueryBuilder buildQueryContion(List<QueryCondition> list) {
		BoolQueryBuilder boolqueryBuilder = QueryBuilders.boolQuery();
		for (QueryCondition queryCondtion : list) {
			QueryBuilder queryBuildert = buildQueryContion(queryCondtion);
			boolqueryBuilder.must(queryBuildert);

		}
		return boolqueryBuilder;
	}

	/**
	 * should 相当于 in 多条件查询
	 * 
	 * @param list
	 * @return
	 */
	public static QueryBuilder buildQueryInContion(List<QueryCondition> list) {
		BoolQueryBuilder boolqueryBuilder = QueryBuilders.boolQuery();
		for (QueryCondition queryCondtion : list) {
			QueryBuilder queryBuildert = buildQueryContion(queryCondtion);
			boolqueryBuilder.should(queryBuildert);
		}
		return boolqueryBuilder;
	}
}
