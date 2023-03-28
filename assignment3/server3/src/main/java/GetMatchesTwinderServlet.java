import Model.ChannelPoolGenerator;

import com.google.gson.Gson;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
@WebServlet(name = "GetMatchesTwinderServlet", value = "/GetMatchesTwinderServlet")
public class GetMatchesTwinderServlet extends HttpServlet {
  private final static String QUEUE_NAME1 = "HAPPYPOOL1";
  private final static String QUEUE_NAME2 = "HAPPYPOOL2";
  private ChannelPoolGenerator channelPoolGenerator;
  private static final int startSwiperId = 1;
  private static final int endSwiperId = 5000;
  private static final int startSwipeeId = 1;
  private static final int endSwipeeId = 1000000;
  private final static String table1Name = "MyTable";
  private final static String table2Name = "LikesAndDislikesTable";
  private DynamoDbClient client3;
  @Override
  public void init() throws ServletException {
    try {
      super.init();
      this.channelPoolGenerator = new ChannelPoolGenerator();
      AwsCredentialsProvider credentialsProvider = SystemPropertyCredentialsProvider.create();
      System.setProperty("aws.accessKeyId", "ASIA32MRRSJ3XNJQXLDH");
      System.setProperty("aws.secretAccessKey", "+/pqMWJuKMuxNkKmMYU93SR0+Q7PPtCSRmCp6gAa");
      System.setProperty("aws.sessionToken", "FwoGZXIvYXdzEIP//////////wEaDL0jExt9rX2QcC3ENCLJAWYUME4xjCRzmFl0rwuZ2XEphPExUPYPifWA3tfehzI9QKtAV4YGBj4bvyxDzUYWRWeT5KOaSVRcKgLun/w/++iAASiaNJyzER952DZJMDpIOEcpF5i9d60WBub/KeaFdKjT2IdgEas1ZDr6hcx02M6UdKuC2tgGKH0gbQp+iUSrB0wc4c3V0PIqINeX6QT5ahhk/Y0zMqNozZXC3mLuiWWwuawRnZsOIY9a9j0Iaufo3FcrVvMQpfA+hvi2SBHOLcN63eSx3dKuqCivyoyhBjItO5uYZehA196zBqTkQI/BIIJcxl5LS1gLW9rtCXaUDYd1mJFpiglSNSLIf/5v");

      this.client3 = DynamoDbClient.builder()
          .credentialsProvider(credentialsProvider)
          .region(Region.US_WEST_2)
          .build();
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {

    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    String urlPath = req.getPathInfo();
    res.setContentType("application/json");
    PrintWriter out = res.getWriter();
    if (!isValidURL(urlPath)) {
      out.write("Invalid URL ");
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      out.close();
      return;
    } else {
      String swiper = urlPath.split("/")[1];
      res.setStatus(HttpServletResponse.SC_OK);
      GetItemResponse responseItem = getInfoLikesAndDisLikes(swiper, this.client3);
      if (responseItem.hasItem()){
        AttributeValue personAttribute = responseItem.item().get("person");
        ArrayList<String> personList = new ArrayList<>(Arrays.asList(personAttribute.s().split(",")));
        Map<String, ArrayList<String>> matchListMap = new HashMap<>();
        matchListMap.put("matchlist", personList);
        Gson gson = new Gson();
        String json = gson.toJson(matchListMap);
        out.write(json);
      }
      out.close();
    }
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

  }

  private  boolean isValidURL(String urlPath){
    if (urlPath == null || urlPath.isEmpty()) {
      return false;
    }
    String[] urlParts = urlPath.split("/");
    if (urlParts.length != 2 || !urlParts[0].equals("")){
      return false;
    }
    String userID = urlParts[1];
    int numUserID = Integer.parseInt(userID);
    if (numUserID >= startSwiperId && numUserID <= endSwiperId){
      return true;
    }else {
      return false;
    }
  }

  private  GetItemResponse getInfoLikesAndDisLikes(String swiper, DynamoDbClient client3) {
    Map<String, AttributeValue> keyToFind = new HashMap<>();
    keyToFind.put("swiper", AttributeValue.builder().s(swiper).build());
    GetItemRequest request = GetItemRequest.builder()
        .tableName(table2Name)
        .key(keyToFind)
        .build();
    GetItemResponse response = client3.getItem(request);
    return response;
  }

}
