package shop.zip.travel.domain.bookmark.repository.querydsl;


import java.util.List;
import org.springframework.data.domain.Pageable;
import shop.zip.travel.domain.post.travelogue.dto.res.TravelogueSimpleRes;

public interface BookmarkRepositoryQuerydsl {

  List<TravelogueSimpleRes> getBookmarkedList(Long memberId, Pageable pageable);
}