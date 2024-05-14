package com.github.model.validator;

import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.nonNull;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import com.github.model.User;

public class UserDocumentSequenceProvider implements DefaultGroupSequenceProvider<User> {

	@Override
	public List<Class<?>> getValidationGroups(User user) {
		List<Class<?>> groups = new ArrayList<>();
		groups.add(User.class);

		if (hasUserAndUserType(user)) {
			if ("CONSUMER".equalsIgnoreCase(user.getUserType().name())) {
				groups.add(CPFGroup.class);
			} else if ("SELLER".equalsIgnoreCase(user.getUserType().name())) {
				groups.add(CNPJGroup.class);
			}
		}

		return groups;
	}

	private boolean hasUserAndUserType(User user) {
		return nonNull(user) && nonNull(user.getUserType());
	}

}
