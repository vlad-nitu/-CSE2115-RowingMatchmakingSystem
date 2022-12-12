package nl.tudelft.cse.sem.template.user.repositories;

import nl.tudelft.cse.sem.template.user.domain.User;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
