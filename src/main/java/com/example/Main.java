package com.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.requests.RestAction;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

@Controller
@SpringBootApplication
public class Main extends ListenerAdapter{

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  int jasonCounter = 0;
  boolean playsomegame = false;
  List<String> gameList = new ArrayList<>();


  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
    JDABuilder.createLight("Nzk2NTIxNTE3Mzk5NjcwODA0.X_ZIeA.zITYw6lLGoqvM-d4jlu7iiY57K4", GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
            .addEventListeners(new Main())
            .build();
    Timer timer = new Timer();
    timer.schedule(new SayHello(), 0, 300000);
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event)
  {
    User author = event.getAuthor();
    Message msg = event.getMessage();
    String messageTest = msg.getContentRaw().toLowerCase();
    MessageChannel channel = event.getChannel();

    if(messageTest.contains("dick") && !(author.getName().equals("Mr. roBOT"))){



      EmbedBuilder result= new EmbedBuilder();
      result.setTitle("Dick is bad. Dix is lvl99");
      result.setImage("http://tonybowen.me/dix.png");
      event.getChannel().sendMessage(result.build()).queue();


    }
    else if ((messageTest.contains("playsomegame") || messageTest.contains("iwannaplay") || messageTest.contains("startsomegame")) && !(author.getName().equals("Mr. roBOT"))){
      if(playsomegame = false) {
        playsomegame = true;
        channel.sendMessage("Lets play a game! Type iwannaplay to participate and startsomegame to start") /* => RestAction<Message> */
                .queue();
      }

      if(messageTest.contains("iwannaplay"))
      {
        gameList.add(author.getName());
        channel.sendMessage("Fuck you" + author.getName() + ", but I added your ass to the game anyway.") /* => RestAction<Message> */
                .queue();
      }

      if(messageTest.contains("startsomegame"))
      {
        channel.sendMessage("somegame Participants:") /* => RestAction<Message> */
                .queue();
        for(int i = 0; i < gameList.size(); i++) {
          channel.sendMessage(gameList.get(i)) /* => RestAction<Message> */
                  .queue();
        }
      }





    }
    else if (author.getName().equals("Carlos Pascetti")){
      if(jasonCounter < 5)
      {
        jasonCounter++;
        System.out.println("jc = " + jasonCounter);
      }
      else
      {
        channel.sendMessage("Have you eaten any vegetables today?") /* => RestAction<Message> */
                .queue();
        jasonCounter = 0;
      }
    }
    else if(messageTest.contains("fishing") && !(author.getName().equals("Mr. roBOT"))){

      JDA JDA = channel.getJDA();
      OkHttpClient http = JDA.getHttpClient();
      EmbedBuilder result= new EmbedBuilder();

      okhttp3.Request request = new Request.Builder().url("http://tonybowen.me/fishing.gif").build();
      Response response = null;
      try {
        response = http.newCall(request).execute();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        response = http.newCall(request).execute();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        InputStream body = response.body().byteStream();
        result.setImage("attachment://image.gif"); // Use same file name from attachment
        Response finalResponse = response;
        Response finalResponse1 = response;
        channel.sendMessage(result.build())
                .addFile(body, "image.gif") // Specify file name as "image.png" for embed (this must be the same, its a reference which attachment belongs to which image in the embed)
                .queue(m -> finalResponse.close(), error -> { // Send message and close response when done
                  finalResponse1.close();
                  RestAction.getDefaultFailure().accept(error);
                });
      } catch (Throwable ex) {
        response.close();
        if (ex instanceof Error) throw (Error) ex;
        else throw (RuntimeException) ex;
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
