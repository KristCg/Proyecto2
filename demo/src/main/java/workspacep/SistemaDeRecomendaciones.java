package workspacep;

import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;

public class SistemaDeRecomendaciones implements TransactionWork<LinkedList<String>> {
    private String user;

    public User(String username) {
        this.user = user;

    }

    public String getUsername() {
        return username;
    }




}
