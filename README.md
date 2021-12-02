# z2zzBlog
很简单的支持多用户访问和使用的博客系统。原型来自于《spring boot 企业级应用开发实战》没有weibo那么多的功能，仅支持一些基本功能，话不多说开始介绍功能:

用户管理
普通用户：注册用户，登录，

管理员：增加用户，修改用户，删除用户，搜索用户更换头像，修改信息

安全设置
角色授权，权限设置，主要基于spring security进行安全设置

博客管理
发博客，编辑，删除，分类，标签，添加图片，模糊查询，最新排序，最热排序，阅读量统计，点赞统计，评论统计

评论管理
发表评论，删除评论，统计评论量

点赞管理
点赞，取消点赞，点赞量统计

分类管理
创建分类，编辑分类，删除分类，按分类查询

标签管理
创建标签，删除标签，按标签查询

首页搜索
全文检索，最新博文，最热博文，热门标签，热门用户，热门文章

简易文件服务器
基于mongoDB存储用户上传的图片
一些项目重难点问题：

博客项目文章在数据库中怎么存，用什么数据类型特别长的文章会不会超出字段的长度限制，编码格式用的是什么。

该项目将博客内容存储在mysql和elasticsearch中

Mysql Long Text类型。

TEXT类型一般分为 TINYTEXT(255长度)、TEXT(65535)、 MEDIUMTEXT（int最大值16M），和LONGTEXT(long最大值4G)这四种，它被用来存储非二进制字符集，二进制字符集使用blob类型的字段来存储。

对于text列，插入时MySQL不会对它进行填充，并且select时不会删除任何末尾的字节。

如果text列被作为索引，则在它的内容后面添加空格时，会出现duplicate key错误，也就是说，如果我们定义了一个作为索引的text字段，它的值是'a',则不能定义一个值为'a '的记录，因为这样会产生冲突。

对text列进行排序的时候，决定顺序的字符个数是由参数max_sort_length来决定的

text和varchar的区别

SET max_sort_length=1000; 
SELECT id,comment FROM table ORDER BY comment;
在大多数情况下，我们可以把text视为varchar字段，但是这两个字段类型在存储字符大小上有一些区别：

    varchar在mysql中必须满足最大行宽度限制，也就是 65535(64k)字节，而varchar本身是按字符串个数来定义的,在mysql中使用uft-8字符集一个字符占用三个字节，所以单表varchar实际占用最大长度如下：
    1.使用utf-8字符编码集varchar最大长度是(65535-2)/3=21844个字符（超过255个字节会有2字节的额外占用空间开销，所以减2,如果是255以下,则减1）。
    2.使用 utf-8mb4字符集，mysql中使用 utf-8mb4 字符集一个字符占用4个字节，所以 varchar 最大长度是(65535-2)/4=16383 个字符（超过255个字节会有2字节的额外占用空间开销，所以减2,如果是255以下,则减1）。

    text的最大限制也是64k个字节,但是本质是溢出存储,innodb默认只会存放前768字节在数据页中,而剩余的数据则会存储在溢出段中。text类型的数据,将被存储在元数据表之外地方,但是varchar/char将和其他列一起存储在表数据文件中，值得注意的是，varchar列在溢出的时候会自动转换为text类型。text数据类型实际上将会大幅度增加数据库表文件尺寸。

    除此之外，二者还有以下的区别

1、当text作为索引的时候，必须 制定索引的长度，而当varchar充当索引的时候，可以不用指明。

2、text列不允许拥有默认值

3、当text列的内容很多的时候，text列的内容会保留一个指针在记录中，这个指针指向了磁盘中的一块区域，当对这个表进行select *的时候，会从磁盘中读取text的值，影响查询的性能，而varchar不会存在这个问题。

由于MySQL是单进程多线程模型，一个SQL语句无法利用多个cpu core去执行，这也就决定了MySQL比较适合OLTP(特点：大量用户访问、逻辑读，索引扫描，返回少量数据，SQL简单)业务系统，同时要针对MySQL去制定一些建模规范和开发规范，尽量避免使用Text类型，它不但消耗大量的网络和IO带宽，同时在该表上的DML操作都会变得很慢。

改进方法:

使用es存储
在MySQL中，一般log表会存储text类型保存request或response类的数据，用于接口调用失败时去手动排查问题，使用频繁的很低。可以考虑写入本地log file，通过filebeat抽取到es中，按天索引，根据数据保留策略进行清理。



使用对象存储
有些业务场景表用到TEXT，BLOB类型，存储的一些图片信息，比如商品的图片，更新频率比较低，可以考虑使用对象存储，例如阿里云的OSS，AWS的S3都可以，能够方便且高效的实现这类需求。

文章上有没有浏览次数展示？点赞次数怎么进行并发控制。怎么解决?

点赞业务本身并不复杂，无非是对数据的update，但是点赞本身是无意识行为，并且同一个用户可对博文进行点赞/取消点赞，如果直接操作数据库，无疑会增加数据库io操作。

方案:

缓存+异步推送
缓存+定时任务
优点:

降低对数据库的操作
提高点赞的效率
缺点:

