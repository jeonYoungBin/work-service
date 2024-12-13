package com.work_service.work;

import com.work_service.work.exception.CustomException;
import com.work_service.work.service.WorkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class WorkApplicationTests {

	@Autowired
	WorkService workService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@DisplayName("작품 등록")
	@Test
	void 작품등록() {
		Long bookId = workService.saveBook("test", true, true, "ALL");
		assertThat(bookId).isNotZero();
	}

	@DisplayName("회원 가입")
	@Test
	void 회원가입() throws CustomException {
		String token = workService.saveMember("test", passwordEncoder.encode("1234"), "홍길동", 15);
		assertThat(token).isNotEmpty();
	}

	@DisplayName("작품 조회")
	@Test
	void 작품조회() throws CustomException {
		//given
		workService.saveMember("test", passwordEncoder.encode("1234"), "홍길동", 15);
		Long bookId = workService.saveBook("test", true, true, "ALL");

		//when
		Long saveViewHistoryId = workService.saveViewHistory(bookId, "test");

		//then
		assertThat(saveViewHistoryId).isNotZero();
	}

	@DisplayName("작품 조회(나이제한)")
	@Test
	void 작품조회_나이제한() throws CustomException {
		//given
		workService.saveMember("test", passwordEncoder.encode("1234"), "홍길동", 15);
		Long bookId = workService.saveBook("test", true, true, "YouthNotAllowed");

		//when, then
		assertThatThrownBy(() -> workService.saveViewHistory(bookId, "test")).isInstanceOf(CustomException.class);
	}

	@DisplayName("작품 구매")
	@Test
	void 작품구매() throws CustomException {
		//given
		workService.saveMember("test", passwordEncoder.encode("1234"), "홍길동", 32);
		Long bookId = workService.saveBook("test", true, true, "YouthNotAllowed");

		//when
		Long savePurchaseHistoryId = workService.savePurchaseHistory(bookId, "test");

		//then
		assertThat(savePurchaseHistoryId).isNotZero();
	}

	@DisplayName("작품 구매(나이제한)")
	@Test
	void 작품구매_나이제한() throws CustomException {
		//given
		workService.saveMember("test", passwordEncoder.encode("1234"), "홍길동", 15);
		Long bookId = workService.saveBook("test", true, true, "YouthNotAllowed");

		//when, then
		assertThatThrownBy(() -> workService.savePurchaseHistory(bookId, "test")).isInstanceOf(CustomException.class);
	}

	@DisplayName("작품 이벤트 변경")
	@Test
	void 작품이벤트변경() throws CustomException {
		//given
		Long bookId = workService.saveBook("test", false, false, "ALL");

		//when
		boolean bookEvent = workService.updateBookEvent(true, bookId);

		//then
		assertThat(bookEvent).isTrue();

	}






}
