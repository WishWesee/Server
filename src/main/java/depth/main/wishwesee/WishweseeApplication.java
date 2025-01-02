package depth.main.wishwesee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WishweseeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WishweseeApplication.class, args);
	}

}