redis挂掉，或者mq延迟使数据库数据与redis数据不一致(正在发生) 解决方案:定时同步redis与数据库数据
丢失数据 解决方案:MQ挂掉，不解决
原文链接:点赞功能设计与实现 - 云+社区 - 腾讯云

最新最热如何排序：

最热博客：

根据访问量，评论数，点赞量，发表时间（优先级依次递减）进行排序

最热标签，用户：

通过elasticsearch聚合查询然后按热度排序，取相应的前多少多少位数据即可。

    @Override
    public List<TagVO> listTop30Tags() {
 
        List<TagVO> list = new ArrayList<>();
 
        // 查询条件
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
                .withSearchType(SearchType.QUERY_THEN_FETCH).withIndices("blog").withTypes("blog")
                .addAggregation(terms("tags").field("tags")
                        .order(Terms.Order.count(false)).size(30)).build();
 
        // 聚合
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery,
                new ResultsExtractor<Aggregations>() {
                    @Override
                    public Aggregations extract(SearchResponse response) {
                        return response.getAggregations();
                    }
                });
 
        StringTerms modelTerms = (StringTerms) aggregations.asMap().get("tags");
 
        Iterator<Bucket> modelBucketIt = modelTerms.getBuckets().iterator();
        while (modelBucketIt.hasNext()) {
            Bucket actiontypeBucket = modelBucketIt.next();
 
            list.add(new TagVO(actiontypeBucket.getKey().toString(), actiontypeBucket.getDocCount()));
        }
        return list;
    }
 
    @Override
    public List<User> listTop12Users() {
 
        List<String> usernamelist = new ArrayList<>();// 存储排序后的用户账号
 
        // 查询条件
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
                .withSearchType(SearchType.QUERY_THEN_FETCH).withIndices("blog").withTypes("blog")
                .addAggregation(terms("users").field("username")
                        .order(Terms.Order.count(false)).size(12)).build();
        // 聚合
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery,
                new ResultsExtractor<Aggregations>() {
                    @Override
                    public Aggregations extract(SearchResponse response) {
                        return response.getAggregations();
                    }
                });
 
        StringTerms modelTerms = (StringTerms) aggregations.asMap().get("users");
 
        Iterator<Bucket> modelBucketIt = modelTerms.getBuckets().iterator();
        while (modelBucketIt.hasNext()) {
            Bucket actiontypeBucket = modelBucketIt.next();
            String username = actiontypeBucket.getKey().toString();
            usernamelist.add(username);
        }
 
        // 根据用户名，查出用户的详细信息
        List<User> list = userService.listUsersByUsernames(usernamelist);
 
        // 按照 usernamelist 的顺序返回用户对象
        List<User> returnList = new ArrayList<>();
 
        for (String username : usernamelist) {
            for (User user : list) {
                if (username.equals(user.getUsername())) {
                    returnList.add(user);
                    break;
                }
            }
        }
 
        return returnList;
    }
重点在于order,阅读order源码：

    /**
     * Sets the order in which the buckets will be returned.
     */
    public TermsAggregationBuilder order(Terms.Order order) {
        if (order == null) {
            throw new IllegalArgumentException("[order] must not be null: [" + name + "]");
        }
        this.order = order;
        return this;
    }
其中this.order为：

    private Terms.Order order = Terms.Order.compound(Terms.Order.count(false), Terms.Order.term(true));
Terms.Order.compound源码:

        /**
         * Creates a bucket ordering strategy which sorts buckets based multiple criteria
         *
         * @param   orders a list of {@link Order} parameters to sort on, in order of priority
         */
        public static Order compound(Order... orders) {
            return compound(Arrays.asList(orders));
        }
 
        public static Order compound(List<Order> orders) {
            return new InternalOrder.CompoundOrder(orders);
        }
该方法用于创造桶排序策略，由多个order共同决定 

        CompoundOrder(List<Terms.Order> compoundOrder) {
            this(compoundOrder, true);
        }
 
 
        CompoundOrder(List<Terms.Order> compoundOrder, boolean absoluteOrdering) {
            this.orderElements = new LinkedList<>(compoundOrder);
            Terms.Order lastElement = compoundOrder.get(compoundOrder.size() - 1);
            if (absoluteOrdering && !(InternalOrder.TERM_ASC == lastElement || InternalOrder.TERM_DESC == lastElement)) {
                // add term order ascending as a tie-breaker to avoid non-deterministic ordering
                // if all user provided comparators return 0.
                this.orderElements.add(Order.term(true));
            }
        }
 
term.order.count源码:

        public static Order count(boolean asc) {
            return asc ? InternalOrder.COUNT_ASC : InternalOrder.COUNT_DESC;
        }
    public static final InternalOrder TERM_ASC = new InternalOrder(TERM_ASC_ID, "_term", true, new Comparator<Terms.Bucket>() {
 
        @Override
        public int compare(Terms.Bucket o1, Terms.Bucket o2) {
            return o1.compareTerm(o2);
        }
    });
最后size()方法显而易见是设置完排序后的桶的大小后返回这个NativeSearchQueryBuilder。

不难理解，通过对标签建桶，然后统计桶内标签个数，然后排序，从而得到了最热标签列表。

(虽然代码没有指定根据什么进行排序，但是es内置排序默认以Dos_count进行排序)
