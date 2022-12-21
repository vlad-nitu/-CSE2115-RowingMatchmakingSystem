package nl.tudelft.cse.sem.template.user.publishers;

import lombok.Cleanup;
import nl.tudelft.cse.sem.template.user.utils.BaseActivity;
import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

@Service
public class ActivityPublisher {
    private final transient UserUtils userUtils;

    public ActivityPublisher(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public ActivityPublisher() {
        this.userUtils = new UserUtils("http://localhost:8084/");
    }


    /**
     * Create a new activity.
     *
     * @param activity all the information that needs to included in the activity
     * @return BaseActivity object representation of the created activity or null if an error was encountered
     */
    public BaseActivity createActivity(BaseActivity activity) throws Exception {
        @Cleanup
        Response res = userUtils.postRequest("/createActivity", activity);
        return res.getStatus() == 200 ? res.readEntity(new GenericType<BaseActivity>(){}) : null;
    }

}
