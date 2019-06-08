### Mapping
multi-fields. For instance, a string field could be mapped as a text field for full-text search, and as a keyword field for sorting or aggregations

In Elasticsearch, there is no dedicated array type. Any field can contain zero or more values by default, however, all values in the array must be of the same datatype. For instance:

granular fields:

Type mappings, object fields and nested fields contain sub-fields, called properties.

range type:

Relation parameter:  
WITHIN  
Matches documents who’s range field is entirely within the query’s range.
CONTAINS  
Matches documents who’s range field entirely contains the query’s range.
INTERSECTS  
Matches documents who’s range field intersects the query’s range. This is the default value when querying range fields.  

Mapping Parameters  
index:false  enabled:false  
_all and _source  
copy_to  null_value:1.0  coerce:false

Dynamic Template  
match_mapping_type:"string" match:"f_" dynamic:"strict"  
if dynamic is strict,you just can't add a filed to the mapping dynamically,but can be do by POST command.

### Fixed Data
what is ingest node?

Painless script:inline or stored  
accessing fields:ctx.field_name;ctx_source.field_name;doc[];  
_reindex  _update_by_query _delete_by_query  
version_type:"external"  conflicts:"proceed"  op_type:"create"  
how is reindex batch do?  
ingest node  
pipeline

### Cluster Management
node.master:true  master eligible  
node.data:true  data  
node.inget:true   
machine learnning  
coordinating only  
hot/warm architecture  
shard filtering  
tag nodes  
node.attr.{}={}
move shards to warm nodes  
shards allocation awareness  
index.routing.allocation.include.{attr}  
index.routing.allocation.exclude.{attr}  
index.routing.allocation.require.{attr}    
cluster.routing.allocation.awareness.attributes:{attr}  
### capacity planning

### Monitoring and alerting
