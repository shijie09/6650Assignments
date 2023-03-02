

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProcessSwipees {

  private ConcurrentHashMap<String, Integer> likesReceived;
  private ConcurrentHashMap<String, Integer> dislikesReceived;
  private ConcurrentHashMap<String, List<String>> likedUsers;

  public ProcessSwipees() {
    likesReceived = new ConcurrentHashMap<>();
    dislikesReceived = new ConcurrentHashMap<>();
    likedUsers = new ConcurrentHashMap<>();
  }

  public void swipe(String leftOrRight, String swiper, String swipee) {
    // left means dislike
    if(leftOrRight.equals("left")) {
      dislikesReceived.put(swipee, dislikesReceived.getOrDefault(swipee, 0) + 1);
    }else {
      // right means like
      likesReceived.put(swipee, likesReceived.getOrDefault(swipee, 0) + 1);
      if(!likedUsers.containsKey(swiper)) {
        likedUsers.put(swiper, Collections.synchronizedList(new ArrayList<>()));
        likedUsers.get(swiper).add(swipee);
      }
    }
  }

//  public Integer getLikes(String swipee) {
//    return likesReceived.get(swipee);
//  }
//
//  public Integer getDislikes(String swipee) {
//    return dislikesReceived.get(swipee);
//  }

  public List<String> getTop100LikedUsers(String swiper) {
    List<String> liked = new ArrayList<>();
    if(liked.size() <= 100)
      liked = likedUsers.get(swiper);
    else {
      for(int i = 0; i < 100; i++) {
        liked.add(likedUsers.get(swiper).get(i));
      }
    }
    return liked;
  }
}
