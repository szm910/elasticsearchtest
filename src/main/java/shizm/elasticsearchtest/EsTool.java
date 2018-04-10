package shizm.elasticsearchtest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
import static org.elasticsearch.index.query.QueryBuilders.*;

import shizm.model.Product;

public class EsTool {
	Client client;

	public EsTool() {
		try {
			client = TransportClient.builder().build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void createIndex() throws IOException {
		IndicesAdminClient indicesAdminClient = client.admin().indices();
		indicesAdminClient.prepareCreate("products").execute().actionGet();
		XContentBuilder builder = XContentFactory.jsonBuilder()
				.startObject()
					.startObject("products")
						.startObject("properties")
							.startObject("cName")
								.field("type", "string")
								.field("store", "yes")
								.field("index", "analyzed")
							.endObject()
							.startObject("cCode")
								.field("type", "string")
								.field("store", "yes")
								.field("index", "analyzed")
							.endObject()
							.startObject("cClassName")
								.field("type", "string")
								.field("store", "yes")
								.field("index", "analyzed")
							.endObject()
							.startObject("cAuth")
								.field("type", "string")
								.field("store", "yes")
								.field("index", "analyzed")
							.endObject()
							.startObject("cSpecItems")
								.field("type", "string")
								.field("store", "yes")
								.field("index", "analyzed")
							.endObject()
						.endObject()
					.endObject()
				.endObject();
		PutMappingRequest mapping = Requests.putMappingRequest("products").type("products").source(builder);
		client.admin().indices().putMapping(mapping).actionGet();
	}

	public void addDocument(Product p) throws IOException {
		IndexResponse response = client.prepareIndex("products", "products", null)
				.setSource(jsonBuilder().startObject().field("cName", p.cName).field("cCode", p.cCode)
						.field("cClassName", p.cClassName).field("cAuth", p.cAuth).field("cTitle", p.cTitle).endObject())
				.execute().actionGet();
		System.out.println(response.getId());
	}

	public void updateDocument() {

	}

	public void delDocument(String id) {
		DeleteResponse response = client.prepareDelete("products", "products", id).execute().actionGet();
		System.out.println(response.getId());
	}

	public void delIndex() {
		ClusterStateResponse response = client.admin().cluster().prepareState().execute().actionGet();
		// 获取所有索引
		String[] indexs = response.getState().getMetaData().getConcreteAllIndices();
		for (String index : indexs) {
			System.out.println(index + " delete");//
			// 清空所有索引。
			DeleteIndexResponse deleteIndexResponse = client.admin().indices().prepareDelete(index).execute()
					.actionGet();
			System.out.println(deleteIndexResponse.getHeaders());
		}
	}

	public List<Product> query(String... keywords) {

		QueryBuilder qb = boolQuery()
				.must(boolQuery()
						.should(matchQuery("cCode", "2code2"))
						.should(matchQuery("cName", "1name1")))
				.must(queryStringQuery(QueryParser.escape("cAuth:*$Test3$*")));
		System.out.println(qb.toString());
		SearchResponse response = client.prepareSearch().setQuery(qb).execute().actionGet();
		long nbHits = 0;
		List<Product> products = new ArrayList<Product>();
		System.out.println(response);
		for (SearchHit hit : response.getHits()) {
			Product p = new Product();
			p.cName = hit.getSource().get("cName").toString();
			p.cCode = hit.getSource().get("cCode").toString();
			products.add(p);
		}
		nbHits += response.getHits().getTotalHits();
		System.out.println(nbHits);

		return products;
	}

	public List<Product> multiQuery(String... keywords) {

		QueryBuilder qb = QueryBuilders.queryStringQuery(QueryParser.escape("cAuth:*$Test1$*"));

		QueryBuilder qb2 = QueryBuilders.queryStringQuery(QueryParser.escape("cName:*name2*"));

		SearchRequestBuilder srb1 = client.prepareSearch().setQuery(qb).setSize(10);
		SearchRequestBuilder srb2 = client.prepareSearch().setQuery(qb2).setSize(10);
		MultiSearchRequestBuilder mb = client.prepareMultiSearch().add(srb1).add(srb2);

		System.out.println("描述：" + mb.request().toString());
		MultiSearchResponse sr = mb.execute().actionGet();

		long nbHits = 0;
		List<Product> products = new ArrayList<Product>();
		for (MultiSearchResponse.Item item : sr.getResponses()) {
			SearchResponse response = item.getResponse();
			System.out.println(response);
			for (SearchHit hit : response.getHits()) {
				Product p = new Product();
				p.cName = hit.getSource().get("cName").toString();
				p.cCode = hit.getSource().get("cCode").toString();
				products.add(p);
			}
			nbHits += response.getHits().getTotalHits();
		}
		System.out.println(nbHits);

		return products;
	}

	public void close() {
		client.close();
	}
}
