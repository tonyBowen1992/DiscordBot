package com.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.ServletContextResource;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;

@Controller
@SpringBootApplication
public class Main extends ListenerAdapter{

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  int jasonCounter = 0;


  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
    JDABuilder.createLight("Nzk2NTIxNTE3Mzk5NjcwODA0.X_ZIeA.ufsLJQMY4KsB5eq_n2IvUKrv4Qg", GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
            .addEventListeners(new Main())
            .build();
    Timer timer = new Timer();
    timer.schedule(new SayHello(), 0, 600000);
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event)
  {
    User author = event.getAuthor();
    Message msg = event.getMessage();
    String messageTest = msg.getContentRaw().toLowerCase();

    if(messageTest.contains("dick") && !(author.getName().equals("Mr. roBOT"))){

      MessageChannel channel = event.getChannel();

      EmbedBuilder result= new EmbedBuilder();
      result.setTitle("Dick is bad. Dix is lvl99");
      result.setImage("https://tonybowen.me/dix.png");
      event.getChannel().sendMessage(result.build()).queue();


    }
    else if (author.getName().equals("Carlos Pascetti")){
      if(jasonCounter < 5)
      {
        jasonCounter++;
        System.out.println("jc = " + jasonCounter);
      }
      else
      {
        MessageChannel channel = event.getChannel();
        channel.sendMessage("Have you eaten any vegetables today?") /* => RestAction<Message> */
                .queue();
        jasonCounter = 0;
      }
    }
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }

  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

}
