/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.TestApplicationContextInitializer;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.google.cloud.healthcare.fdamystudies.matchers.HasLastName.hasLastName;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
public class UserDetailsRepositoryTest {

	@Resource
	private UserDetailsRepository userDetailsRepository;

	@Test
	public void FindsUsersWithLastName() {
		UserDetailsBO user1 = UserDetailsBO.builder()
				.userId("user_id")
				.email("email1@example.com")
				.firstName("Given name")
				.lastName("Surname")
				._ts(new GregorianCalendar(2000, 1, 1).getTime())
				.verificationDate(new GregorianCalendar(2000, 1, 2).getTime())
				.codeExpireDate(LocalDateTime.of(2000, Month.JUNE, 1, 20, 0, 0))
				.build();
		UserDetailsBO user2 = UserDetailsBO.builder()
				.userId("user_id")
				.email("email2@example.com")
				.firstName("Given name 2")
				.lastName("Surname")
				._ts(new GregorianCalendar(2000, 1, 1).getTime())
				.verificationDate(new GregorianCalendar(2000, 1, 2).getTime())
				.codeExpireDate(LocalDateTime.of(2000, Month.JUNE, 1, 20, 0, 0))
				.build();
		UserDetailsBO user3 = UserDetailsBO.builder()
				.userId("user_id")
				.email("email2@example.com")
				.firstName("Given name 2")
				.lastName("NotSurname")
				._ts(new GregorianCalendar(2000, 1, 1).getTime())
				.verificationDate(new GregorianCalendar(2000, 1, 2).getTime())
				.codeExpireDate(LocalDateTime.of(2000, Month.JUNE, 1, 20, 0, 0))
				.build();
		userDetailsRepository.save(user1);
		userDetailsRepository.save(user2);
		userDetailsRepository.save(user3);

		List<UserDetailsBO> users = userDetailsRepository.findByLastName("Surname");
		assertThat(users,
				   hasItems(hasLastName("Surname"), hasLastName("Surname")));
	}
}
