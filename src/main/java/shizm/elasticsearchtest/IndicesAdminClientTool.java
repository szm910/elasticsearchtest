package shizm.elasticsearchtest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.alias.exists.AliasesExistResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.action.admin.indices.stats.CommonStats;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.admin.indices.stats.ShardStats;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.common.base.Objects;
import com.google.common.collect.UnmodifiableIterator;

public class IndicesAdminClientTool {
	Client client;
	IndicesAdminClient indicesAdminClient;

	public IndicesAdminClientTool() {
		try {
			client = TransportClient.builder().build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
			indicesAdminClient = client.admin().indices();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断索引是否存在
	 * 
	 * @param client
	 * @param index
	 * @return
	 */
	public static boolean isIndexExists(Client client, String index) {
		if (Objects.equal(client, null)) {
			// logger.info("--------- IndexAPI isIndexExists 请求客户端为null");
			return false;
		}
		if (StringUtils.isBlank(index)) {
			// logger.info("--------- IndexAPI isIndexExists 索引名称为空");
			return false;
		}
		IndicesAdminClient indicesAdminClient = client.admin().indices();
		IndicesExistsResponse response = indicesAdminClient.prepareExists(index).get();
		return response.isExists();
		/*
		 * 另一种方式 IndicesExistsRequest indicesExistsRequest = new
		 * IndicesExistsRequest(index); IndicesExistsResponse response =
		 * client.admin().indices().exists(indicesExistsRequest).actionGet();
		 */
	}

	/**
	 * 判断类型是否存在
	 * 
	 * @param client
	 * @param index
	 * @param type
	 * @return
	 */
	public static boolean isTypeExists(Client client, String index, String type) {
		if (!isIndexExists(client, index)) {
			// logger.info("--------- isTypeExists 索引 [{}] 不存在",index);
			return false;
		}
		IndicesAdminClient indicesAdminClient = client.admin().indices();
		TypesExistsResponse response = indicesAdminClient.prepareTypesExists(index).setTypes(type).get();
		return response.isExists();
	}

	/**
	 * 创建空索引 默认setting 无mapping
	 * 
	 * @param client
	 * @param index
	 * @return
	 */
	public static boolean createSimpleIndex(Client client, String index) {
		IndicesAdminClient indicesAdminClient = client.admin().indices();
		CreateIndexResponse response = indicesAdminClient.prepareCreate(index).get();
		return response.isAcknowledged();
	}

	/**
	 * 创建索引 指定setting
	 * 
	 * @param client
	 * @param index
	 * @return
	 */
	public static boolean createIndex(Client client, String index) {
		// settings
		Settings settings = Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2)
				.build();
		// mapping
		XContentBuilder mappingBuilder;
		try {
			mappingBuilder = XContentFactory.jsonBuilder().startObject().startObject(index).startObject("properties")
					.startObject("name").field("type", "string").field("store", "yes").endObject().startObject("sex")
					.field("type", "string").field("store", "yes").endObject().startObject("college")
					.field("type", "string").field("store", "yes").endObject().startObject("age")
					.field("type", "integer").field("store", "yes").endObject().startObject("school")
					.field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()
					.endObject().endObject().endObject();
		} catch (Exception e) {
			// logger.error("--------- createIndex 创建 mapping 失败：",e);
			return false;
		}
		IndicesAdminClient indicesAdminClient = client.admin().indices();
		CreateIndexResponse response = indicesAdminClient.prepareCreate(index).setSettings(settings)
				.addMapping(index, mappingBuilder).get();
		return response.isAcknowledged();
	}
	/**
     * 删除索引
     * @param client
     * @param index
     */
    public static boolean deleteIndex(Client client, String index) {
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        DeleteIndexResponse response = indicesAdminClient.prepareDelete(index).execute().actionGet();
        return response.isAcknowledged();
    }
    
    /**
     * 关闭索引
     * @param client
     * @param index
     * @return
     */
    public static boolean closeIndex(Client client, String index){
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        CloseIndexResponse response = indicesAdminClient.prepareClose(index).get();
        return response.isAcknowledged();
    }
    /**
     * 关闭索引
     * @param client
     * @param index
     * @return
     */
    public static boolean openIndex(Client client, String index){
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        OpenIndexResponse response = indicesAdminClient.prepareOpen(index).get();
        return response.isAcknowledged();
    }
    /**
     * 设置映射
     * @param client
     * @param index
     * @param type
     * @return
     */
    public static boolean putIndexMapping(Client client, String index, String type){
        // mapping
        XContentBuilder mappingBuilder;
        try {
            mappingBuilder = XContentFactory.jsonBuilder()
                    .startObject()
                        .startObject(type)
                            .startObject("properties")
                                .startObject("name").field("type", "string").field("store", "yes").endObject()
                                .startObject("sex").field("type", "string").field("store", "yes").endObject()
                                .startObject("college").field("type", "string").field("store", "yes").endObject()
                                .startObject("age").field("type", "long").field("store", "yes").endObject()
                                .startObject("school").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()
                            .endObject()
                        .endObject()
                    .endObject();
        } catch (Exception e) {
//            logger.error("--------- createIndex 创建 mapping 失败：", e);
            return false;
        }
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        PutMappingResponse response = indicesAdminClient.preparePutMapping(index).setType(type).setSource(mappingBuilder).get();
        return response.isAcknowledged();
    }
    /**
     * 为索引创建别名
     * @param client
     * @param index
     * @param alias
     * @return
     */
    public static boolean addAliasIndex(Client client, String index , String alias){
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        IndicesAliasesResponse response = indicesAdminClient.prepareAliases().addAlias(index, alias).get();
        return response.isAcknowledged();
    }
    /**
     * 判断别名是否存在
     * @param client
     * @param aliases
     * @return
     */
    public static boolean isAliasExist(Client client, String... aliases){
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        AliasesExistResponse response = indicesAdminClient.prepareAliasesExist(aliases).get();
        return response.isExists();
    }
    /**
     * 获取别名
     * @param client
     * @param aliases
     */
    public static void getAliasIndex(Client client, String... aliases){
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        GetAliasesResponse response = indicesAdminClient.prepareGetAliases(aliases).get();
        ImmutableOpenMap<String, List<AliasMetaData>> aliasesMap = response.getAliases();
        UnmodifiableIterator<String> iterator = aliasesMap.keysIt();
        while(iterator.hasNext()){
            String key = iterator.next();
            List<AliasMetaData> aliasMetaDataList = aliasesMap.get(key);
            for(AliasMetaData aliasMetaData : aliasMetaDataList){
//                logger.info("--------- getAliasIndex {}", aliasMetaData.getAlias());
            }
        }
    }
    
    /**
     * 删除别名
     * @param client
     * @param index
     * @param aliases
     * @return
     */
    public static boolean deleteAliasIndex(Client client, String index, String... aliases){
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        IndicesAliasesResponse response = indicesAdminClient.prepareAliases().removeAlias(index, aliases).get();
        return response.isAcknowledged();
    }
    /**
     * 更新设置
     * @param client
     * @param index
     * @param settings
     * @return
     */
    public static boolean updateSettingsIndex(Client client, String index, Settings settings){
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        UpdateSettingsResponse response = indicesAdminClient.prepareUpdateSettings(index).setSettings(settings).get();
        return response.isAcknowledged();
    }
    
    /**
     * 索引统计
     * @param client
     * @param index
     */
    public static void indexStats(Client client, String index) {
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        IndicesStatsResponse response = indicesAdminClient.prepareStats(index).all().get();
        ShardStats[] shardStatsArray = response.getShards();
        for(ShardStats shardStats : shardStatsArray){
//            logger.info("shardStats {}",shardStats.toString());
        }
        Map<String, IndexStats> indexStatsMap = response.getIndices();
        for(String key : indexStatsMap.keySet()){
//            logger.info("indexStats {}", indexStatsMap.get(key));
        }
        CommonStats commonStats = response.getTotal();
//        logger.info("total commonStats {}",commonStats.toString());
        commonStats = response.getPrimaries();
//        logger.info("primaries commonStats {}", commonStats.toString());
    }
}
