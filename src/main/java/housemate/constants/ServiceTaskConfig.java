package housemate.constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import housemate.constants.Enum.ServiceConfiguration;
import housemate.repositories.ServiceConfigRepository;

public class ServiceTaskConfig {
	@Autowired
	ServiceConfigRepository servConfRepo;

	public static int DURATION_TIMES_CUSTOMER_SHOULD_NOT_CANCEL_TASK;
	public static int DURATION_TIMES_STAFF_SHOULD_NOT_CANCEL_TASK;
	public static int DURATION_TIMES_ALLOW_BAD_STAFF_PROFICENT_SCORE_APPLY;
	public static int MINUS_POINTS_FOR_CUSTOMER_CANCEL_TASK;
	public static int MINUS_POINTS_FOR_STAFF_CANCEL_TASK;
	public static int BAD_STAFF_PROFICIENT_SCORE;
	public static int DURATION_MINUTES_TIMES_STAFF_START_REPORT = 15;

	@Bean
	public void setting() {
		DURATION_TIMES_CUSTOMER_SHOULD_NOT_CANCEL_TASK = Integer.parseInt(servConfRepo
				.findFirstByConfigType(ServiceConfiguration.DURATION_TIMES_ALLOW_BAD_STAFF_PROFICENT_SCORE_APPLY)
				.getConfigValue());
		DURATION_TIMES_STAFF_SHOULD_NOT_CANCEL_TASK = Integer.parseInt(servConfRepo
				.findFirstByConfigType(ServiceConfiguration.DURATION_TIMES_STAFF_SHOULD_NOT_CANCEL_TASK)
				.getConfigValue());
		DURATION_TIMES_ALLOW_BAD_STAFF_PROFICENT_SCORE_APPLY = Integer.parseInt(servConfRepo
				.findFirstByConfigType(ServiceConfiguration.DURATION_TIMES_ALLOW_BAD_STAFF_PROFICENT_SCORE_APPLY)
				.getConfigValue());
		MINUS_POINTS_FOR_CUSTOMER_CANCEL_TASK = Integer.parseInt(servConfRepo
				.findFirstByConfigType(ServiceConfiguration.MINUS_POINTS_FOR_CUSTOMER_CANCEL_TASK)
				.getConfigValue());
		MINUS_POINTS_FOR_STAFF_CANCEL_TASK = Integer.parseInt(servConfRepo
				.findFirstByConfigType(ServiceConfiguration.MINUS_POINTS_FOR_STAFF_CANCEL_TASK)
				.getConfigValue());
		BAD_STAFF_PROFICIENT_SCORE = Integer.parseInt(servConfRepo
				.findFirstByConfigType(ServiceConfiguration.BAD_STAFF_PROFICIENT_SCORE)
				.getConfigValue());
	}

}
