import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessLikesAndDislikes {

  private ConcurrentHashMap<String, Integer> likesReceived;
  private ConcurrentHashMap<String, Integer> dislikesReceived;
  private ConcurrentHashMap<String, List<String>> likedUsers;

  public ProcessLikesAndDislikes() {
    likesReceived = new ConcurrentHashMap<>();
    dislikesReceived = new ConcurrentHashMap<>();
    likedUsers = new ConcurrentHashMap<>();
  }

  public void swipe(String leftOrRight, String swiper, String swipee) {
    if(leftOrRight.equals("right")) {
      dislikesReceived.put(swiper, dislikesReceived.getOrDefault(swiper, 0) + 1);
    }else {
      likesReceived.put(swiper, likesReceived.getOrDefault(swiper, 0) + 1);
      if(!likedUsers.containsKey(swiper)) {
        likedUsers.put(swiper, Collections.synchronizedList(new ArrayList<>()));
      }
      likedUsers.get(swiper).add(swipee);
    }
  }

  public Integer getLikes(String swiper) {
    return likesReceived.get(swiper);
  }

  public Integer getDislikes(String swiper) {
    return dislikesReceived.get(swiper);
  }

}