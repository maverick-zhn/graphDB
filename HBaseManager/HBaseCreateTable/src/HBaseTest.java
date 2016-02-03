/**
 * Created by Servio Palacios on 2/2/16.
 */

import java.io.IOException;

        import org.apache.hadoop.hbase.HBaseConfiguration;
        import org.apache.hadoop.hbase.HColumnDescriptor;
        import org.apache.hadoop.hbase.HTableDescriptor;
        import org.apache.hadoop.hbase.client.HBaseAdmin;
        import org.apache.hadoop.hbase.TableName;

        import org.apache.hadoop.conf.Configuration;


public class HBaseTest {

    public static void main(String[] args) throws IOException {

        // Instantiating configuration class
        Configuration con = HBaseConfiguration.create();

        // Instantiating HbaseAdmin class
        HBaseAdmin admin = new HBaseAdmin(con);

        // Instantiating table descriptor class
        HTableDescriptor tableDescriptor = new
                HTableDescriptor(TableName.valueOf("maverick2"));

        // Adding column families to table descriptor
        tableDescriptor.addFamily(new HColumnDescriptor("family1"));
        tableDescriptor.addFamily(new HColumnDescriptor("family2"));
        tableDescriptor.addFamily(new HColumnDescriptor("family3"));
        tableDescriptor.addFamily(new HColumnDescriptor("family4"));

        // Execute the table through admin
        admin.createTable(tableDescriptor);
        System.out.println(" Table created ");
    }
}