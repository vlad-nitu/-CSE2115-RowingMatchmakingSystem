package nl.tudelft.cse.sem.template.user.repositories;

import nl.tudelft.cse.sem.template.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
