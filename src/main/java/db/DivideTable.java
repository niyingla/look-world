package db;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DivideTable {
    /**
     * 列 列值类型 Pair 数组
     */
    volatile ArrayList<Pair<String, String>> columns;

    @Test
    public void test()throws Exception{
        //总计批量数
        Integer batch = 1000;
        //单次完成计数器
        CountDownLatch countDownLatch = new CountDownLatch(batch);
        //获取当前线程链接 当前县城没有数据 就会走init的方法
        ThreadLocal<Connection> threadLocal = ThreadLocal.withInitial(() -> {
            try {
                //指定配置文件
                File file = new File("./data/sql/sharding.yml");
                //创建数据源
                DataSource dataSource = YamlShardingSphereDataSourceFactory.createDataSource(file);
                //获取链接
                return dataSource.getConnection();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        //计数
        AtomicInteger counter = new AtomicInteger();
        //订阅个数
        Observable.range(0, batch)
                //多少个数据一个窗口
                .window(100)
                //发起订阅
                .subscribe(o ->
                        // newThread开始 一步执行订阅内容
                        o.subscribeOn(Schedulers.newThread())
                        //start - end 之间的当前订阅数
                        .subscribe(batchId -> {
                            try {
                                batchId = counter.incrementAndGet();
                                //获取当前线程链接
                                Connection connection = threadLocal.get();
                                //查询数据
                                try ( Statement statement = connection.createStatement()) {
                                    //查询結果
                                    ResultSet resultSet = statement.executeQuery(String.format("select * from huge.user limit %d , %d ", batchId * 10000 * 10000));
                                    //插入数据
                                    copy("t_user", resultSet, threadLocal.get());
                                    //关闭
                                    resultSet.close();
                                }
                                //计数
                                countDownLatch.countDown();
                                //打印
                                System.out.format("% finished %d/%d\n ", batchId, batch - countDownLatch.getCount(), batch);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }));
        //线程等待
        countDownLatch.await();
    }

    /**
     * 复制数据
     *
     * @param table
     * @param resultSet
     * @param connection
     * insert into table (id ,col2) values(100,xxx)
     */
    private void copy(String table, ResultSet resultSet, Connection connection) throws SQLException{
        //双重检查锁 单例创建列集合
        if (columns == null) {
            synchronized (DivideTable.class) {
                if (columns == null) {
                    columns = new ArrayList<>();
                    //获取元数据
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    //循环列
                    for (int i = 1; i<metaData.getColumnCount();i++){
                        //获取列名
                        String columnName = metaData.getColumnName(i);
                        //获取列类型
                        String columnTypeName = metaData.getColumnTypeName(i);
                        //加入列集合
                        columns.add(Pair.of(columnName, columnTypeName));
                    }
                }
            }
        }
        List<String> values = new ArrayList<>();
        //循环数据行
        while (resultSet.next()){
            List<String> row = new ArrayList<>();
            //挨个循环这行每个数据
            for (int i = 1; i < columns.size(); i++) {
                //获取当前列类型
                String columnTypeName = columns.get(i-1).getRight();
                String val = "";
                if (columnTypeName == "BIGINT") {
                    val = resultSet.getObject(i).toString();
                } else {
                    if (resultSet.getObject(i) == null) {
                        val = "null";
                    } else {
                        val = "'" + resultSet.getObject(i).toString() + "'";
                    }
                }
                row.add(val);
            }
            //加入一条数据
            values.add("(" + row.stream().collect(Collectors.joining(",")) + ")");
        }
        //格式化填入数据
        String sql =String.format("insert into %s (%S) values %w",
                table,
                columns.stream().map(Pair::getLeft).collect(Collectors.joining(",")),
                values.stream().collect(Collectors.joining(",")));
        //提交数据
        connection.createStatement().execute(sql);
    }

}
