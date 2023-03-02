import Model.ChannelPoolGenerator;
import Model.Info;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
@WebServlet(name = "TwinderServlet", value = "/TwinderServlet")
public class TwinderServlet extends HttpServlet {
  private final static String QUEUE_NAME1 = "HAPPYPOOL1";
  private final static String QUEUE_NAME2 = "HAPPYPOOL2";
  private ChannelPoolGenerator channelPoolGenerator;
  @Override
  public void init() throws ServletException {
    try {
      super.init();
      this.channelPoolGenerator = new ChannelPoolGenerator();
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

  }

  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    String urlPath = req.getPathInfo();
    PrintWriter writer = res.getWriter();
    String inputString = readRequest(req);
    if(!isValidURL(urlPath) || !isValidBody(inputString)) {
      if (!isValidURL(urlPath)){
        writer.write("Invalid URL");
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        writer.close();
        return;
      }
      if (!isValidBody(inputString)){
        writer.write("Invalid requestBody");
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        writer.close();
        return;
      }

    }else {
      writer.write("Good request Body! Congratulations!");
      res.setStatus(HttpServletResponse.SC_CREATED);
      writer.close();
      Info input;
          try {
            input = processRequest(inputString);
            try {
              String[] urlPartsQueue = urlPath.split("/");
              String action = urlPartsQueue[1];
              String inputQueueString = action + '/' + input.getSwiper() + '/' + input.getSwipee()  + '/' + input.getComment();
              Channel channel = channelPoolGenerator.takeChannel();

              channel.queueDeclare(QUEUE_NAME1, false, false, false, null);
              channel.basicPublish("", QUEUE_NAME1, (BasicProperties)null, inputQueueString.getBytes(StandardCharsets.UTF_8));

              channel.queueDeclare(QUEUE_NAME2, false, false, false, null);
              channel.basicPublish("", QUEUE_NAME2, (BasicProperties)null, inputQueueString.getBytes(StandardCharsets.UTF_8));

              this.channelPoolGenerator.add(channel);
              res.setStatus(HttpServletResponse.SC_CREATED);
            } catch (IOException | InterruptedException e) {
              e.printStackTrace();
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
    }
  }
  private  boolean isValidURL(String urlPath){
    if (urlPath == null || urlPath.isEmpty()) {
      return false;
    }
    String[] urlParts = urlPath.split("/");
    if (urlParts.length != 2 || !urlParts[0].equals("")){
      return false;
    }
    if (urlParts[1].equals("left") || urlParts[1].equals("right") ){
      return true;
    }else {
      return false;
    }


  }
  protected String readRequest(HttpServletRequest request)
      throws ServletException, IOException {
    try {
      StringBuilder sb = new StringBuilder();
      String s;
      while ((s = request.getReader().readLine()) != null) {
        sb.append(s);
      }
      return sb.toString();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "";
  }
  private boolean isValidBody(String reqString) {
    Gson gson = new Gson();
    Info reqBody = new Info();
    try {
      reqBody = (Info) gson.fromJson(reqString, Info.class);
      return reqBody.getSwipee()!= null && reqBody.getSwiper() != null && reqBody.getComment()!=null;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  protected Info processRequest(String reqString)
      throws Exception {
    Gson gson = new Gson();
    Info reqBody = new Info();

    try {

      reqBody = (Info) gson.fromJson(reqString, Info.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return reqBody;
  }

}
