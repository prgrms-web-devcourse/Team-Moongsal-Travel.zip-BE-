package shop.zip.travel.presentation.member;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.zip.travel.domain.member.entity.Member;
import shop.zip.travel.domain.member.repository.MemberRepository;
import shop.zip.travel.domain.post.travelogue.DummyGenerator;
import shop.zip.travel.domain.post.travelogue.repository.TravelogueRepository;
import shop.zip.travel.global.security.JwtTokenProvider;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
class MemberMyPageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private TravelogueRepository travelogueRepository;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private Member member;

  @BeforeEach
  void setUp() {
    member = new Member(
      "user@naver.com",
      "password1234!",
      "nickname",
      "2000",
      "ProfileUrlForTest");

    memberRepository.save(member);
    travelogueRepository.save(DummyGenerator.createTravelogue(member));
  }

  @DisplayName("유저는 본인의 개인정보를 조회할 수 있다")
  @Test
  public void get_my_page_info() throws Exception {

    String token = "Bearer " + jwtTokenProvider.createToken(member.getId());

    mockMvc.perform(get("/api/members/my/info")
        .header("AccessToken", token))
      .andExpect(status().isOk())
      .andDo(print())
      .andDo(document("get-my-info",
        responseFields(
          fieldWithPath("email").description("이메일"),
          fieldWithPath("nickname").description("닉네임"),
          fieldWithPath("birthYear").description("생년월일"),
          fieldWithPath("profileImageUrl").description("프로필 이미지 url")
        )
      ));
  }

  @DisplayName("유저는 본인이 작성한 여행기 목록을 조회할 수 있다")
  @Test
  public void get_my_travelogues() throws Exception {
    String token = "Bearer " + jwtTokenProvider.createToken(member.getId());

    mockMvc.perform(get("/api/members/my/travelogues")
        .header("AccessToken", token)
        .queryParam("size", "2")
        .queryParam("page", "0"))
      .andExpect(status().isOk())
      .andDo(print())
      .andDo(document("get-my-travelogues",
        responseFields(
          fieldWithPath("content[].title").description("Travelogue 제목"),
          fieldWithPath("content[].nights").description("몇박 몇일 중 몇박에 해당하는 값"),
          fieldWithPath("content[].days").description("몇박 몇일 중 몇일에 해당하는 값"),
          fieldWithPath("content[].totalCost").description("여행 전체 비용"),
          fieldWithPath("content[].country").description("방문한 나라"),
          fieldWithPath("content[].thumbnail").description("썸네일 링크"),
          fieldWithPath("content[].member.nickname").description("작성자 닉네임"),
          fieldWithPath("content[].member.profileImageUrl").description("작성자 프로필 이미지 링크"),
          fieldWithPath("pageable").description(""),
          fieldWithPath("size").description("요청된 페이지 사이즈"),
          fieldWithPath("number").description("페이지 번호"),
          fieldWithPath("sort.empty").description("데이터가 없는지 여부"),
          fieldWithPath("sort.unsorted").description("데이터가 정렬되어 있지 않은지에 대한 여부"),
          fieldWithPath("sort.sorted").description("데이터가 정렬되어 있는지에 대한 여부"),
          fieldWithPath("numberOfElements").description("조회된 데이터 갯수"),
          fieldWithPath("first").description("첫번째 페이지인지 여부"),
          fieldWithPath("last").description("마지막 페이지인지 여부"),
          fieldWithPath("empty").description("데이터가 없는지 여부")
        )
      ));
  }

}