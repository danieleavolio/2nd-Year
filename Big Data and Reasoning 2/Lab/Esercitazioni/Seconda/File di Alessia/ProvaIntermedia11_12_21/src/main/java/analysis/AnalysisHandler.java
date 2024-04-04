package analysis;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.example.TableSchema;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

public class AnalysisHandler {

    private static final Configuration conf = new Configuration();

    public static void main(String[] args) {
        conf.set("hbase.zookeeper.quorum", "master,slave2,slave4");
        System.out.println("Total income of February (precise month) " + String.valueOf(totalIncomeForMonth(2)));
        groupIncomeByYear();
        customerOutcomeByCategory("2355af7c75e7c98b43a87b2a7f210dc5");
        analyzeMonths();
    }


    public static float totalIncomeForMonth(int month) {
        // Select the total income for a precise month

        // We have to start always initializing a scan object, in this way we can query the db
        Scan scan = new Scan();
        // We work always with string, so we convert the month in a string
        String monthString = String.valueOf(month);
        if (month < 10)
            monthString = "0" + monthString;
        // Given the fact that our key is composed by customer_id+timestamp, we have to make explicitly clear in which
        // part of the string the month is located
        monthString = "-" + monthString + "-";
        // We can use filter class to filter what we have in the db and get just what we want
        RowFilter filter = new RowFilter(CompareOperator.EQUAL, new SubstringComparator(monthString));
        scan.setFilter(filter);
        // We try to connect to the database
        Float totalIncome;
        try {
            Connection connection = ConnectionFactory.createConnection(conf);
            // Taking the table that we want to query
            Table ordersTable = connection.getTable(TableName.valueOf(TableSchema.ORDERS));
            // Taking query results
            ResultScanner results = ordersTable.getScanner(scan);
            totalIncome = 0.0f;
            for (Result result : results) {
                // Given the fact that we already know what we expect from the query, we can directly access to the value of the result
                byte[] partialIncome = result.getValue(TableSchema.STATUS_FAMILY.getBytes(), TableSchema.AMOUNT_QUALIFIER.getBytes());
                totalIncome += Bytes.toFloat(partialIncome);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return totalIncome;
    }

    public static void groupIncomeByYear() {
        // For each year select the total income

        try {
            Connection connection = ConnectionFactory.createConnection(conf);
            Table orderTable = connection.getTable(TableName.valueOf(TableSchema.ORDERS));
            // If we want to skip rows (orders) that are already cancelled
            // SingleColumnValueFilter filter = new SingleColumnValueFilter(TableSchema.STATUS_FAMILY, TableSchema.STATUS_QUALIFIER, CompareOperator.NOT_EQUAL, "cancelled".getBytes());
            Scan scanner = new Scan();
            ResultScanner results = orderTable.getScanner(scanner);
            Map<Integer, Float> incomesPerYear = new HashMap<>();
            // Every result is an order, and we want to sum up the total income of every order in a given year
            for(Result result: results) {
                String[] splitResult = Bytes.toString(result.getRow()).split("-");
                Integer year = Integer.parseInt(splitResult[splitResult.length-1]);
                byte[] partialIncome = result.getValue(TableSchema.STATUS_FAMILY.getBytes(), TableSchema.AMOUNT_QUALIFIER.getBytes());
                if(incomesPerYear.containsKey(year)) {
                   incomesPerYear.put(year, incomesPerYear.get(year)+Bytes.toFloat(partialIncome));
                }
                else incomesPerYear.put(year, Bytes.toFloat(partialIncome));
            }
            // Now we return the total income for each year
            for (Integer currentYear: incomesPerYear.keySet()) {
                System.out.println("Total income for year "+currentYear+": "+ incomesPerYear.get(currentYear));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void customerOutcomeByCategory(String customer_id) {
        // Select for a particular user the total amount spent for each category
        try {
            Connection connection = ConnectionFactory.createConnection(conf);
            Table ordersTable = connection.getTable(TableName.valueOf(TableSchema.ORDERS));
            RowFilter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(customer_id));
            Scan scanner = new Scan();
            scanner.setFilter(filter);
            ResultScanner results = ordersTable.getScanner(scanner);
            Map<String, Float> amountSpentPerCategory = new HashMap<>();
            for(Result result: results) {
                // Given the fact that the result of our query is a list of products [PRODUCTS_FAMILY -> every qualifier is a product]
                // we need a NavigableMap to iterate over every qualifier
                NavigableMap<byte[], byte[]> products = result.getFamilyMap(TableSchema.PRODUCTS_FAMILY.getBytes());
                for(Map.Entry<byte[], byte[]> pair: products.entrySet()) {
                    String[] values = Bytes.toString(pair.getValue()).split("-");
                    if(amountSpentPerCategory.containsKey(values[2])) {
                        amountSpentPerCategory.put(values[2], amountSpentPerCategory.get(values[2])+(Float.parseFloat(values[1]) * Float.parseFloat(values[0])));
                    }
                    else amountSpentPerCategory.put(values[2],(Float.parseFloat(values[1]) * Float.parseFloat(values[0])));
                }
            }
            for(String category: amountSpentPerCategory.keySet()) {
                System.out.println("Amount spent per category " + category + ": " + amountSpentPerCategory.get(category));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void analyzeMonths() {
        // Select the count of orders and the most popular category for each month

        try {
            Connection connection = ConnectionFactory.createConnection(conf);
            Table orderTable = connection.getTable(TableName.valueOf(TableSchema.ORDERS));
            Scan scanner = new Scan();
            ResultScanner results = orderTable.getScanner(scanner);
            // The result of my query is a list of rows
            int countOrders = 0;
            Map<String, Map<String, Integer>> numOrdersPerCategoryPerMonth = new HashMap<>();
            for(Result result: results) {
                countOrders++;
                String[] rowId = Bytes.toString(result.getRow()).split("-");
                String month = rowId[rowId.length-2];
                boolean monthContained = numOrdersPerCategoryPerMonth.containsKey(month);

                NavigableMap<byte[], byte[]> products = result.getFamilyMap(TableSchema.PRODUCTS_FAMILY.getBytes());
                for (Map.Entry<byte[], byte[]> pair: products.entrySet()) {
                    String[] valueQualifier = Bytes.toString(pair.getValue()).split("-");
                    Integer numProducts = Integer.parseInt(valueQualifier[0]);
                    String category = valueQualifier[2];

                    if(!monthContained) {
                        numOrdersPerCategoryPerMonth.put(month, new HashMap<String, Integer>());
                    }
                    if(!numOrdersPerCategoryPerMonth.get(month).containsKey(category)) {
                        numOrdersPerCategoryPerMonth.get(month).put(category, 0);
                    }
                    numOrdersPerCategoryPerMonth.get(month).put(category, numOrdersPerCategoryPerMonth.get(month).get(category)+numProducts);
                }
            }
            System.out.println("Total number of orders: " + countOrders);
            System.out.println("Most popular category for each month:");
            showMostPopularCategory(numOrdersPerCategoryPerMonth);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showMostPopularCategory(Map<String, Map<String, Integer>> numOrdersPerCategoryPerMonth) {
        for (String month: numOrdersPerCategoryPerMonth.keySet()) {
            int maxCount = 0;
            String currentMaxCategory = "";
            for(String category: numOrdersPerCategoryPerMonth.get(month).keySet()) {
                if(numOrdersPerCategoryPerMonth.get(month).get(category) >= maxCount)
                    currentMaxCategory = category;
            }
            System.out.println("Month " + month + " : " + currentMaxCategory);
        }
    }

}
