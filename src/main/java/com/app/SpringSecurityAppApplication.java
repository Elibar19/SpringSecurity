package com.app;

import com.app.persistence.entity.PermissionEntity;
import com.app.persistence.entity.RoleEntity;
import com.app.persistence.entity.RoleEnum;
import com.app.persistence.entity.UserEntity;
import com.app.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SpringSecurityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAppApplication.class, args);

	}

	@Bean
	CommandLineRunner init(UserRepository userRepository){
		return args -> {
			//CREATE PERMISSIONS
			PermissionEntity createPermission = PermissionEntity.builder()
					.name("CREATE")
					.build();
			PermissionEntity readPermission = PermissionEntity.builder()
					.name("READ")
					.build();

			//CREATE ROLES
			RoleEntity roleAdmin = RoleEntity.builder()
					.roleEnum(RoleEnum.ADMIN)
					.permissionList(Set.of(createPermission, readPermission))
					.build();
			RoleEntity roleUser = RoleEntity.builder()
					.roleEnum(RoleEnum.USER)
					.permissionList(Set.of(readPermission))
					.build();

			//CREATE USERS
			UserEntity userElian = UserEntity.builder()
					.username("elian")
					.password("$2a$10$Rn5dqssiYpaiRiKA30Hi..3ZU38waxJ.JAcoPwV06nSk06S6Vk6ly")
					.roles(Set.of(roleAdmin))
					.build();
			UserEntity userValen = UserEntity.builder()
					.username("valen")
					.password("$2a$10$Rn5dqssiYpaiRiKA30Hi..3ZU38waxJ.JAcoPwV06nSk06S6Vk6ly")
					.roles(Set.of(roleUser))
					.build();

			userRepository.saveAll(List.of(userElian, userValen));
		};
	}

}
