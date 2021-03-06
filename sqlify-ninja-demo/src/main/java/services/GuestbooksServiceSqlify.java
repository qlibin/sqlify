package services;

import com.google.inject.Inject;
import java.util.List;
import models.Guestbook;
import ninja.jdbc.NinjaDatasources;
import org.sqlify.Database;
import org.sqlify.Sqlify;
import org.sqlify.resultparser.ListResultParser;
import org.sqlify.resultparser.SingleResultParser;

public class GuestbooksServiceSqlify {

  private final Database database;

  @Inject
  public GuestbooksServiceSqlify(NinjaDatasources ninjaDatasources) {
    database = Database.from(ninjaDatasources.getDatasource("default").getDataSource());
  }

  public List<Guestbook> listGuestBookEntries() {
    return database.withConnection(connection -> 
      Sqlify.sql(
        "SELECT id, email, content FROM guestbooks")
        .parseResultWith(ListResultParser.of(Guestbook.class))
        .executeSelect(connection)
    );
  }

  public Long createGuestbook(Guestbook guestbook) {
    return database.withTransaction(connection -> 
      Sqlify.sql(
        "INSERT INTO guestbooks (email, content) VALUES ({email}, {content})")
        .withParameter("email", guestbook.email)
        .withParameter("content", guestbook.content)
        .parseResultWith(SingleResultParser.of(Long.class))
        .executeUpdateAndReturnGeneratedKey(connection)
    );
  }

}
